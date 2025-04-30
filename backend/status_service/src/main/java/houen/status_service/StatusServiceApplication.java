package houen.status_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import jakarta.jms.ConnectionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableWebSocketMessageBroker
@EnableScheduling
public class StatusServiceApplication {
  @Bean
  public JmsListenerContainerFactory<?> jmsListenerFactory(ConnectionFactory jmsConnectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    configurer.configure(factory, jmsConnectionFactory);
    factory.setPubSubDomain(true);
    return factory;
  }

	public static void main(String[] args) {
		SpringApplication.run(StatusServiceApplication.class, args);
	}
}

@Configuration
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
	}

	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/websocket").setAllowedOriginPatterns("*");
	}
}

enum NoteStatus {
	CREATED,
	UPDATED,
	REMOVED
}

class NoteMessage {
	private String content;

	public String getContent() {
		return this.content;
	}

	public NoteMessage() {
	}

	public NoteMessage(String s) {
		this.content = s;
	}
}

@Controller
class StompController {
	@Autowired
	private SimpMessagingTemplate con;

	public void publishNote(NoteStatus status) {
		var logger = LoggerFactory.getLogger(StompController.class);
		logger.error("Sending!!! " + status);
		con.convertAndSend("/topic/notes", new NoteMessage(status.toString()));
	}

	@MessageMapping("/hello")
	public String receiveMessage(String msg) {
		var logger = LoggerFactory.getLogger(StompController.class);
		logger.error("Received " + msg);
		return "OK";
	}

	private Integer s = 0;
	@Scheduled(fixedRate = 5000)
  public void periodicSend() {
  	s = (s + 1) % 3;
  	publishNote(NoteStatus.values()[s]);
  }
}
