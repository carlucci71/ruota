package it.ddlsolution.ruota.controller;

import it.ddlsolution.ruota.dto.Giocatore;
import it.ddlsolution.ruota.dto.Tabellone;
import it.ddlsolution.ruota.dto.request.AvviaDTO;
import it.ddlsolution.ruota.service.GameService;

import it.ddlsolution.ruota.util.Utility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {
    private final GameService gameService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Tabellone> tabelloni = gameService.getTabelloni();
        result.put("Tabelloni", tabelloni.size());
        result.put("Tabellone titolo",gameService.getTabelloneTurno() == null? "--": gameService.getTabelloneTurno().getTitolo());
        result.put("TabelloneInProgress",gameService.getTabelloneInProgress() == null? "--": gameService.getTabelloneInProgress().getFrase());
        result.put("GiocatoreTurno",gameService.getGiocatoreTurno() == null? "--": gameService.getGiocatoreTurno().getNome());
        result.put("Giocatori", gameService.getGiocatori());
        result.put("Fase", gameService.getFase());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<Void> reset() {
        gameService.resetGiocatori();
        gameService.resetTurno();
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<Void> avvia(@RequestBody AvviaDTO avviaDTO) {
        gameService.avvia(avviaDTO.getNomeGiocatore());
        return ResponseEntity.noContent().build();
    }


}