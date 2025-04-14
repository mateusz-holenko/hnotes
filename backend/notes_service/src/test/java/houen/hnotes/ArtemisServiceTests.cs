package houen.hnotes;

import java.util.ArrayList;

import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;

@SpringBootTest()
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
  public void receiveMessage_WithProperContent_ShouldWork() throws Exception {
    Assertions.assertEquals(0, artemisService.NumberOfProcessedMessages);

    jmsTemplate.convertAndSend("verification-result.queue", "{\"id\":0,\"result\":\"accepted\"}");

    Assertions.assertEquals(1, artemisService.NumberOfProcessedMessages);
  }

  @Test
  public void receiveMessage_Empty_ShouldWork() throws Exception {
    Assertions.assertEquals(0, artemisService.NumberOfProcessedMessages);

    jmsTemplate.convertAndSend("verification-result.queue", "{}");

    Assertions.assertEquals(1, artemisService.NumberOfProcessedMessages);
  }

  @Test
  public void receiveMessage_WithProperContent_ShouldAcceptNote() throws Exception {
    var n = new Note();
    n.setTitle("title");
    n.setContent("content");
    notesStore.addNote(n);

    var currentNotes = notesStore.getNotes(null, 100, 1, "").toArray();

    Assertions.assertEquals(1, currentNotes.length);
    Assertions.assertEquals(0, currentNotes[0].getId());
    Assertions.assertEquals(NoteStatus.UNVERIFIED, currentNotes[0].getStatus();
  }
}
