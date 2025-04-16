package houen.hnotes;

import java.util.ArrayList;

import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;

import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.*;


@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureTestDatabase
public class ArtemisServiceTests {

  private static MockRestServiceServer mockServer;

  @MockitoBean
  private ElasticSearchService searchService;

  @Autowired
  private NotesStore notesStore;

  @Autowired
  private JmsTemplate jmsTemplate;

  @Autowired
  private ArtemisService artemisService;

  @BeforeAll
  private static void initAll(@Autowired RestTemplate restTemplate) {
    mockServer = MockRestServiceServer.createServer(restTemplate);
  }

  @BeforeEach
  private void initEach(TestInfo testInfo) {
    notesStore.clear();
    var logger = LoggerFactory.getLogger(ArtemisServiceTests.class);
    logger.info("--- Starting test: {} ---", testInfo.getDisplayName());
  }

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
  public void receiveMessage_NonJson_ShouldDiscard(CapturedOutput output) throws Exception {
    jmsTemplate.convertAndSend("verification-result.queue", "this-is-not-a-json-at-all");
    await().untilAsserted(() -> assertThat(output).contains("Received unexpected Note-Verification-Result message: >>this-is-not-a-json-at-all<<"));
  }

  @Test
  public void receiveMessage_WithProperContent_ShouldAcceptNote(CapturedOutput output) throws Exception {
    notesStore.addNote(new Note("title", "content"));

    var currentNotes = new ArrayList<Note>();
    notesStore.getNotes(null, 100, 0, "").forEach(currentNotes::add);

    Assertions.assertEquals(1, currentNotes.size());

    var retrievedNote = currentNotes.get(0);
    final Integer retrievedNoteId = retrievedNote.getId();

    Assertions.assertEquals("title", retrievedNote.getTitle());
    Assertions.assertEquals("content", retrievedNote.getContent());
    Assertions.assertEquals(NoteStatus.UNVERIFIED, retrievedNote.getStatus());

    jmsTemplate.convertAndSend("verification-result.queue", new NoteVerificationResult(retrievedNoteId, "accepted").toJSONString());
    await().untilAsserted(() -> assertThat(output).contains(String.format("Accepting note #%d", retrievedNoteId)));
    // TODO: there might still be a race condition, as the "Accepting note" msg is written BEFORE actually touching the notes store; fixit

    currentNotes.clear();
    notesStore.getNotes(null, 100, 0, "").forEach(currentNotes::add);

    Assertions.assertEquals(1, currentNotes.size());

    retrievedNote = currentNotes.get(0);
    Assertions.assertEquals("title", retrievedNote.getTitle());
    Assertions.assertEquals("content", retrievedNote.getContent());
    Assertions.assertEquals(NoteStatus.ACCEPTED, retrievedNote.getStatus());
  }

  @Test
  public void receiveMessage_WithBlockedContent_ShouldRejectNote(CapturedOutput output) throws Exception {
    notesStore.addNote(new Note("title", "content"));

    var currentNotes = new ArrayList<Note>();
    notesStore.getNotes(null, 100, 0, "").forEach(currentNotes::add);

    Assertions.assertEquals(1, currentNotes.size());

    var retrievedNote = currentNotes.get(0);
    var retrievedNoteId = retrievedNote.getId();
    Assertions.assertEquals("title", retrievedNote.getTitle());
    Assertions.assertEquals("content", retrievedNote.getContent());
    Assertions.assertEquals(NoteStatus.UNVERIFIED, retrievedNote.getStatus());

    jmsTemplate.convertAndSend("verification-result.queue", new NoteVerificationResult(retrievedNoteId, "rejected").toJSONString());
    await().untilAsserted(() -> assertThat(output).contains(String.format("Rejecting note #%d", retrievedNoteId)));
    // TODO: there might still be a race condition, as the "Rejecting note" msg is written BEFORE actually touching the notes store; fixit

    currentNotes.clear();
    notesStore.getNotes(null, 100, 0, "").forEach(currentNotes::add);

    Assertions.assertEquals(1, currentNotes.size());

    retrievedNote = currentNotes.get(0);
    Assertions.assertEquals("title", retrievedNote.getTitle());
    Assertions.assertEquals("content", retrievedNote.getContent());
    Assertions.assertEquals(NoteStatus.REJECTED, retrievedNote.getStatus());
  }
}
