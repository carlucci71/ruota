package it.ddlsolution.ruota.controller;

import it.ddlsolution.ruota.dto.request.AvviaDTO;
import it.ddlsolution.ruota.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
@Slf4j
public class GameController {
    private final GameService gameService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> info() {
        return ResponseEntity.ok(gameService.buildInfo());
    }


    @DeleteMapping
    public ResponseEntity<Map<String, Object>> init() {
        gameService.resetGiocatori();
        gameService.reset();
        return ResponseEntity.ok(gameService.buildInfo());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> avvia(@RequestBody AvviaDTO avviaDTO) {
        gameService.avvia(avviaDTO.getNome());
        return ResponseEntity.ok(gameService.buildInfo());
    }

    @GetMapping("/gira")
    public ResponseEntity<Map<String, Object>> gira(@RequestParam(required = false) String forzato) {
        Object gira = gameService.gira(forzato);
        Map<String, Object> ret = gameService.buildInfo();
        ret.put("SPICCHIO", gira);
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
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/soluzione")
    public ResponseEntity<Map<String, Object>> soluzione(@RequestParam String soluzione) {
        Map<String, Object> callSoluzione = gameService.soluzione(soluzione);
        Map<String, Object> ret = gameService.buildInfo();
        ret.putAll(callSoluzione);
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/autoSingolaChiamata")
    public ResponseEntity<Map<String, Object>> autoSingolaChiamata(@RequestParam boolean nascondi) {
        Map<String, Object> autoSingolaChiamata = gameService.autoSingolaChiamata(nascondi);
        Map<String, Object> ret = gameService.buildInfo();
        ret.putAll(autoSingolaChiamata);
        return ResponseEntity.ok(ret);
    }

    @GetMapping("/prenota/{nome}")
    public ResponseEntity<Map<String, Object>> prenota(@PathVariable String nome) {
        Map<String, Object> prenota = gameService.prenota(nome);
        Map<String, Object> ret = gameService.buildInfo();
        ret.putAll(prenota);
        return ResponseEntity.ok(ret);
    }

}