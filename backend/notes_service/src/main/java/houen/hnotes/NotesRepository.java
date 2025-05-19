package houen.hnotes;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotesRepository extends MongoRepository<Note, String>
{
  Page<Note> findByIdIn(String[] ids, Pageable page);
}
