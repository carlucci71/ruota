package it.ddlsolution.ruota.controller;

import it.ddlsolution.ruota.dto.Giocatore;
import it.ddlsolution.ruota.dto.Tabellone;
import it.ddlsolution.ruota.service.GiocatoriService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final List<Tabellone> tabelloni;
    private final GiocatoriService giocatoriService;

    @GetMapping
    public ResponseEntity<Map> info() {
        return ResponseEntity.ok(
                Map.of("Tabelloni", tabelloni.size()
                        ,"Tabellone",tabelloni.get(3).getTitolo()
                        ,"Giocatori",giocatoriService.getGiocatori()
                )
        );
    }

    @DeleteMapping
    public ResponseEntity<Void> reset() {
        giocatoriService.reset();
        return ResponseEntity.noContent().build();
    }

}