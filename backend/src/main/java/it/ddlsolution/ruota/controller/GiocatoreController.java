package it.ddlsolution.ruota.controller;

import it.ddlsolution.ruota.dto.Giocatore;
import it.ddlsolution.ruota.dto.request.AddGiocatore;
import it.ddlsolution.ruota.service.GiocatoriService;
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

import java.util.Set;

@RestController
@RequestMapping("/giocatore")
@RequiredArgsConstructor
@Slf4j
public class GiocatoreController {

    private final GiocatoriService giocatoriService;

    @PostMapping
    public ResponseEntity<Void> add(@RequestBody AddGiocatore addGiocatore) {
        Set<Giocatore> giocatori = giocatoriService.getGiocatori();
        String giocatoreNome = addGiocatore.getNome();
        Giocatore giocatore = new Giocatore(giocatoreNome);
        if (giocatori.contains(giocatore)) {
            throw new RuntimeException("Giocatore già presente: " + giocatoreNome);
        }
        giocatori.add(new Giocatore(giocatoreNome));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{nome}")
    public ResponseEntity<Void> update(@RequestBody AddGiocatore addGiocatore, @PathVariable String nome) {
        Set<Giocatore> giocatori = giocatoriService.getGiocatori();
        String nuovoNome = addGiocatore.getNome();
        if (!nuovoNome.equalsIgnoreCase(nome)) {
            long countNuovoNome = giocatori.stream().filter(g -> g.getNome().equalsIgnoreCase(nuovoNome)).count();
            if (countNuovoNome > 0) {
                throw new RuntimeException("Nuovo nome già presente: " + nuovoNome);
            }
            Giocatore daModificare = giocatori.stream().filter(g -> g.getNome().equalsIgnoreCase(nome)).findFirst().orElseThrow(() -> new RuntimeException("Giocatore da modificare non trovato: " + nome));
            daModificare.setNome(nuovoNome);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{nome}")
    public ResponseEntity<Void> delete(@PathVariable String nome) {
        Set<Giocatore> giocatori = giocatoriService.getGiocatori();
        boolean rimosso = giocatori.removeIf(g -> g.getNome().equalsIgnoreCase(nome));
        if (!rimosso) {
            throw new RuntimeException("Giocatore da cancellare non trovato: " + nome);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> reset() {
        giocatoriService.reset();
        return ResponseEntity.noContent().build();
    }

}