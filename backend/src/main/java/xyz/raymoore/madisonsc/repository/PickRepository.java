package xyz.raymoore.madisonsc.repository;

import java.util.UUID;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.repository.ListCrudRepository;
import xyz.raymoore.madisonsc.domain.Pick;

@NullMarked
public interface PickRepository extends ListCrudRepository<Pick, UUID> {
}
