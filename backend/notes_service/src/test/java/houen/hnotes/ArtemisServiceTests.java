package houen.hnotes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.micrometer.core.instrument.MeterRegistry;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
public class ArtemisServiceTests {

  private static Logger logger = LoggerFactory.getLogger(ArtemisServiceTests.class);

  @MockitoBean
  private ElasticSearchService searchService;

  @MockitoBean
  private NotesStore notesStore;

  @Autowired
  @Qualifier("verificationJmsTemplate")
  private JmsTemplate jmsTemplate;

  @Autowired
  private ArtemisService artemisService;

  @Autowired
  private MeterRegistry meterRegistry;

  @BeforeEach
  private void initEach(TestInfo testInfo) throws Exception {
    // TODO: this shouldn't be here; eliminate pls
    var f = artemisService.getClass().getDeclaredField("notesStore");
    f.setAccessible(true);
    f.set(artemisService, notesStore);

    logger.info("--- Starting test: {} ---", testInfo.getDisplayName());
  }

  @Test
  @Timeout(1)
  public void sendMessageToQueue_ShouldWork() throws Exception {
    artemisService.send(new NoteVerificationRequest("0", "title", "content"));
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
    jmsTemplate.convertAndSend("verification-result.queue", new NoteVerificationResult("0", "accepted").toJSONString());
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
    jmsTemplate.convertAndSend("verification-result.queue", new NoteVerificationResult("accepted_note_id", "accepted").toJSONString());
    await().untilAsserted(() -> assertThat(meterRegistry.counter("artemis.messagesProcessed").count()).isEqualTo(1));

    verify(notesStore, times(1)).acceptNote("accepted_note_id");
    verify(notesStore, times(0)).rejectNote("accepted_note_id");
  }

  @Test
  public void receiveMessage_WithBlockedContent_ShouldRejectNote(CapturedOutput output) throws Exception {
    jmsTemplate.convertAndSend("verification-result.queue", new NoteVerificationResult("rejected_note_id", "rejected").toJSONString());
    await().untilAsserted(() -> assertThat(meterRegistry.counter("artemis.messagesProcessed").count()).isEqualTo(1));

    verify(notesStore, times(0)).acceptNote("rejected_note_id");
    verify(notesStore, times(1)).rejectNote("rejected_note_id");
  }
}
