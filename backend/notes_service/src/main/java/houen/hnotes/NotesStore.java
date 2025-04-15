package houen.hnotes;

import java.util.ArrayList;

import java.security.Principal;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Component
public class NotesStore {

    private final VerificationService verificationService;
    private final ElasticSearchService elasticSearchService;
    private final NotesRepository notesRepository;

    private final Logger logger;

    public NotesStore(@Autowired NotesRepository notesRepository, @Autowired ElasticSearchService searchService, @Autowired VerificationService verificationService) {
      this.verificationService = verificationService;
      this.elasticSearchService = searchService;
      this.notesRepository = notesRepository;

      logger = LoggerFactory.getLogger(NotesRestController.class);
    }

    public void clear() {
      notesRepository.deleteAll();
    }

    public void createDummyNotes() throws Exception {
      notesRepository.deleteAll();
      for(int i = 0; i < 15; i++) {
        var n = new Note();
        n.setTitle(String.format("Note #%d", i));
        n.setContent("Here comes the content");

        addNote(n);
      }
    }

    public boolean acceptNote(Integer noteId) {
      var noteOption = notesRepository.findById(noteId);
      if(noteOption.isEmpty()) {
        return false;
      }

      var note = noteOption.get();
      note.setStatus(NoteStatus.ACCEPTED);
      notesRepository.save(note);
      return true;
    }

    public NewNoteResult addNote(Note n) throws Exception {
      var verificationResult = verificationService.Check(n.getContent());

      logger.debug("Note verification result is: " + verificationResult.toString());

      if(!verificationResult.status().equals("accepted")) {
        return null;
      }

      notesRepository.save(n);
      elasticSearchService.indexNote(n);
      return new NewNoteResult(n.getId(), n.getCreationTimestamp());
    }

    public Iterable<Note> getNotes(Principal p, int limit, int page, String query) throws Exception {
      if(p != null) {
        // TODO: user principal to filter notes
        var name = p.getName();
        logger.debug("Principal name: " + name);
      }

      if(query.length() > 0) {
        var ids = elasticSearchService.searchNotes(query);
        // TODO: retrieve notes in batch from a DB and return
        throw new UnsupportedOperationException(String.format("Querying notes is not currently supported, but would return %d notes", ids.length));
      }

      var result = notesRepository
        .findAll(PageRequest.of(page, limit, Sort.by("lastModificationTimestamp").descending()));
      return result.getContent();
    }

    public EditedNoteResult editNote(Note n) throws Exception {
      ensureNoteExists(n.getId());

      notesRepository.save(n);
      elasticSearchService.indexNote(n);

      return new EditedNoteResult(n.getLastModificationTimestamp());
    }

    public void deleteNote(int id) {
      ensureNoteExists(id);

      notesRepository.deleteById(id);
      // TODO: remove from elastic search index
    }

    private void ensureNoteExists(int id) {
      var present = notesRepository.existsById(id);
      if(!present) {
        throw new IllegalArgumentException(String.format("Note %d not found", id));
      }
   }
}
