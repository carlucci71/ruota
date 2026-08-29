package it.ddlsolution.ruota.service;

import it.ddlsolution.ruota.dto.Giocatore;
import it.ddlsolution.ruota.dto.Tabellone;
import it.ddlsolution.ruota.util.Utility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
@Getter
@RequiredArgsConstructor
public class GameService {
    public final static char PLACEHOLDER = '-';
    private final Utility utility;
    private Giocatore giocatoreTurno;
    private Tabellone tabelloneTurno;
    private Tabellone tabelloneInProgress;
    private Set<Giocatore> giocatori;
    private Fase fase;
    private boolean garageUse;
    private boolean triploUse;
    private boolean jollyUse;
    private int valoreCresce;
    private List<Tabellone> tabelloni;

    public void incrementaPuntiManche(int punti) {
        String nome = giocatoreTurno.getNome();
        Giocatore daModificare = giocatori.stream().filter(g -> g.getNome().equalsIgnoreCase(nome)).findFirst().orElseThrow(() -> new RuntimeException("Giocatore da modificare non trovato: " + nome));
        daModificare.setPuntiManche(daModificare.getPuntiManche() + punti);
    }


    public void bancarotta() {
        String nome = giocatoreTurno.getNome();
        Giocatore daModificare = giocatori.stream().filter(g -> g.getNome().equalsIgnoreCase(nome)).findFirst().orElseThrow(() -> new RuntimeException("Giocatore da modificare non trovato: " + nome));
        if (daModificare.isWithJolly()){
            daModificare.setWithJolly(false);
        } else {
            daModificare.setPuntiManche(0);
            daModificare.setPuntiTotale(0);
            nextGiocatore();
        }
    }

    public Object gira(String forzato) {
        if (fase != Fase.INIZIALE && fase != Fase.GIOCA) {
            throw new RuntimeException("Puoi chiamare solo se sei nella fase iniziale, ora sei in fase: " + fase.name());
        }
        List<Object> ruotaBase = ruotaBase();
        List<Object> ruota = new ArrayList<>();
        for (Object spicchio : ruotaBase) {
            if (spicchio.equals(SpicchiCustom.GARAGE)) {
                if (garageUse) {
                    spicchio = 500;
                } else {
                    garageUse = true;
                }
            }
            if (spicchio.equals(SpicchiCustom.JOLLY)) {
                if (jollyUse) {
                    spicchio = 100;
                } else {
                    jollyUse = true;
                }
            }
            if (spicchio.equals(SpicchiCustom.CRESCE)) {
                spicchio = valoreCresce;
            }
            if (spicchio.equals(SpicchiCustom.TRIPLO)) {
                if (triploUse) {
                    spicchio = SpicchiCustom.BANCAROTTA;
                } else {
                    int randomTriplo = utility.randomUntil(3);
                    if (randomTriplo == 1 || randomTriplo == 3) {
                        spicchio = SpicchiCustom.BANCAROTTA;
                    } else {
                        spicchio = SpicchiCustom.RADDOPPIA;
                    }
                    triploUse=true;
                }
            }
            ruota.add(spicchio);
        }
        fase = Fase.CHIAMA;
        Object ottenuto = getSpicchio(ruota, forzato);
        if (ottenuto.equals(SpicchiCustom.PASSA)) {
            nextGiocatore();
        }
        if (ottenuto.equals(SpicchiCustom.BANCAROTTA)) {
            bancarotta();
        }
        return ottenuto;
    }

    private Object getSpicchio(List ruota,String forzato){
        if (forzato==null){
            return ruota.get(utility.randomUntil(ruota.size()) - 1);
        } else {
            for (Object o : ruota) {
                if (o.toString().equals(forzato)) return o;
            }
        }
        throw new RuntimeException("Impossibile forzare: " + forzato);
    }

    public int consonante(Character lettera) {
        StringBuffer nuovaFrase = new StringBuffer();
        int ret = 0;
        char[] frase = getTabelloneTurno().getFrase().toCharArray();
        char[] inProgress = getTabelloneInProgress().getFrase().toCharArray();
        for (int i = 0; i < frase.length; i++) {
            char attInProgress = inProgress[i];
            char attChar = frase[i];
            //cc = [ ', À, È, Ì, Ò, Ù]

            if (attInProgress == PLACEHOLDER && convertiCarattere(attChar) == lettera) {
                ret++;
                nuovaFrase.append(attChar);
            } else {
                nuovaFrase.append(attInProgress);
            }
        }
        this.tabelloneInProgress = new Tabellone(tabelloneInProgress.getTitolo() + "," + nuovaFrase);
        fase = Fase.GIOCA;
        return ret;
    }

    Character convertiCarattere(char c) {
        Character confronta = null;

        switch (c) {
            case 'À' -> {
                confronta = 'A';
            }
            case 'È' -> {
                confronta = 'E';
            }
            case 'Ì' -> {
                confronta = 'I';
            }
            case 'Ò' -> {
                confronta = 'O';
            }
            case 'Ù' -> {
                confronta = 'U';
            }
            default -> {
                confronta = c;
            }
        }

        return confronta;
    }

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
            } else if (c == '\'') {
                nuovaFrase.append("'");
            } else {
                nuovaFrase.append(PLACEHOLDER);
            }
        }
        this.tabelloneInProgress = new Tabellone(tabellone.getTitolo() + "," + nuovaFrase.toString());
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

    public void reset() {
        garageUse = false;
        triploUse = false;
        jollyUse = false;
        valoreCresce = 1000;
        fase = Fase.INIZIALE;
        for (Giocatore giocatore : giocatori) {
            giocatore.setPuntiTotale(0);
        }
    }

    private void nextGiocatore(){
        List<Giocatore> list = new ArrayList<>(giocatori);
        int idx = list.indexOf(giocatoreTurno);
        if (idx == -1) throw new NoSuchElementException();
        giocatoreTurno = list.get((idx + 1) % list.size());
        fase = Fase.GIOCA;
    }

    public List<Object> ruotaBase() {
        /*
        garage --> ?
        triplo --> bancarotta
        1000 (cresce)
        jolly (100)

PASSA x2
GARAGE
TRIPLO
CRESCE
BANCAROTTA
JOLLY
100
200 x3
300 x2
400 x2
500 x3
600 x2
700 x2
800 x2

         */
        return List.of(
                SpicchiCustom.PASSA, 500, SpicchiCustom.GARAGE, 600, 200, 800,
                SpicchiCustom.TRIPLO, 300, 500, 100, 400, 800,
                SpicchiCustom.PASSA, 600, 300, 700, 200, SpicchiCustom.CRESCE,
                SpicchiCustom.BANCAROTTA, 500, SpicchiCustom.JOLLY, 700, 200, 400
        );
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
        fraseRandom = 0;//TODO frase fissa
        Tabellone tabellone = tabelloni.get(fraseRandom);
        setTabelloneTurno(tabellone);
    }

    public List<Tabellone> getTabelloni() {
        return tabelloni;
    }

    public void setTabelloni(List<Tabellone> tabelloni) {
        this.tabelloni = tabelloni;
    }

    enum Fase {INIZIALE, CHIAMA, GIOCA}

    enum SpicchiCustom {PASSA, GARAGE, TRIPLO, BANCAROTTA, JOLLY, CRESCE, RADDOPPIA}
}
