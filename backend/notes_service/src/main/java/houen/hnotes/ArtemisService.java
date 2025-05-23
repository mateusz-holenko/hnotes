package houen.hnotes;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

@Service
public class ArtemisService {

  private static final String VerificationQueueName = "verification.queue";
  private static final String VerificationResultQueueName = "verification-result.queue";

  private final JmsTemplate brokerTemplate;
  private final NotesStore notesStore;

  private final Logger logger;

  private final Counter processedCounter;
  private final Counter sentCounter;
  private final Counter errorsCounter;

  @Autowired
  public ArtemisService(@Qualifier("verificationJmsTemplate") JmsTemplate jmsTemplate, @Lazy NotesStore notesStore, MeterRegistry meterRegistry) {
    this.brokerTemplate = jmsTemplate;
    this.notesStore = notesStore;

    logger = LoggerFactory.getLogger(NotesRestController.class);

    processedCounter = Counter.builder("artemis.messagesProcessed")
      .baseUnit("number of messages")
      .register(meterRegistry);

    sentCounter = Counter.builder("artemis.messagesSent")
      .baseUnit("number of messages")
      .register(meterRegistry);

    errorsCounter = Counter.builder("artemis.errors")
      .baseUnit("number of messages")
      .register(meterRegistry);
  }

  public void send(NoteVerificationRequest request) {
    logger.info("Sending verification request for note #{}", request.getId());
    brokerTemplate.send(ArtemisService.VerificationQueueName, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        var requestAsJSON = new JSONObject(request);
        return session.createTextMessage(requestAsJSON.toString());
      }
    });
    sentCounter.increment();
  }

  @JmsListener(destination = ArtemisService.VerificationResultQueueName)
  public void processMessage(String content) {
    var result = NoteVerificationResult.fromJSON(content);
    if(result == null) {
      logger.error("Received unexpected Note-Verification-Result message: >>{}<<", content);
      errorsCounter.increment();
      return;
    }

    switch(result.getResult()) {
      case "accepted":
        logger.info("Accepting note #{}", result.getId());
        notesStore.acceptNote(result.getId());
        break;
      case "rejected":
        logger.info("Rejecting note #{}", result.getId());
        notesStore.rejectNote(result.getId());
      default:
        // TODO: this should be handled inside NoteVerificaitonResult itself while parsing JSON
        logger.error("Unexpected Note-Verification-Result status: {}", result.getResult());
        errorsCounter.increment();
        return;
    }

    processedCounter.increment();
  }
}

