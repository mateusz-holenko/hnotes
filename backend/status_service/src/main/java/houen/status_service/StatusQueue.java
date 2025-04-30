package houen.status_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class StatusQueue {

  private static final Logger Logger = LoggerFactory.getLogger(StatusQueue.class);
  private static final String StatusUpdateTopic = "status.topic";

  private static final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private StompController stompController;

  @JmsListener(destination = StatusQueue.StatusUpdateTopic)
  public void processIncomingMessage(String content) {
    Logger.debug("Handling incoming message: >>{}<<", content);
    try {
      var status = (StatusUpdate)mapper.readValue(content, StatusUpdate.class);
      stompController.sendUpdate(status);
    } catch(Exception e) {
      Logger.error("Error while parsing message: {}", e.getMessage());
    }
  }
}
