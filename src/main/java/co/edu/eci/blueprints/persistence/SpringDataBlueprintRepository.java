package co.edu.eci.blueprints.persistence;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.BlueprintId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataBlueprintRepository extends JpaRepository<Blueprint, BlueprintId> {
    Optional<Blueprint> findByAuthorAndName(String author, String name);

    List<Blueprint> findAllByAuthor(String author);

    boolean existsByAuthorAndName(String author, String name);
}