package it.ddlsolution.ruota.service;

import it.ddlsolution.ruota.dto.Giocatore;
import it.ddlsolution.ruota.dto.Tabellone;
import it.ddlsolution.ruota.util.Utility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@Getter
@RequiredArgsConstructor
public class GameService {
    private Giocatore giocatoreTurno;
    private Tabellone tabelloneTurno;
    private Tabellone tabelloneInProgress;
    private Set<Giocatore> giocatori;
    private Fase fase;

    enum Fase{INIZIALE}

    private final Utility utility;
    private List<Tabellone> tabelloni;
    public void setGiocatoreTurno(String nome) {
        giocatoreTurno = giocatori
                .stream()
                .filter(g -> g.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Giocatore non presente: " + nome));
    }

    public void setTabelloneTurno(Tabellone tabellone) {
        StringBuffer nuovaFrase = new StringBuffer();
        this.tabelloneTurno = tabellone;
        char[] charArray = tabellone.getFrase().toCharArray();
        for (char c : charArray) {
            if (c == ' ') {
                nuovaFrase.append(" ");
            } else {
                nuovaFrase.append(".");
            }
        }
        this.tabelloneInProgress = new Tabellone(tabellone.getTitolo() + "," + nuovaFrase.toString());
    }

    public void setTabelloni(List<Tabellone> tabelloni) {
        this.tabelloni=tabelloni;
    }

    public void addGiocatori(String giocatoreNome) {
        Giocatore giocatore = new Giocatore(giocatoreNome);
        if (giocatori.contains(giocatore)) {
            throw new RuntimeException("Giocatore già presente: " + giocatoreNome);
        }
        giocatori.add(new Giocatore(giocatoreNome));
    }

    public void resetGiocatori() {
        giocatori = new LinkedHashSet<>();
        addGiocatori("GIMMI");
    }

    public void resetTurno() {
        giocatoreTurno = null;
        tabelloneTurno = null;
        tabelloneInProgress = null;
    }

    public void resetFase() {
        fase=Fase.INIZIALE;
    }

    public void update(String nuovoNome, String nome) {
        if (!nuovoNome.equalsIgnoreCase(nome)) {
            long countNuovoNome = giocatori.stream().filter(g -> g.getNome().equalsIgnoreCase(nuovoNome)).count();
            if (countNuovoNome > 0) {
                throw new RuntimeException("Nuovo nome già presente: " + nuovoNome);
            }
            Giocatore daModificare = giocatori.stream().filter(g -> g.getNome().equalsIgnoreCase(nome)).findFirst().orElseThrow(() -> new RuntimeException("Giocatore da modificare non trovato: " + nome));
            daModificare.setNome(nuovoNome);
        }
    }

    public void deleteGiocatore(String nome) {
        boolean rimosso = giocatori.removeIf(g -> g.getNome().equalsIgnoreCase(nome));
        if (!rimosso) {
            throw new RuntimeException("Giocatore da cancellare non trovato: " + nome);
        }
    }

    public void avvia(String nomeGiocatoreAvvia) {
        Giocatore giocatore;
        if (nomeGiocatoreAvvia == null) {
            giocatore = giocatori.stream().toList().get(utility.randomUntil(giocatori.size()) - 1);
        } else {
            giocatore = giocatori.stream().toList().stream().filter(g -> g.getNome().equalsIgnoreCase(nomeGiocatoreAvvia)).findFirst().orElseThrow(() -> new RuntimeException("Giocatore non trovato: " + nomeGiocatoreAvvia));
        }
        setGiocatoreTurno(giocatore.getNome());
        int fraseRandom = utility.randomUntil(tabelloni.size());
        fraseRandom = 1;//TODO frase fissa
        Tabellone tabellone = tabelloni.get(fraseRandom);
        setTabelloneTurno(tabellone);
    }

    public List<Tabellone> getTabelloni(){
        return  tabelloni;
    }
}
