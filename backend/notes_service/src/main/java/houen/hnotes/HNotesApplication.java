package houen.hnotes;

import java.time.Duration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;

@SpringBootApplication
public class HNotesApplication {

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  public CircuitBreakerConfigCustomizer externalServiceVerificationBreakerConfigCustomizer() {
    return CircuitBreakerConfigCustomizer.of(
      "verification-service",
        builder -> builder
          .slidingWindowSize(5)
          .slidingWindowType(SlidingWindowType.COUNT_BASED)
          .waitDurationInOpenState(Duration.ofSeconds(10))
          .minimumNumberOfCalls(5)
          .failureRateThreshold(50.0f));
  }

  public static void main(String[] args) {
    SpringApplication.run(HNotesApplication.class, args);
	}
}
