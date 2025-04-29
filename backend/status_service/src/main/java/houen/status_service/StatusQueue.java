package houen.status_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class StatusQueue {

  private static final Logger Logger = LoggerFactory.getLogger(StatusQueue.class);
  private static final String StatusUpdateTopic = "status.topic";

  @JmsListener(destination = StatusQueue.StatusUpdateTopic)
  public void processIncomingMessage(String content) {
    Logger.info("Handling incoming message: >>{}<<", content);
  }
}
