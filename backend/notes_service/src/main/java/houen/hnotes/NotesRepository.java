package houen.hnotes;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NotesRepository extends CrudRepository<Note, Integer>, PagingAndSortingRepository<Note, Integer> { }
