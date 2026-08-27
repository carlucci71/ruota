package it.ddlsolution.ruota.service;

import it.ddlsolution.ruota.dto.Giocatore;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class GiocatoriService {
    private Set<Giocatore> giocatori = new HashSet<>();

    public GiocatoriService() {
        reset();
    }

    public Set<Giocatore> getGiocatori() {
        return giocatori;
    }

    public void reset() {
        giocatori.clear();
        giocatori.add(new Giocatore("GIMMI"));
    }
}
