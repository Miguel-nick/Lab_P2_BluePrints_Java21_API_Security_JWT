package co.edu.eci.blueprints.controllers;

import co.edu.eci.blueprints.persistence.BlueprintNotFoundException;
import co.edu.eci.blueprints.services.BlueprintsServices;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class DrawController {

    private final BlueprintsServices services;
    private final SimpMessagingTemplate messagingTemplate;

    public DrawController(BlueprintsServices services, SimpMessagingTemplate messagingTemplate) {
        this.services = services;
        this.messagingTemplate = messagingTemplate;
    }

    public record PointDTO(int x, int y) {}
    public record DrawMessage(String author, String name, PointDTO point) {}
    public record BroadcastUpdate(String author, String name, java.util.List<PointDTO> points) {}

    @MessageMapping("/draw")
    public void handleDraw(DrawMessage message) {
        try {
            services.addPoint(message.author(), message.name(), message.point().x(), message.point().y());

            var blueprint = services.getBlueprint(message.author(), message.name());
            var points = blueprint.getPoints().stream()
                    .map(p -> new PointDTO(p.x(), p.y()))
                    .toList();

            String destination = "/topic/blueprints.%s.%s".formatted(message.author(), message.name());
            messagingTemplate.convertAndSend(destination, new BroadcastUpdate(message.author(), message.name(), points));
        } catch (BlueprintNotFoundException e) {
            // El blueprint no existe: no hay a quién notificar, se ignora silenciosamente
            // (opcional: loggear para debug)
        }
    }
}