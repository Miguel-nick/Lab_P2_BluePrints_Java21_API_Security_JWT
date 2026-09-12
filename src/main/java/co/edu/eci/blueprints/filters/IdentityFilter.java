package co.edu.eci.blueprints.filters;

import co.edu.eci.blueprints.model.Blueprint;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"identity", "default", "test"})
public class IdentityFilter implements BlueprintsFilter {
    @Override
    public Blueprint apply(Blueprint blueprint) {
        return blueprint;
    }
}