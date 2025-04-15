package houen.hnotes;

import java.util.ArrayList;

import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;

// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
// import org.junit.runner.RunWith;
// import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest()
@ExtendWith(OutputCaptureExtension.class)
// @RunWith(SpringRunner.class)
// @DataJpaTest
public class ArtemisServiceTests {

  @Autowired
  private NotesStore notesStore;

  @Autowired
  private RestTemplate restTemplate;

  private MockRestServiceServer mockServer;

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private ArtemisService artemisService;

  @Test
  @Timeout(1)
  public void sendMessageToQueue_ShouldWork() throws Exception {
    artemisService.send(new NoteVerificationRequest(0, "title", "content"));
    var message = jmsTemplate.receive("verification.queue");

    Assertions.assertNotNull(message);
    Assertions.assertEquals(ActiveMQTextMessage.class, message.getClass());

    var asJson = new JSONObject(((ActiveMQTextMessage)message).getText());
    Assertions.assertEquals(0, asJson.getInt("id"));
    Assertions.assertEquals("title", asJson.getString("title"));
    Assertions.assertEquals("content", asJson.getString("content"));
  }

  @Test
  public void receiveMessage_WithProperContent_ShouldWork(CapturedOutput output) throws Exception {
    jmsTemplate.convertAndSend("verification-result.queue", new NoteVerificationResult(0, "accepted").toJSONString());
    await().untilAsserted(() -> assertThat(output).contains("Accepting note #0"));
  }

  @Test
  public void receiveMessage_Empty_ShouldDiscard(CapturedOutput output) throws Exception {
    jmsTemplate.convertAndSend("verification-result.queue", "{}");
    await().untilAsserted(() -> assertThat(output).contains("Received unexpected Note-Verification-Result message: >>{}<<"));
  }

  @Test
  public void receiveMessage_WithProperContent_ShouldAcceptNote(CapturedOutput output) throws Exception {
    mockServer = MockRestServiceServer.createServer(restTemplate);
    mockServer
      .expect(requestTo("http://verification/verificator"))
      .andExpect(method(HttpMethod.POST))
      .andRespond(withSuccess("{\"status\":\"accepted\",\"length\":120}", MediaType.APPLICATION_JSON));

    // TODO: this should not be 16!, we need to make it independent from previous tests
    mockServer
      .expect(requestTo("http://elastic/note/_doc/16"))
      .andExpect(method(HttpMethod.POST))
      .andRespond(withSuccess());

    notesStore.clear();
    notesStore.addNote(new Note("title", "content"));

    var currentNotes = new ArrayList<Note>();
    notesStore.getNotes(null, 100, 0, "").forEach(currentNotes::add);

    Assertions.assertEquals(1, currentNotes.size());

    var retrievedNote = currentNotes.get(0);
    Assertions.assertEquals(16, retrievedNote.getId());
    Assertions.assertEquals(NoteStatus.UNVERIFIED, retrievedNote.getStatus());

    jmsTemplate.convertAndSend("verification-result.queue", new NoteVerificationResult(16, "accepted").toJSONString());
    await().untilAsserted(() -> assertThat(output).contains("Accepting note #16"));
    // TODO: there might still be a race condition, as the "Accepting note" msg is written BEFORE actually touching the notes store; fixit

    currentNotes.clear();
    notesStore.getNotes(null, 100, 0, "").forEach(currentNotes::add);

    Assertions.assertEquals(1, currentNotes.size());

    retrievedNote = currentNotes.get(0);
    Assertions.assertEquals(16, retrievedNote.getId());
    Assertions.assertEquals(NoteStatus.ACCEPTED, retrievedNote.getStatus());
  }
}
