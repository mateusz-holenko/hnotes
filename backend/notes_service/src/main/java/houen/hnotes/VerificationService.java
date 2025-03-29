package houen.hnotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class VerificationService {
  @Value("${hnotes.notes.verificationService.url}")
  private String verificationServiceUrl;

  @Autowired
  private RestTemplate restServiceTemplate;

  public record VerificationResult(String status, Integer length) {};

  @CircuitBreaker(name = "verification-service")
  public VerificationResult Check(String content) {
    return restServiceTemplate.postForObject(verificationServiceUrl + "/verificator", content, VerificationResult.class);
  }
}
