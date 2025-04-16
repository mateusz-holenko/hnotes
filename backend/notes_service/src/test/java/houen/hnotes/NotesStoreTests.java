package houen.hnotes;

import java.util.ArrayList;

import org.apache.activemq.artemis.jms.client.ActiveMQTextMessage;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;

@SpringBootTest()
public class NotesStoreTests {

  @Autowired
  private NotesStore notesStore;

  @MockitoBean
  private ArtemisService artemisService;

  @MockitoBean
  private ElasticSearchService searchService;


  @Test
  public void testGettingNotes() throws Exception {
    notesStore.createDummyNotes();
    var notes = new ArrayList<Note>();
    notesStore.getNotes(null, 3, 1, "").forEach(notes::add);

    Assertions.assertEquals(3, notes.size());
  }
}
