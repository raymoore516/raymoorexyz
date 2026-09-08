package xyz.raymoore.madisonsc.repository;

import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.repository.ListCrudRepository;
import xyz.raymoore.madisonsc.domain.Contestant;

@NullMarked
public interface ContestantRepository extends ListCrudRepository<Contestant, UUID> {
}
