package houen.hnotes;

import org.springframework.stereotype.Service;

import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.Session;
import jakarta.jms.JMSException;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONObject;

@Service
public class ArtemisService {

  private static final String VerificationQueueName = "verification.queue";
  private static final String VerificationResultQueueName = "verification-result.queue";

  private final JmsTemplate t;

  @Autowired
  public ArtemisService(JmsTemplate jmsTemplate) {
    this.t = jmsTemplate;
  }

  public void send(String text) {
    t.send(ArtemisService.VerificationQueueName, new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(text);
      }
    });
  }

  @JmsListener(destination = ArtemisService.VerificationResultQueueName)
  public void processMessage(String content) {
    var logger = LoggerFactory.getLogger(NotesRestController.class);

    // TODO: this should be done by some generic STOMP handler
    var str = Stream.of(content.split(","))
      .map(x -> (new Character((char)Integer.parseInt(x)).toString()))
      .collect(Collectors.joining());
    var json = new JSONObject(str);

    logger.error("received!!! " + json);
  }
}

