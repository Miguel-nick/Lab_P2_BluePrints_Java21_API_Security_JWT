package co.edu.eci.blueprints.persistence;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@Primary
public class PostgresBlueprintPersistence implements BlueprintPersistence {
    private final SpringDataBlueprintRepository repository;

    public PostgresBlueprintPersistence(SpringDataBlueprintRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveBlueprint(Blueprint blueprint) throws BlueprintPersistenceException {
        if (repository.existsByAuthorAndName(blueprint.getAuthor(), blueprint.getName())) {
            throw new BlueprintPersistenceException(
                    "Blueprint already exists: " + blueprint.getAuthor() + ":" + blueprint.getName());
        }
        repository.save(blueprint);
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        return repository.findByAuthorAndName(author, name)
                .orElseThrow(() -> new BlueprintNotFoundException(
                        "Blueprint not found: %s/%s".formatted(author, name)));
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> blueprints = new HashSet<>(repository.findAllByAuthor(author));
        if (blueprints.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints for author: " + author);
        }
        return blueprints;
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(repository.findAll());
    }

    @Override
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        Blueprint blueprint = getBlueprint(author, name);
        blueprint.addPoint(new Point(x, y));
        repository.save(blueprint);
    }
}