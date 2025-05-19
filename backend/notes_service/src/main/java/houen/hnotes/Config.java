package houen.hnotes;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import jakarta.jms.ConnectionFactory;

@Configuration
public class Config {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean(name="verificationJmsTemplate")
  public JmsTemplate createVerificationJmsTemplate(ConnectionFactory connectionFactory) {
    var template = new JmsTemplate(connectionFactory);
    template.setPubSubDomain(false);
    return template;
  }

  @Bean(name="statusJmsTemplate")
  public JmsTemplate createStatusJmsTemplate(ConnectionFactory connectionFactory) {
    var template = new JmsTemplate(connectionFactory);
    template.setPubSubDomain(true);
    return template;
  }

  @Bean
  public CircuitBreakerConfigCustomizer externalServiceVerificationBreakerConfigCustomizer() {
    return CircuitBreakerConfigCustomizer.of(
      "rest-service",
        builder -> builder
          .slidingWindowSize(5)
          .slidingWindowType(SlidingWindowType.COUNT_BASED)
          .waitDurationInOpenState(Duration.ofSeconds(10))
          .minimumNumberOfCalls(5)
          .failureRateThreshold(50.0f));
  }
}
