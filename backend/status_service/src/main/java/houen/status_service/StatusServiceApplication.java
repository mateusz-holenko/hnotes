package houen.status_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

import jakarta.jms.ConnectionFactory;

@SpringBootApplication
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
