package houen.hnotes;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@CrossOrigin(origins = "*")
public class NotesRestController {
    @Autowired
    private NotesStore notesStore;

    @Autowired
    private NotesRestControllerOptions options;

    // TODO: this should be removed/moved to some debug controller
    @PostMapping("/notes/create")
    public void createNotes() throws Exception {
      notesStore.createDummyNotes();
    }

    @PostMapping("/notes")
    public NewNoteResult addNote(@RequestBody Note n) throws Exception {
      options.waitOnCreate();
      return notesStore.addNote(n);
    }

    @GetMapping("/notes")
    public Iterable<Note> getNotes(Principal p, @RequestParam(value = "limit", defaultValue = "10") Integer limit, @RequestParam(value = "page", defaultValue = "0") Integer page, @RequestParam(value = "query", defaultValue = "") String query) throws Exception {
      options.waitOnFetch();
      return notesStore.getNotes(p, limit, page, query);
    }

    @PutMapping("/notes/{id}")
    public EditedNoteResult editNote(@PathVariable("id") int id, @RequestBody Note n) throws Exception {
      options.waitOnUpdate();
      n.setId(id);
      return notesStore.editNote(n);
    }

    @DeleteMapping("/notes/{id}")
    public void deleteNote(@PathVariable("id") int id) {
      options.waitOnRemove();
      notesStore.deleteNote(id);
    }
}
