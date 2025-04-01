package houen.hnotes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class ElasticSearchService {
  @Value("${hnotes.notes.elasticSearchService.url}")
  private String elasticSearchServiceUrl;

  @Value("${hnotes.notes.elasticSearchService.api-key}")
  private String elasticSearchServiceApiKey;

  @Autowired
  private RestTemplate restServiceTemplate;

  public record SearchResult(String status, Integer length) {};

  @CircuitBreaker(name = "elastic-search-service")
  public void add(Note note) {
    var content = String.format("""
{
  "title": "%s",
  "content": "%s"
}
    """, note.getTitle(), note.getContent());

    var entity = new HttpEntity<String>(content, prepareHeaders());
    var url = elasticSearchServiceUrl + "note/_doc/" + note.getId();

    restServiceTemplate.exchange(url, HttpMethod.POST, entity, SearchResult.class);
  }

  private HttpHeaders prepareHeaders() {
    var headers = new HttpHeaders();
    headers.set("Authorization", String.format("ApiKey %s", elasticSearchServiceApiKey));
    headers.set("Content-Type", "application/json");

    return headers;
  }

  @CircuitBreaker(name = "elastic-search-service")
  public void search(String content) {
    var query = String.format("""
{
  "query": {
    "bool": {
      "should": [
        {
          "match": {
            "title": {
              "query": "%s"
            }
          }
        },
        {
          "match": {
            "content": {
              "query": "%s"
            }
          }
        }
      ],
      "minimum_should_match" : 1
    }
  }
}""", content, content);

    var entity = new HttpEntity<String>(query, prepareHeaders());
    var url = elasticSearchServiceUrl + "note/_search";
    restServiceTemplate.exchange(url, HttpMethod.POST, entity, SearchResult.class);

    return;
  }
}
