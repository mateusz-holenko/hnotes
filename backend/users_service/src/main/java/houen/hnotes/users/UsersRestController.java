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

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UsersRestController {
    @Value("${hnotes.users.jwt.key}")
    private String jwtKey;
  
    @PostMapping("/users/login")
    public String loginUser() {
      var currentTimestamp = System.currentTimeMillis();
      var jws = Jwts.builder()
        .subject("Username")
        .setIssuedAt(new Date(currentTimestamp))
        .setExpiration(new Date(currentTimestamp +  60 * 60 * 1000)) // 1h
        .signWith(SignatureAlgorithm.HS512, jwtKey.getBytes());

      return jws.compact();
    }
}
