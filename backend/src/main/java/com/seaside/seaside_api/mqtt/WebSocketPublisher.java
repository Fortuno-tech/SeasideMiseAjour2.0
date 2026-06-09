package com.seaside.seaside_api.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
 
import java.util.Map;
 
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketPublisher {
 
    private final SimpMessagingTemplate messagingTemplate;
 
    // ─── Pousse une mise à jour de comptage vers React ───────
    // React s'abonne à /topic/evenement/{evenementId}
    public void publierMiseAJour(String evenementId, Map<String, Object> donnees) {
        String destination = "/topic/evenement/" + evenementId;
        messagingTemplate.convertAndSend(destination, donnees);
        log.debug("WebSocket push → {} | {}", destination, donnees);
    }
 
    // ─── Pousse une alerte (capacité max atteinte, etc.) ────
    public void publierAlerte(String evenementId, String message) {
        String destination = "/topic/alerte/" + evenementId;
        messagingTemplate.convertAndSend(destination, Map.of(
                "type",    "ALERTE",
                "message", message
        ));
        log.info("WebSocket alerte → {} | {}", destination, message);
    }
}