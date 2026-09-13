package it.ddlsolution.ruota.controller;

import it.ddlsolution.ruota.dto.request.AddGiocatoreDTO;

import it.ddlsolution.ruota.service.GameService;
import it.ddlsolution.ruota.websocket.GameWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@RestController
@RequestMapping("/giocatore")
@RequiredArgsConstructor
@Slf4j
public class GiocatoreController {

    private final GameService gameService;
    private final GameWebSocketHandler gameWebSocketHandler;

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody AddGiocatoreDTO addGiocatore) {
        gameService.addGiocatori(addGiocatore.getNome());
        Map<String, Object> ret = gameService.buildInfo();
        gameWebSocketHandler.broadcast(ret);
        return ResponseEntity.ok(ret);
    }

    @PutMapping("/{nome}")
    public ResponseEntity<Map<String, Object>> update(@RequestBody AddGiocatoreDTO addGiocatore, @PathVariable String nome) {
        gameService.update(addGiocatore.getNome(),nome);
        Map<String, Object> ret = gameService.buildInfo();
        gameWebSocketHandler.broadcast(ret);
        return ResponseEntity.ok(ret);
    }

    @DeleteMapping("/{nome}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String nome) {
        gameService.deleteGiocatore(nome);
        Map<String, Object> ret = gameService.buildInfo();
        gameWebSocketHandler.broadcast(ret);
        return ResponseEntity.ok(ret);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> reset() {
        gameService.resetGiocatori();
        Map<String, Object> ret = gameService.buildInfo();
        gameWebSocketHandler.broadcast(ret);
        return ResponseEntity.ok(ret);
    }

}