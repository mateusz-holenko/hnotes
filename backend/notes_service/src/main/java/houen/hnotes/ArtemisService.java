package houen.hnotes;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.Session;
import jakarta.jms.JMSException;

@Service
public class ArtemisService {

  private final JmsTemplate t;

  @Autowired
  public ArtemisService(JmsTemplate jmsTemplate) {
    this.t = jmsTemplate;
  }

  public void send(String text) {
    t.send("testdest", new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(text);
      }
    });
  }

  public String receive() {
    try {
    var msg = (TextMessage) t.receive("testdest");
    return msg.getText();
    } catch (Exception e) {
      return "[EXCEPTION]";
    }
  }
}

