package houen.hnotes;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureTestEntityManager
@Transactional
public class NotesStoreTests {

  @Autowired
  private NotesStore notesStore;

  @MockitoBean
  private ArtemisService artemisService;

  @MockitoBean
  private ElasticSearchService searchService;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  public void testGettingNotes() throws Exception {
    notesStore.createDummyNotes();
    var notes = new ArrayList<Note>();
    notesStore.getNotes(null, 3, 1, "").forEach(notes::add);

    Assertions.assertEquals(3, notes.size());
  }

  @Test
  public void search_ShouldWork() throws Exception {
    entityManager.persist(new Note("First note", "Like a butterfly"));
    entityManager.persist(new Note("Second note", "Like a dragon"));
    entityManager.persist(new Note("Third note", "And another butterfly"));

    when(searchService.searchNotes("butterfly"))
      .thenReturn(new Integer[] { 1, 3 });

    var notes = new ArrayList<Note>();
    notesStore.getNotes(null, 10, 0, "butterfly").forEach(notes::add);

    Assertions.assertEquals(2, notes.size());
    // TODO: the ordering is off here; I would expect the 'third note' to be returned first!

    Assertions.assertEquals("First note", notes.get(0).getTitle());
    Assertions.assertEquals("Like a butterfly", notes.get(0).getContent());

    Assertions.assertEquals("Third note", notes.get(1).getTitle());
    Assertions.assertEquals("And another butterfly", notes.get(1).getContent());
  }
}
