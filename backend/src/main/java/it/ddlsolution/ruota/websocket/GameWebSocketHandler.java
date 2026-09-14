package it.ddlsolution.ruota.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.ddlsolution.ruota.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler WebSocket del gioco.
 *
 * <p>Ogni client connesso a {@code /api/ruota/game/ws} riceve lo stato del gioco
 * quando questo cambia (broadcast), in modo che tutti i tabelloni aperti restino
 * sincronizzati in tempo reale senza polling.</p>
 *
 * <p>Formato messaggi server -&gt; client: {@code {"tipo":"STATE","data":{...stato gioco...}}}</p>
 * <p>Formato messaggi client -&gt; server: {@code {"action":"getState"}} oppure {@code {"action":"ping"}}</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GameWebSocketHandler extends TextWebSocketHandler {

    /** Tipo messaggio inviato ad ogni aggiornamento di stato. */
    public static final String TIPO_STATE = "STATE";

    private final GameService gameService;
    private final ObjectMapper objectMapper;

    /** Set thread-safe delle sessioni WebSocket attive. */
    private final Set<WebSocketSession> sessioni = ConcurrentHashMap.newKeySet();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessioni.add(session);
        log.info("WebSocket connesso: {} (totale client: {})", session.getId(), sessioni.size());
        inviaStato(session, "AFTER_CONNECT");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        try {
            Map<String, Object> richiesta = objectMapper.readValue(payload, Map.class);
            String azione = String.valueOf(richiesta.getOrDefault("action", ""));
            switch (azione) {
                case "getState":
                    inviaStato(session,"HANDLE_TEXT_MESSAGE");
                    break;
                case "ping":
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage("{\"tipo\":\"PONG\"}"));
                    }
                    break;
                default:
                    log.debug("Azione WebSocket non gestita da client {}: {}", session.getId(), payload);
            }
        } catch (Exception e) {
            log.warn("Messaggio WebSocket non valido ricevuto da {}: {}", session.getId(), payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessioni.remove(session);
        log.info("WebSocket disconnesso: {} ({}) - totale client: {}", session.getId(), status, sessioni.size());
    }

    /** Invia a tutti i client connessi lo stato aggiornato del gioco. */
    public void broadcastStato() {
        broadcast(gameService.buildInfo(), "INFO");
    }

    /**
     * Diffonde un payload (in genere la stessa mappa restituita dal controller)
     * a tutti i client connessi, avvolto nel messaggio {@code STATE}.
     */
    public void broadcast(Map<String, Object> payload, String contesto) {
        Map<String, Object> linked = new LinkedHashMap<>();
        linked.put("CONTESTO", contesto);
        linked.putAll(payload);
        String json = serializza(linked);
        if (json == null) {
            return;
        }
        for (WebSocketSession session : sessioni) {
            try {
                if (session.isOpen()) {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(json));
                    }
                } else {
                    sessioni.remove(session);
                }
            } catch (Exception e) {
                log.warn("Invio WebSocket fallito per la sessione {}: {}", session.getId(), e.getMessage());
                sessioni.remove(session);
            }
        }
    }

    private void inviaStato(WebSocketSession session, String contesto) {
        Map<String, Object> linked = new LinkedHashMap<>();
        linked.put("CONTESTO", contesto);
        Map<String, Object> payload = gameService.buildInfo();
        linked.putAll(payload);
        String json = serializza(linked);
        if (json == null) {
            return;
        }
        try {
            if (session.isOpen()) {
                synchronized (session) {
                    session.sendMessage(new TextMessage(json));
                }
            }
        } catch (Exception e) {
            log.error("Errore invio stato iniziale WebSocket per la sessione {}", session.getId(), e);
            sessioni.remove(session);
        }
    }

    private String serializza(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(Map.of("tipo", TIPO_STATE, "data", payload));
        } catch (JsonProcessingException e) {
            log.error("Errore serializzazione broadcast WebSocket", e);
            return null;
        }
    }
}