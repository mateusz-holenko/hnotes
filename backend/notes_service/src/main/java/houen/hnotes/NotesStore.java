package houen.hnotes;

import java.security.Principal;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Component
public class NotesStore {
  
    private static final Logger logger = LoggerFactory.getLogger(NotesStore.class);

    private final ElasticSearchService elasticSearchService;
    private final NotesRepository notesRepository;
    private final ArtemisService artemisService;
    private final StatusService statusService;

    @Autowired
    public NotesStore(NotesRepository notesRepository, ElasticSearchService searchService, ArtemisService artemisService, StatusService statusService) {
      this.elasticSearchService = searchService;
      this.notesRepository = notesRepository;
      this.artemisService = artemisService;
      this.statusService = statusService;
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

    public boolean acceptNote(String noteId) {
      if(!setNoteStatus(noteId, NoteStatus.ACCEPTED)) {
        return false;
      }

      statusService.sendStatusUpdate(StatusUpdate.NoteAccepted(noteId));
      return true;
    }

    public boolean rejectNote(String noteId) {
      if(!setNoteStatus(noteId, NoteStatus.REJECTED)) {
        return false;
      }

      statusService.sendStatusUpdate(StatusUpdate.NoteRejected(noteId));
      return true;
    }

    private boolean setNoteStatus(String noteId, NoteStatus status) {
      var noteOption = notesRepository.findById(noteId);
      if(noteOption.isEmpty()) {
        return false;
      }

      var note = noteOption.get();
      note.setStatus(status);
      notesRepository.save(note);
      return true;
    }

    public NewNoteResult addNote(Note n) throws Exception {
      notesRepository.save(n);
      statusService.sendStatusUpdate(StatusUpdate.NoteCreated(n.getId()));
      elasticSearchService.indexNote(n);
      artemisService.send(new NoteVerificationRequest(n.getId(), n.getTitle(), n.getContent()));
      return new NewNoteResult(n.getId(), n.getCreationTimestamp());
    }

    public Iterable<Note> getNotes(Principal p, int limit, int page, String query) throws Exception {
      if(p != null) {
        // TODO: user principal to filter notes
        var name = p.getName();
        logger.debug("Principal name: " + name);
      }

      var pageRequest = PageRequest.of(page, limit, Sort.by("lastModificationTimestamp").descending());
      Page<Note> result;

      if(query.length() > 0) {
        var ids = elasticSearchService.searchNotes(query);
        result = notesRepository.findByIdIn(ids, pageRequest);
      } else {
        result = notesRepository.findAll(pageRequest);
      }

      return result.getContent();
    }

    public EditedNoteResult editNote(Note n) throws Exception {
      ensureNoteExists(n.getId());

      notesRepository.save(n);
      statusService.sendStatusUpdate(StatusUpdate.NoteUpdated(n.getId()));
      elasticSearchService.indexNote(n);

      return new EditedNoteResult(n.getLastModificationTimestamp());
    }

    public void deleteNote(String id) {
      ensureNoteExists(id);

      notesRepository.deleteById(id);
      statusService.sendStatusUpdate(StatusUpdate.NoteDeleted(id));
      // TODO: remove from elastic search index
    }

    private void ensureNoteExists(String id) {
      var present = notesRepository.existsById(id);
      if(!present) {
        throw new IllegalArgumentException(String.format("Note %d not found", id));
      }
   }
}
