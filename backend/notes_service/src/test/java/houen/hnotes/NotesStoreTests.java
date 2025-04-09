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
public class NotesStoreTests {

  @Autowired
  private NotesStore notesStore;

  @Autowired
  private RestTemplate restTemplate;

  private MockRestServiceServer mockServer;

  @BeforeEach
  public void init() {
    mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void testGettingNotes() throws Exception {
    Assertions.assertNotNull(notesStore);

    for(int i = 1; i <= 15; i++) {
      mockServer
        .expect(requestTo("http://verification/verificator"))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess("{\"status\":\"accepted\",\"length\":120}", MediaType.APPLICATION_JSON));

      mockServer
        .expect(requestTo("http://elastic/note/_doc/" + i))
        .andExpect(method(HttpMethod.POST))
        .andRespond(withSuccess());
    }

    notesStore.createDummyNotes();
    var notes = new ArrayList<Note>();
    notesStore.getNotes(null, 3, 1, "").forEach(notes::add);

    Assertions.assertEquals(3, notes.size());
  }

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private ArtemisService artemisService;

  @Test
  @Timeout(1)
  public void testMessageBroker() throws Exception {
    // jmsTemplate.convertAndSend("verification-result.queue", "nope");

    artemisService.send(new NoteVerificationRequest(0, "title", "content"));
    var message = jmsTemplate.receive("verification.queue");

    Assertions.assertNotNull(message);
    Assertions.assertEquals(ActiveMQTextMessage.class, message.getClass());

    var text = ((ActiveMQTextMessage)message).getText();
    var asJson = new JSONObject(text);
    Assertions.assertEquals(0, asJson.getInt("id"));
    Assertions.assertEquals("title", asJson.getString("title"));
    Assertions.assertEquals("content", asJson.getString("content"));
  }
}
