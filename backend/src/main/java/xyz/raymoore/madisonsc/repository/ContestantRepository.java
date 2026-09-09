package xyz.raymoore.madisonsc.repository;

import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.query.Param;
import xyz.raymoore.madisonsc.domain.Contestant;

@NullMarked
public interface ContestantRepository extends ListCrudRepository<Contestant, UUID> {

    @Query("""
            SELECT contestant_id, entry_date, name
            FROM madisonsc.contestant
            WHERE LOWER(name) = LOWER(:name)
            ORDER BY contestant_id
            FOR UPDATE
            """)
    List<Contestant> findByNameIgnoreCaseForUpdate(@Param("name") String name);
}
