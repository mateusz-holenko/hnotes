package houen.hnotes;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
