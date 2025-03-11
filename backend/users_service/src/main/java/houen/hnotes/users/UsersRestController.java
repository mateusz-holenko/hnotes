package houen.hnotes.users;

import java.util.ArrayList;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.beans.factory.annotation.Autowired;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Base64;

import java.util.Collection;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.http.HttpStatus;

import java.util.Map;

import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UsersRestController {

    private static Collection<User> users = new ArrayList<User>() {{
      add(new User(100, "admin"));
      add(new User(101, "User1"));
      add(new User(800, "Guest"));
    }};

    @Value("${hnotes.users.jwt.key}")
    private String jwtKey;

    @PostMapping("/users/login")
    public Map<String, String> loginUser(@RequestBody UserCredentials credentials) {
      // var logger = LoggerFactory.getLogger(UsersRestController.class);
      // logger.error("creds are: " + credentials.toString());

      var user = users.stream()
        .filter(u -> u.getHandle().equals(credentials.username()) && u.checkCredentials(credentials.password()))
        .findFirst();

      if(!user.isPresent()) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("user '%s' not found", credentials.username()));
      }

      var currentTimestamp = System.currentTimeMillis();
      var jws = Jwts.builder()
        .subject(user.get().getId().toString())
        .setIssuedAt(new Date(currentTimestamp))
        .setExpiration(new Date(currentTimestamp +  60 * 60 * 1000)) // 1h
        .signWith(SignatureAlgorithm.HS512, jwtKey.getBytes());

      return Map.of(
        "username", user.get().getHandle(),
        "jwt", jws.compact());
    }
}
