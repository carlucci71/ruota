package it.ddlsolution.ruota.config;

import it.ddlsolution.ruota.websocket.GameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.List;

/**
 * Registra l'endpoint WebSocket del gioco.
 *
 * <p>Il percorso é relativo al context-path dell'applicazione
 * ({@code server.servlet.context-path=/api/ruota}); l'endpoint é sotto
 * {@code /game/**} così l'handshake WebSocket (che passa comunque dalla catena
 * di filtri Spring Security) viene permesso dalla regola gia' esistente
 * {@code /game/** -> permitAll}.</p>
 *
 * <p><b>Origini consentite:</b> senza {@code setAllowedOrigins}, l'OriginHandshakeInterceptor
 * di Spring WebSocket richiede per default che l'header {@code Origin} dell'handshake sia
 * same-origin col server (es. {@code localhost:8083}) e rifiuta con 403 gli handshake
 * cross-origin (es. browser su {@code localhost:4800} o su IP pubblico). Per questo
 * l'endpoint dichiara esplicitamente le stesse origini consentite del CORS
 * (proprietà {@code ALLOWED_SERVERS}).</p>
 *
 * <p>URL completo: {@code ws://host:8083/api/ruota/game/ws}.</p>
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    /** Percorso (relativo al context-path) dell'endpoint WebSocket del gioco. */
    public static final String PERCORSO_GAME = "/game/ws";

    private final GameWebSocketHandler gameWebSocketHandler;

    /**
     * Origini consentite per l'handshake WebSocket (stesse del CORS HTTP).
     * Default: {@code http://localhost:4800,http://localhost:8083}.
     */
    @Value("${ALLOWED_SERVERS:http://localhost:4800,http://localhost:8083}")
    private List<String> allowedServers;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebSocketHandler, PERCORSO_GAME)
                .setAllowedOrigins(allowedServers.toArray(new String[0]));
    }
}