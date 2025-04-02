package houen.hnotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class ElasticSearchService {
  @Value("${hnotes.notes.elasticSearchService.url}")
  private String elasticSearchServiceUrl;

  @Value("${hnotes.notes.elasticSearchService.api-key}")
  private String elasticSearchServiceApiKey;

  @Autowired
  private RestTemplate restServiceTemplate;

  public record SearchResult(String status, Integer length) {};

  @CircuitBreaker(name = "rest-service")
  public void indexNote(Note note) throws Exception {
    var content = new JSONObject()
      .put("title", note.getTitle())
      .put("content", note.getContent())
      .toString();

    var entity = new HttpEntity<String>(content, prepareHeaders());
    var url = elasticSearchServiceUrl + "note/_doc/" + note.getId();

    restServiceTemplate.exchange(url, HttpMethod.POST, entity, SearchResult.class);
  }

  private HttpHeaders prepareHeaders() {
    var headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    return headers;
  }

  @CircuitBreaker(name = "rest-service")
  public String searchNotes(String content) throws Exception {
    var query = new JSONObject()
      .put("query", new JSONObject()
        .put("bool", new JSONObject()
          .put("should", new JSONArray()
            .put(new JSONObject()
              .put("match", new JSONObject()
                .put("title", new JSONObject()
                  .put("query", content))))
            .put(new JSONObject()
              .put("match", new JSONObject()
                .put("content", new JSONObject()
                  .put("query", content)))))
          .put("minimum_should_match", 1)))
      .toString();

    var entity = new HttpEntity<String>(query, prepareHeaders());
    var url = elasticSearchServiceUrl + "note/_search";
    var result = restServiceTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    var str = result.getBody();
    return new JSONObject(str).toString();
  }
}
