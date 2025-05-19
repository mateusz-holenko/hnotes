package houen.hnotes;

import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@DataMongoTest
@EnableMongoRepositories
@ComponentScan(basePackages = {"houen.hnotes"}, resourcePattern="NotesStore.class")
public class NotesStoreTests {

  @Autowired
  private NotesStore notesStore;

  @MockitoBean
  private ArtemisService artemisService;

  @MockitoBean
  private ElasticSearchService searchService;

  @MockitoBean
  private StatusService statusService;

  @Test
  @DirtiesContext
  public void newNote_ShouldBeUnverified() throws Exception {
    notesStore.addNote(new Note("title", "content"));

    var currentNotes = new ArrayList<Note>();
    notesStore.getNotes(null, 100, 0, "").forEach(currentNotes::add);

    Assertions.assertEquals(1, currentNotes.size());

    var retrievedNote = currentNotes.get(0);
    Assertions.assertEquals("title", retrievedNote.getTitle());
    Assertions.assertEquals("content", retrievedNote.getContent());
    Assertions.assertEquals(NoteStatus.UNVERIFIED, retrievedNote.getStatus());
  }

  @Test
  @DirtiesContext
  public void search_ShouldWork() throws Exception {
    var noteId0 = notesStore.addNote(new Note("First note", "Like a butterfly")).id();
    notesStore.addNote(new Note("Second note", "Like a dragon"));
    var noteId1 = notesStore.addNote(new Note("Third note", "And another butterfly")).id();

    when(searchService.searchNotes("butterfly"))
      .thenReturn(new String[] { noteId0, noteId1 });

    var notes = new ArrayList<Note>();
    notesStore.getNotes(null, 10, 0, "butterfly").forEach(notes::add);

    Assertions.assertEquals(2, notes.size());

    Assertions.assertEquals("Third note", notes.get(0).getTitle());
    Assertions.assertEquals("And another butterfly", notes.get(0).getContent());
    Assertions.assertEquals("First note", notes.get(1).getTitle());
    Assertions.assertEquals("Like a butterfly", notes.get(1).getContent());
  }
}
