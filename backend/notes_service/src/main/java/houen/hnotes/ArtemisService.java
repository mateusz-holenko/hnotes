package houen.hnotes;

import java.io.Serializable;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

@Service
public class ArtemisService {

  private static final String VerificationQueueName = "verification.queue";
  private static final String VerificationResultQueueName = "verification-result.queue";

  private final JmsTemplate t;

  @Autowired
  public ArtemisService(JmsTemplate jmsTemplate) {
    this.t = jmsTemplate;
  }

  public void send(NoteVerificationRequest request) {
    t.send(ArtemisService.VerificationQueueName, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        var requestAsJSON = new JSONObject(request);
        return session.createTextMessage(requestAsJSON.toString());
      }
    });
  }

  @JmsListener(destination = ArtemisService.VerificationResultQueueName)
  public void processMessage(String content) {
    var logger = LoggerFactory.getLogger(NotesRestController.class);
    logger.error("received raw >" + content + "<");

    var json = new JSONObject(content);

    logger.error("received decoded >" + json + "<");
  }
}

