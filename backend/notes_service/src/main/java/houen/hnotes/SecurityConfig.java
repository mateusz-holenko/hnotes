package houen.hnotes;

import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import javax.crypto.spec.SecretKeySpec;

@Configuration
// @EnableWebSecurity
public class SecurityConfig {
  @Value("${hnotes.notes.jwt.key}")
  private String jwtKey;

  @Bean
  public JwtDecoder jwtDecoder() {
    var keyAsBytes = jwtKey.getBytes();
    var keySpec = new SecretKeySpec(keyAsBytes, 0, keyAsBytes.length, "RSA");
    return NimbusJwtDecoder.withSecretKey(keySpec).macAlgorithm(MacAlgorithm.HS512).build();
  }
}
