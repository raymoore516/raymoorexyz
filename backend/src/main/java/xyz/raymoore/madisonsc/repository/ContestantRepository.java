package xyz.raymoore.madisonsc.repository;

import java.util.UUID;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.jdbc.repository.query.Query;
import xyz.raymoore.madisonsc.domain.Contestant;

@NullMarked
public interface ContestantRepository extends ListCrudRepository<Contestant, UUID> {

    @Query("SELECT contestant_id, entry_date, name FROM madisonsc.contestant ORDER BY name ASC")
    List<Contestant> findAllAlphabetically();
}
