package houen.hnotes;

import java.util.ArrayList;
import java.time.Instant;
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
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class NotesRestController {
  
    @Autowired
    private NotesRepository notesRepository;
    
    @PostMapping("/notes/create")
    public void createNotes() {
      var innerNotes = new ArrayList<Note>();
      for(int i = 0; i < 15; i++) {
        var n = new Note();
        n.setTitle(String.format("Note #%d", i));
        n.setContent("Here comes the content");
        innerNotes.add(n);
      }
      notesRepository.deleteAll();
      notesRepository.saveAll(innerNotes);
    }

    record NewNoteResult(Integer id, Instant creationTimestamp) {};
    
    @PostMapping("/notes")
    @ResponseBody
    public NewNoteResult addNote(@RequestBody Note n) {
      notesRepository.save(n);
      return new NewNoteResult(n.getId(), n.getCreationTimestamp());
    }

    @GetMapping("/notes")
    @ResponseBody
    public Iterable<Note> getNotes(Principal p, @RequestParam(value = "limit", defaultValue = "10") Integer limit, @RequestParam(value = "page", defaultValue = "0") Integer page) {
      var logger = LoggerFactory.getLogger(NotesRestController.class);

      if(p != null) {
        var name = p.getName();
        logger.error("Principal name: " + name);
      }
      
      var result = notesRepository
        .findAll(PageRequest.of(page, limit, Sort.by("lastModificationTimestamp").descending()));
      return result.getContent();
    }
    
    record EditedNoteResult(Instant lastModificationTimestamp) {};

    @PutMapping("/notes/{id}")
    @ResponseBody
    public EditedNoteResult editNote(@PathVariable("id") int id, @RequestBody Note n) {
      ensureNoteExists(id);
      
      n.setId(id);
      notesRepository.save(n);

      return new EditedNoteResult(n.getLastModificationTimestamp());
    }
    
    @DeleteMapping("/notes/{id}")
    public void deleteNote(@PathVariable("id") int id) {
      ensureNoteExists(id);
      
      notesRepository.deleteById(id);
    }

    private void ensureNoteExists(int id) {
      // using two DB calls is ugly, but I couldn't find a better way in the API?!
      var present = notesRepository.existsById(id);
      if(!present) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("note %d not found", id));
      }
   }
}
