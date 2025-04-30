package houen.status_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
class StompController {
  private static final Logger Logger = LoggerFactory.getLogger(StompController.class);

  @Autowired
  private SimpMessagingTemplate connection;

  public void sendUpdate(StatusUpdate status) {
    Logger.info("Sending status update: " + status.toString());
    connection.convertAndSend("/topic/status", status);
  }
}
