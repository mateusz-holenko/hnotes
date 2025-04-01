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

import org.springframework.web.server.ResponseStatusException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import jakarta.jms.Message;
import jakarta.jms.TextMessage;
import jakarta.jms.Session;
import jakarta.jms.JMSException;

@Service
class ArtemisService {

  private final JmsTemplate t;

  @Autowired
  public ArtemisService(JmsTemplate jmsTemplate) {
    this.t = jmsTemplate;
  }

  public void send(String text) {
    t.send("testdest", new MessageCreator() {
      public Message createMessage(Session session) throws JMSException {
        return session.createTextMessage(text);
      }
    });
  }

  public String receive() {
    try {
    var msg = (TextMessage) t.receive("testdest");
    return msg.getText();
    } catch (Exception e) {
      return "[EXCEPTION]";
    }
  }
}

@RestController
@CrossOrigin(origins = "*")
public class NotesRestController {
  
    @Autowired
    private ArtemisService artemisService;
    
    @GetMapping("/artemis/send/{text}")
    public void artemisSend(@PathVariable(value = "text") String text) {
      artemisService.send(text);
    }
    
    @GetMapping("/artemis/receive")
    public String artemisReceive() {
      return artemisService.receive();
    }
  
    @Autowired
    private NotesRestControllerOptions options;

    @Autowired
    private VerificationService verificationServiceProxy;

    @Autowired
    private ElasticSearchService elasticSearchServiceProxy;
  
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
    public NewNoteResult addNote(@RequestBody Note n) {
      var logger = LoggerFactory.getLogger(NotesRestController.class);
      var verificationResult = verificationServiceProxy.Check(n.getContent());
      logger.error("Verification result is: " + verificationResult.toString());
      if(!verificationResult.status().equals("accepted")) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Note's content not accepted");
      }

      options.waitOnCreate();

      notesRepository.save(n);
      return new NewNoteResult(n.getId(), n.getCreationTimestamp());
    }
    
    @GetMapping("/test/add/{id}")
    public void testAddingNote(@PathVariable(value = "id") Integer id) {
      var note = notesRepository
        .findById(id).get();

        elasticSearchServiceProxy.add(note);
    }
    
    @GetMapping("/test/search/{query}")
    public void testQueryingNote(@PathVariable(value = "query") String q) {
        elasticSearchServiceProxy.search(q);
    }

    @GetMapping("/notes")
    public Iterable<Note> getNotes(Principal p, @RequestParam(value = "limit", defaultValue = "10") Integer limit, @RequestParam(value = "page", defaultValue = "0") Integer page) {
      var logger = LoggerFactory.getLogger(NotesRestController.class);

      if(p != null) {
        var name = p.getName();
        logger.error("Principal name: " + name);
      }
      
      options.waitOnFetch();

      var result = notesRepository
        .findAll(PageRequest.of(page, limit, Sort.by("lastModificationTimestamp").descending()));
      return result.getContent();
    }
    
    record EditedNoteResult(Instant lastModificationTimestamp) {};

    @PutMapping("/notes/{id}")
    public EditedNoteResult editNote(@PathVariable("id") int id, @RequestBody Note n) {
      ensureNoteExists(id);
      
      options.waitOnUpdate();

      n.setId(id);
      notesRepository.save(n);

      return new EditedNoteResult(n.getLastModificationTimestamp());
    }
    
    @DeleteMapping("/notes/{id}")
    public void deleteNote(@PathVariable("id") int id) {
      ensureNoteExists(id);
      
      options.waitOnRemove();
      
      notesRepository.deleteById(id);
    }

    private void ensureNoteExists(int id) {
      var present = notesRepository.existsById(id);
      if(!present) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("note %d not found", id));
      }
   }
}
