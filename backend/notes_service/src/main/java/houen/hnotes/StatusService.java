package houen.hnotes;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

@Service
public class StatusService {

  private static final Logger logger = LoggerFactory.getLogger(StatusService.class);
  private static final String StatusUpdatesTopic = "status.topic";

  private final JmsTemplate brokerTemplate;

  private final Counter sentCounter;

  @Autowired
  public StatusService(@Qualifier("statusJmsTemplate") JmsTemplate jmsTemplate, MeterRegistry meterRegistry) {
    this.brokerTemplate = jmsTemplate;

    sentCounter = Counter.builder("status.messagesSent")
      .baseUnit("number of messages")
      .register(meterRegistry);
  }

  public void sendStatusUpdate(StatusUpdate statusUpdate) {
    logger.info("Sending status update: {}", statusUpdate.toString());
    brokerTemplate.send(StatusService.StatusUpdatesTopic, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        var requestAsJSON = new JSONObject(statusUpdate);
        return session.createTextMessage(requestAsJSON.toString());
      }
    });
    sentCounter.increment();
  }
}

