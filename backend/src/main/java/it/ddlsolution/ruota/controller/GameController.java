package it.ddlsolution.ruota.controller;

import it.ddlsolution.ruota.dto.request.AvviaDTO;
import it.ddlsolution.ruota.service.GameService;
import it.ddlsolution.ruota.websocket.GameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {
    private final GameService gameService;
    private final GameWebSocketHandler gameWebSocketHandler;

    @GetMapping
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(gameService.buildInfo());
    }


    @DeleteMapping
    public ResponseEntity<Map<String, Object>> reset() {
        //gameService.resetGiocatori();
        gameService.reset();
        Map<String, Object> ret = gameService.buildInfo();
        gameWebSocketHandler.broadcast(ret, "RESET");
        return ResponseEntity.ok(ret);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> avvia(@RequestBody AvviaDTO avviaDTO) {
        gameService.avvia(avviaDTO.getNome());
        Map<String, Object> ret = gameService.buildInfo();
        gameWebSocketHandler.broadcast(ret, "AVVIA");
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/gira")
    public ResponseEntity<Map<String, Object>> gira(@RequestParam(required = false) String forzato) {
        Object gira = gameService.gira(forzato);
        Map<String, Object> ret = gameService.buildInfo();
        ret.put("SPICCHIO", gira);
        gameWebSocketHandler.broadcast(ret, "GIRA");
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/consonante")
    public ResponseEntity<Map<String, Object>> consonante(@RequestParam Character consonante, @RequestParam Object trovato) {
        if (Character.isLowerCase(consonante)) {
            consonante = Character.toUpperCase(consonante);
        }
        Map<String, Object> chiamaConsonante = gameService.chiamaConsonante(consonante, trovato);
        Map<String, Object> ret = gameService.buildInfo();
        ret.putAll(chiamaConsonante);
        gameWebSocketHandler.broadcast(ret, "CONSONANTE");
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/vocale")
    public ResponseEntity<Map<String, Object>> vocale(@RequestParam Character vocale) {
        if (Character.isLowerCase(vocale)) {
            vocale = Character.toUpperCase(vocale);
        }
        Map<String, Object> compraVocale = gameService.compraVocale(vocale);
        Map<String, Object> ret = gameService.buildInfo();
        ret.putAll(compraVocale);
        gameWebSocketHandler.broadcast(ret, "VOCALE");
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/soluzione")
    public ResponseEntity<Map<String, Object>> soluzione(@RequestParam String soluzione) {
        Map<String, Object> callSoluzione = gameService.soluzione(soluzione);
        Map<String, Object> ret = gameService.buildInfo();
        ret.putAll(callSoluzione);
        gameWebSocketHandler.broadcast(ret, "SOLUZIONE");
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/passa")
    public ResponseEntity<Map<String, Object>> passa() {
        Map<String, Object> callPassa = gameService.passa();
        Map<String, Object> ret = gameService.buildInfo();
        ret.putAll(callPassa);
        gameWebSocketHandler.broadcast(ret, "PASSA");
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/autoSingolaChiamata")
    public ResponseEntity<Map<String, Object>> autoSingolaChiamata(@RequestParam boolean nascondi, @RequestParam(required = false) Boolean riprendi) {
        Map<String, Object> autoSingolaChiamata = new HashMap<>();
        GameService.Manche mancheCorrente = gameService.getMancheCorrente();
        if (!ObjectUtils.isEmpty(mancheCorrente) && (mancheCorrente.getTipoManche() == GameService.TipoManche.AUTO_SINGOLA_CHIAMATA
                || mancheCorrente.getTipoManche() == GameService.TipoManche.AUTO_SINGOLA_CHIAMATA_NASCONDI)) {
            autoSingolaChiamata = gameService.autoSingolaChiamata(nascondi);
        }
        Map<String, Object> ret = gameService.buildInfo();
        ret.putAll(autoSingolaChiamata);
        if (riprendi != null && riprendi) {
            ret.put("RIPRENDI_TIMER", riprendi);
        }
        gameWebSocketHandler.broadcast(ret, "AUTO_SINGOLA_CHIAMATA");
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/prenota/{nome}")
    public ResponseEntity<Map<String, Object>> prenota(@PathVariable String nome) {
        Map<String, Object> prenota = gameService.prenota(nome);
        Map<String, Object> ret = gameService.buildInfo();
        ret.putAll(prenota);
        gameWebSocketHandler.broadcast(ret, "PRENOTA");
        return ResponseEntity.ok(ret);
    }

}