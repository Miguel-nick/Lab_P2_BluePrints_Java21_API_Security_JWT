package co.edu.eci.blueprints.filters;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("undersampling")
public class UndersamplingFilter implements BlueprintsFilter {
    @Override
    public Blueprint apply(Blueprint blueprint) {
        List<Point> input = blueprint.getPoints();
        if (input.size() <= 2) {
            return blueprint;
        }

        List<Point> output = new ArrayList<>();
        for (int index = 0; index < input.size(); index += 2) {
            output.add(input.get(index));
        }
        return new Blueprint(blueprint.getAuthor(), blueprint.getName(), output);
    }
}