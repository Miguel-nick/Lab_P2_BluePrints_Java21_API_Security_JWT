package co.edu.eci.blueprints.filters;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("redundancy")
public class RedundancyFilter implements BlueprintsFilter {
    @Override
    public Blueprint apply(Blueprint blueprint) {
        List<Point> input = blueprint.getPoints();
        if (input.isEmpty()) {
            return blueprint;
        }

        List<Point> output = new ArrayList<>();
        Point previous = null;
        for (Point point : input) {
            if (previous == null || !previous.equals(point)) {
                output.add(point);
                previous = point;
            }
        }
        return new Blueprint(blueprint.getAuthor(), blueprint.getName(), output);
    }
}