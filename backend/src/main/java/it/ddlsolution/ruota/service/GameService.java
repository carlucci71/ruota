package it.ddlsolution.ruota.service;

import it.ddlsolution.ruota.dto.Giocatore;
import it.ddlsolution.ruota.dto.Tabellone;
import it.ddlsolution.ruota.util.Utility;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
    private Map<Character, List<Integer>> posLettere;
    private Set<Giocatore> giocatori;
    private List<Tabellone> tabelloni;
    private Fase fase;
    private TipoManche tipoManche;
    private boolean jollyUse;
    private boolean garageUse;
    private boolean raddoppiaUse;
    private int valoreCresce;

    public void incrementaPuntiManche(int punti) {
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        giocatoreCorrente.setPuntiManche(giocatoreCorrente.getPuntiManche() + punti);
    }

    private Giocatore getGiocatoreCorrente() {
        String nome = giocatoreTurno.getNome();
        Giocatore giocatoreCorrente = giocatori.stream().filter(g -> g.getNome().equalsIgnoreCase(nome)).findFirst().orElseThrow(() -> new RuntimeException("Giocatore da modificare non trovato: " + nome));
        return giocatoreCorrente;
    }


    public void bancarotta() {
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        if (giocatoreCorrente.isWithJolly()) {
            giocatoreCorrente.setWithJolly(false);
        } else {
            giocatoreCorrente.setPuntiManche(0);
            giocatoreCorrente.setPuntiTotale(0);
            nextGiocatore();
        }
        fase = Fase.GIRA;
    }

    public Object gira(String forzato) {
        if (fase != Fase.GIRA) {
            throw new RuntimeException("Puoi girare solo se sei nella fase GIRA, ora sei in fase: " + fase.name());
        }
        List<Object> ruotaBase = ruotaBase();
        List<Object> ruota = new ArrayList<>();
        for (Object spicchio : ruotaBase) {
            if (spicchio.equals(SpicchiCustom.GARAGE)) {
                if (garageUse) {
                    spicchio = 500;
                }
            }
            if (spicchio.equals(SpicchiCustom.JOLLY)) {
                if (jollyUse) {
                    spicchio = 100;
                }
            }
            if (spicchio.equals(SpicchiCustom.CRESCE)) {
                spicchio = valoreCresce;
            }
            if (spicchio.equals(SpicchiCustom.TRIPLO)) {
                if (raddoppiaUse) {
                    spicchio = SpicchiCustom.BANCAROTTA;
                }
            }
            ruota.add(spicchio);
        }
        fase = Fase.PARLA;
        Object ottenuto = getSpicchio(ruota, forzato);
        if (ottenuto.equals(SpicchiCustom.TRIPLO)) {
            int randomTriplo = utility.randomUntil(3);
            if (ottenuto.toString().equals(forzato)) {
                ottenuto = SpicchiCustom.RADDOPPIA;
            } else {
                if (randomTriplo == 1 || randomTriplo == 3) {
                    ottenuto = SpicchiCustom.BANCAROTTA;
                } else {
                    ottenuto = SpicchiCustom.RADDOPPIA;
                }
            }
        }
        if (ottenuto.equals(SpicchiCustom.PASSA)) {
            nextGiocatore();
        }
        if (ottenuto.equals(SpicchiCustom.BANCAROTTA)) {
            bancarotta();
        }
        if (ottenuto.equals(SpicchiCustom.JOLLY)) {
            jollyUse = true;
        }
        if (ottenuto.equals(SpicchiCustom.GARAGE)) {
            garageUse = true;
        }
        if (ottenuto.equals(SpicchiCustom.RADDOPPIA)) {
            raddoppiaUse = true;
        }
        return ottenuto;
    }

    private Object getSpicchio(List ruota, String forzato) {
        if (forzato == null) {
            return ruota.get(utility.randomUntil(ruota.size()) - 1);
        } else {
            for (Object o : ruota) {
                if (o.toString().equals(forzato)) return o;
                //if (o.toString().equals(forzato.equals(SpicchiCustom.TRIPLO.name()) ? SpicchiCustom.RADDOPPIA.name():forzato)) return o;

            }
        }
        throw new RuntimeException("Impossibile forzare: " + forzato);
    }

    public int adaptLettera(Character lettera) {
        StringBuffer nuovaFrase = new StringBuffer();
        int ret = 0;
        char[] frase = getTabelloneTurno().getFrase().toCharArray();
        char[] inProgress = getTabelloneInProgress().getFrase().toCharArray();
        Set<Character> consonantiMancanti = new HashSet<>();
        Set<Character> vocaliMancanti = new HashSet<>();
        for (int i = 0; i < frase.length; i++) {
            char attInProgress = inProgress[i];
            char attChar = frase[i];
            Character attCharConverted = convertiCarattere(attChar);
            if (attInProgress == PLACEHOLDER && attCharConverted == lettera) {
                ret++;
                nuovaFrase.append(attChar);
            } else {
                nuovaFrase.append(attInProgress);
                if (attInProgress == PLACEHOLDER) {
                    try {
                        VocaliAmmesse.valueOf(String.valueOf(attCharConverted));
                        vocaliMancanti.add(attCharConverted);
                    } catch (Exception e) {
                    }
                    try {
                        ConsonantiAmmesse.valueOf(String.valueOf(attCharConverted));
                        consonantiMancanti.add(attCharConverted);
                    } catch (Exception e) {
                    }
                }
            }
        }
        this.tabelloneInProgress = new Tabellone(tabelloneInProgress.getTitolo() + "," + nuovaFrase);
        getTabelloneInProgress().setConsonantiFinite(consonantiMancanti.size() == 0);
        getTabelloneInProgress().setVocaliFinite(vocaliMancanti.size() == 0);
        return ret;
    }

    public void adaptLetteraPosizione(int posizione) {
        StringBuffer nuovaFrase = new StringBuffer();
        char[] frase = getTabelloneTurno().getFrase().toCharArray();
        char[] inProgress = getTabelloneInProgress().getFrase().toCharArray();
        for (int i = 0; i < frase.length; i++) {
            char attInProgress = inProgress[i];
            char attChar = frase[i];
            if (attInProgress == PLACEHOLDER && posizione == i) {
                nuovaFrase.append(attChar);
            } else {
                nuovaFrase.append(attInProgress);
            }
        }
        this.tabelloneInProgress = new Tabellone(tabelloneInProgress.getTitolo() + "," + nuovaFrase);
    }

    Character convertiCarattere(char c) {
        Character confronta;
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
        posLettere = new HashMap<>();
        this.tabelloneTurno = tabellone;
        char[] frase = tabellone.getFrase().toCharArray();
        for (int i = 0; i < frase.length; i++) {
            char c = frase[i];
            if (c == ' ') {
                nuovaFrase.append(" ");
            } else if (c == '\'') {
                nuovaFrase.append("'");
            } else {
                Character attInProgress = convertiCarattere(c);
                posLettere.computeIfAbsent(attInProgress, (x) -> new ArrayList<>()).add(i);
                nuovaFrase.append(PLACEHOLDER);
            }
        }
        this.tabelloneInProgress = new Tabellone(tabellone.getTitolo() + "," + nuovaFrase);
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

    public void reset() {
        giocatoreTurno = null;
        tabelloneTurno = null;
        tabelloneInProgress = null;
        garageUse = false;
        raddoppiaUse = false;
        jollyUse = false;
        valoreCresce = 1000;
        fase = Fase.SETUP;
        tipoManche = TipoManche.AUTO_SINGOLA_CHIAMATA;
        for (Giocatore giocatore : giocatori) {
            giocatore.setPuntiTotale(0);
        }
    }

    public void nextGiocatore() {
        List<Giocatore> list = new ArrayList<>(giocatori);
        int idx = list.indexOf(giocatoreTurno);
        if (idx == -1) throw new NoSuchElementException();
        giocatoreTurno = list.get((idx + 1) % list.size());
        fase = Fase.GIRA;
    }

    public void addJollyGiocatore() {
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        giocatoreCorrente.setWithJolly(true);
    }

    public void raddoppiaGiocatore() {
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        giocatoreCorrente.setPuntiManche(giocatoreCorrente.getPuntiManche() * 2);
    }

    public void garageGiocatore() {
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        giocatoreCorrente.setWithGarage(true);
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
            Giocatore giocatoreCorrente = giocatori.stream().filter(g -> g.getNome().equalsIgnoreCase(nome)).findFirst().orElseThrow(() -> new RuntimeException("Giocatore da modificare non trovato: " + nome));
            giocatoreCorrente.setNome(nuovoNome);
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
        fase = Fase.GIRA;
    }

    public List<Tabellone> getTabelloni() {
        return tabelloni;
    }

    public void setTabelloni(List<Tabellone> tabelloni) {
        this.tabelloni = tabelloni;
    }

    public Map<String, Object> buildInfo() {
        Map<String, Object> ret = new LinkedHashMap<>();
        List<Tabellone> tabelloni = getTabelloni();
        ret.put("Tabelloni", tabelloni.size());
        ret.put("Tabellone titolo", getTabelloneTurno() == null ? "--" : getTabelloneTurno().getTitolo());
        ret.put("TabelloneInProgress", getTabelloneInProgress() == null ? "--" : getTabelloneInProgress().getFraseOK());
        ret.put("VocaliFinite", getTabelloneInProgress() == null ? "--" : getTabelloneInProgress().isVocaliFinite());
        ret.put("ConsonantiFinite", getTabelloneInProgress() == null ? "--" : getTabelloneInProgress().isConsonantiFinite());
        ret.put("GiocatoreTurno", getGiocatoreTurno() == null ? "--" : getGiocatoreTurno().getNome());
        ret.put("Giocatori", getGiocatori());
        ret.put("Fase", getFase());
        ret.put("TipoManche", getTipoManche());
        return ret;
    }

    public Map<String, Object> chiamaConsonante(Character consonante, Object trovato) {
        if (fase != Fase.PARLA) {
            throw new RuntimeException("Puoi girare solo se sei nella fase PARLA, ora sei in fase: " + fase.name());
        }
        if (isConsonante(consonante) == false) {
            throw new RuntimeException("La consonante non è ammessa: " + consonante);
        }
        int trovate = adaptLettera(consonante);
        int numero;
        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("TROVATE", trovate);
        if (trovato.equals(GameService.SpicchiCustom.JOLLY.name())) {
            if (trovate > 0) {
                addJollyGiocatore();
            } else {
                nextGiocatore();
            }
        } else if (trovato.equals(GameService.SpicchiCustom.RADDOPPIA.name())) {
            if (trovate > 0) {
                raddoppiaGiocatore();
            } else {
                nextGiocatore();
            }
        } else if (trovato.equals(GameService.SpicchiCustom.GARAGE.name())) {
            if (trovate > 0) {
                garageGiocatore();
            } else {
                nextGiocatore();
            }
        } else {
            numero = Integer.parseInt(trovato.toString());
            int punti = numero * trovate;
            incrementaPuntiManche(punti);
            ret.put("PUNTI", punti);
        }
        if (trovate == 0) {
            nextGiocatore();
        }
        fase = Fase.GIRA;
        return ret;
    }

    private boolean isVocale(Character lettera) {
        try {
            VocaliAmmesse.valueOf(String.valueOf(lettera));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean isConsonante(Character lettera) {
        try {
            ConsonantiAmmesse.valueOf(String.valueOf(lettera));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Map<String, Object> compraVocale(Character vocale) {
        if (fase != Fase.GIRA) {
            throw new RuntimeException("Puoi girare solo se sei nella fase GIRA, ora sei in fase: " + fase.name());
        }
        if (isVocale(vocale) == false) {
            throw new RuntimeException("La vocale non è ammessa: " + vocale);
        }
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        if (giocatoreCorrente.getPuntiManche() < 500) {
            throw new RuntimeException("Non hai soldi a sufficienza: " + giocatoreCorrente.getPuntiManche());
        }
        incrementaPuntiManche(-500);
        int trovate = adaptLettera(vocale);
        if (trovate == 0) {
            nextGiocatore();
        }
        Map<String, Object> ret = new HashMap<>();
        ret.put("TROVATE", trovate);
        fase = Fase.GIRA;
        return ret;
    }

    public Map<String, Object> soluzione(String soluzione) {
        Map ret = new HashMap();
        if (soluzione.equalsIgnoreCase(getTabelloneTurno().getFrase())) {
            ret.put("ESITO", "OK");
            Giocatore giocatoreCorrente = getGiocatoreCorrente();
            giocatoreCorrente.setPuntiTotale(giocatoreCorrente.getPuntiTotale() + giocatoreCorrente.getPuntiManche() + 1000);
            giocatoreCorrente.setPuntiManche(0);
            valoreCresce = valoreCresce + 1000;
            nextGiocatore();
            tipoManche = TipoManche.STANDARD;
            avvia(getGiocatoreCorrente().getNome());
        } else {
            ret.put("ESITO", "KO");
            nextGiocatore();
        }
        fase = Fase.GIRA;
        return ret;
    }

    public Map<String, Object> autoSingolaChiamata() {
        Map<String, Object> ret = new HashMap<>();
        //casuale da 1 a 20. Se maggiore di 3 vocale altrimenti consonante
        boolean isVocale = true;
        int randomed = utility.randomUntil(20);
        if (randomed < 3) {
            isVocale = false;
        }
        List<Character> caratteri = posLettere.keySet().stream().toList();
        Character lettera = null;
        do {
            randomed = utility.randomUntil(caratteri.size()) - 1;
            Character carattere = caratteri.get(randomed);
            if ((isVocale && isVocale(carattere)) || (!isVocale && isConsonante(carattere))) {
                lettera = carattere;
            }
        } while (lettera == null);
        List<Integer> posizioniLettera = posLettere.get(lettera);
        randomed = utility.randomUntil(posizioniLettera.size()) - 1;
        Integer posizione = posizioniLettera.get(randomed);
        adaptLetteraPosizione(posizione);
        posizioniLettera.remove(posizione);
        if (posizioniLettera.size() == 0) {
            posLettere.remove(lettera);
        }
        ret.put("POSIZIONE", posizione);
        return ret;
    }

    enum Fase {SETUP, GIRA, PARLA}

    enum TipoManche {AUTO_SINGOLA_CHIAMATA, STANDARD}

    public enum SpicchiCustom {PASSA, GARAGE, TRIPLO, BANCAROTTA, JOLLY, CRESCE, RADDOPPIA}

    public enum VocaliAmmesse {A, E, I, O, U}

    public enum ConsonantiAmmesse {B, C, D, F, G, H, L, M, N, P, Q, R, S, T, V, Z, J, K, W, X, Y}
}
