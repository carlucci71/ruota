package it.ddlsolution.ruota.service;

import it.ddlsolution.ruota.dto.Giocatore;
import it.ddlsolution.ruota.dto.Tabellone;
import it.ddlsolution.ruota.util.Utility;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    private Giocatore giocatoreIniziaManche;
    private Tabellone tabelloneTurno;
    private Tabellone tabelloneInProgress;
    private Map<Character, List<Integer>> posLettere;
    private Set<Giocatore> giocatori;
    private String nomeGiocatorePrenotato;
    private List<Tabellone> tabelloni;
    private Fase fase;
    private Integer valoreUltimoGiro;
    private boolean jollyUse;
    private boolean garageUse;
    private boolean raddoppiaUse;
    private int contaCiakUse;
    private int contaPopcornUse;
    private List<Integer> posizioniCinema;
    List<Manche> manches = new ArrayList<>();
    private int mancheCorrente;
    private int contaChiamateNascoste;

    private Giocatore getGiocatoreCorrente() {
        String nome;
        if (manches.get(mancheCorrente).tipoManche == TipoManche.STANDARD
                || manches.get(mancheCorrente).tipoManche == TipoManche.DOPO_CAMPANELLA
        ) {
            nome = giocatoreTurno.getNome();
        } else {
            nome = nomeGiocatorePrenotato;
        }
        Giocatore giocatoreCorrente = giocatori.stream().filter(g -> g.getNome().equalsIgnoreCase(nome)).findFirst().orElseThrow(() -> new RuntimeException("Giocatore da modificare non trovato: " + nome));
        return giocatoreCorrente;
    }

    public void incrementaPuntiManche(int punti) {
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        giocatoreCorrente.setPuntiManche(giocatoreCorrente.getPuntiManche() + punti);
    }

    public void distribuisciPuntiManche() {
        Giocatore giocatoreCorrente = getGiocatoreCorrente();
        int puntiMancheProCapite = giocatoreCorrente.getPuntiManche() / (giocatori.size() - 1);
        giocatoreCorrente.setPuntiManche(0);
        for (Giocatore giocatore : giocatori) {
            if (!giocatore.getNome().equalsIgnoreCase(giocatoreCorrente.getNome())) {
                giocatore.setPuntiManche(puntiMancheProCapite + giocatore.getPuntiManche());
            }
        }
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

    public List<Object> ruotaBase() {
        return List.of(
                SpicchiCustom.PASSA, 500, SpicchiCustom.GARAGE, 600, 200, 800
                , SpicchiCustom.TRIPLO, 300, 500, 100, 400, 800
                , SpicchiCustom.PASSA, 600, 300, 700, 200, SpicchiCustom.CRESCE
                , SpicchiCustom.BANCAROTTA, 500, SpicchiCustom.JOLLY, 700, 200, 400
        );
    }

    public Object gira(String forzato) {
        if (fase != Fase.GIRA) {
            throw new RuntimeException("Puoi girare solo se sei nella fase GIRA, ora sei in fase: " + fase.name());
        }
        List<Object> ruotaBase = ruotaBase();
        List<Object> ruota = new ArrayList<>();
        for (int i = 0; i < ruotaBase.size(); i++) {
            Object spicchio = ruotaBase.get(i);
            if (manches.get(mancheCorrente).isCinema && posizioniCinema != null && posizioniCinema.contains(i)) {
                spicchio = SpicchiCustom.CINEMA;
            }
            if (spicchio.equals(SpicchiCustom.GARAGE)) {
                if (garageUse || mancheCorrente == manches.size() - 1) {
                    spicchio = 500;
                }
            }
            if (spicchio.equals(SpicchiCustom.JOLLY)) {
                if (jollyUse || mancheCorrente == manches.size() - 1) {
                    spicchio = 100;
                }
            }
            if (spicchio.equals(SpicchiCustom.CRESCE)) {
                spicchio = manches.get(mancheCorrente).valoreCresce;
            }
            if (spicchio.equals(SpicchiCustom.TRIPLO)) {
                if (raddoppiaUse || mancheCorrente == manches.size() - 1) {
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
        if (ottenuto.equals(SpicchiCustom.CINEMA)) {
            int ciak = 2 - contaCiakUse;
            int popCorn = 1 - contaPopcornUse;
            int randomed = utility.randomUntil(ciak + popCorn);
            if (randomed > contaCiakUse) {
                ottenuto = SpicchiCustom.CIAK;
                contaCiakUse++;
            } else {
                ottenuto = SpicchiCustom.POPCORN;
                contaPopcornUse++;
            }
            if (posizioniCinema.isEmpty()) {
                posizioniCinema = new ArrayList<>();
            } else {
                posizioniCinema = new ArrayList<>(posizioniCinema.subList(1, posizioniCinema.size()));
            }
            fase = Fase.GIRA;
        }
        if (ottenuto.equals(SpicchiCustom.CIAK)) {
            incrementaPuntiManche(5000);
        }
        if (ottenuto.equals(SpicchiCustom.POPCORN)) {
            distribuisciPuntiManche();
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
        if (ottenuto.equals(SpicchiCustom.CINEMA)) {
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

    public void adaptLetteraPosizione(int posizione, boolean nascondi) {
        StringBuffer nuovaFrase = new StringBuffer();
        char[] frase = getTabelloneTurno().getFrase().toCharArray();
        char[] inProgress = tabelloneInProgress.getFrase().toCharArray();
        for (int i = 0; i < frase.length; i++) {
            char attInProgress = inProgress[i];
            char attChar = frase[i];
            if (attInProgress == PLACEHOLDER && posizione == i) {
                nuovaFrase.append(attChar);
            } else {
                if (nascondi) {
                    if (attChar == ' ' || attChar == '\'') {
                        nuovaFrase.append(attChar);
                    } else {
                        nuovaFrase.append(PLACEHOLDER);
                    }
                } else {
                    nuovaFrase.append(attInProgress);
                }
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
        addGiocatori("BUBU");
    }

    public void reset() {
        giocatoreTurno = null;
        tabelloneTurno = null;
        tabelloneInProgress = null;
        garageUse = false;
        raddoppiaUse = false;
        jollyUse = false;
        valoreUltimoGiro = null;
        giocatoreIniziaManche =null;
        fase = Fase.SETUP;
        for (Giocatore giocatore : giocatori) {
            giocatore.setPuntiTotale(0);
            giocatore.setPuntiManche(0);
            giocatore.setWithGarage(false);
            giocatore.setWithJolly(false);
        }
            /*
            Piatti Estivi
            AUTO_SINGOLA_CHIAMATA

            In fondo al mar
            1000

            Tormentoni
            2000

            Ciak, si gira
            3000
            Include i Ciak con la stella (da 5.000 € l’uno) e il temuto Ciak con i popcorn

            Compiti per le vacanze
            4000

            Triplete
            AUTO_SINGOLA_CHIAMATA
            AUTO_SINGOLA_CHIAMATA
            AUTO_SINGOLA_CHIAMATA_NASCONDI

            Ultimo Round
            5000
             */

        manches = List.of(
                new Manche(CategoriaManche.PIATTI_ESTIVI, TipoManche.AUTO_SINGOLA_CHIAMATA, null, null, false),
                new Manche(CategoriaManche.IN_FONDO_AL_MAR, TipoManche.STANDARD, 1000, null, false),
                new Manche(CategoriaManche.TORMENTONI,TipoManche.STANDARD, 2000, null, false),
                new Manche(CategoriaManche.CIAK_SI_GIRA,TipoManche.STANDARD, 3000, null, true),
                new Manche(CategoriaManche.COMPITI_PER_LE_VACANZE,TipoManche.STANDARD, 4000, null, false),
                new Manche(CategoriaManche.TRIPLETE,TipoManche.AUTO_SINGOLA_CHIAMATA, null, 1, false),
                new Manche(CategoriaManche.TRIPLETE,TipoManche.AUTO_SINGOLA_CHIAMATA, null, 2, false),
                new Manche(CategoriaManche.TRIPLETE,TipoManche.AUTO_SINGOLA_CHIAMATA_NASCONDI, null, 3, false),
                new Manche(CategoriaManche.ULTIMO_TURNO, TipoManche.STANDARD, 5000, null, false)
        );
        mancheCorrente = 0;

    }

    public void nextGiocatore() {
        giocatoreTurno = askNextGiocatore(giocatoreTurno);
        fase = Fase.GIRA;
    }

    public Giocatore askNextGiocatore(Giocatore giocatore) {
        List<Giocatore> list = new ArrayList<>(giocatori);
        int idx = list.indexOf(giocatore);
        if (idx == -1) throw new NoSuchElementException();
        return list.get((idx + 1) % list.size());
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

    private boolean campanellaUltimoGiro() {
        int x = 10;//1 su x non suona
        int random = utility.randomUntil(x);
        if (random < x) {
            return true;
        } else {
            return false;
        }
    }

    private void sceltaUltimoGiro() {
        if (campanellaUltimoGiro()) {
            Object gira;
            do {
                gira = gira(null);
            } while (gira == SpicchiCustom.PASSA
                    || gira == SpicchiCustom.GARAGE
                    || gira == SpicchiCustom.TRIPLO
                    || gira == SpicchiCustom.BANCAROTTA
                    || gira == SpicchiCustom.JOLLY
            );
            valoreUltimoGiro = Integer.valueOf(gira.toString());
            if (!valoreUltimoGiro.equals(manches.get(mancheCorrente).valoreCresce)) {
                valoreUltimoGiro = 1000 + valoreUltimoGiro;
            }
            Manche manche = manches.get(mancheCorrente);
            manche.tipoManche = TipoManche.DOPO_CAMPANELLA;
            fase = Fase.TENTA;
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
        contaChiamateNascoste = 0;
        fase = Fase.GIRA;
        if (mancheCorrente == manches.size() - 1) {
            sceltaUltimoGiro();
        }
        if (manches.get(mancheCorrente).isCinema) {
            contaCiakUse = 0;
            contaPopcornUse = 0;
            posizioniCinema = List.of(10, 18, 21);
            /*
700
Ciak
400
Passa
500
300
600
200
800
Bancarotta


3000
Ciak

500
100
Ciak
800
Passa
             */
        }
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
        ret.put("Giocatori", giocatori);
        ret.put("Fase", fase);
        ret.put("TipoManche", manches.get(mancheCorrente).tipoManche);
        ret.put("CategoriaManche", manches.get(mancheCorrente).categoriaManche);
        ret.put("ValoreCresce", manches.get(mancheCorrente).valoreCresce);
//        ret.put("PosLettere", posLettere);
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

        if (valoreUltimoGiro != null) {
            ret.put("SPICCHIO", valoreUltimoGiro);
            if (trovate>0) {
                fase = Fase.TENTA;
            } else {
                fase = Fase.PARLA;
            }
        } else {
            fase = Fase.GIRA;
        }
        if (mancheCorrente == manches.size() - 1 && valoreUltimoGiro == null) {
            sceltaUltimoGiro();
            ret.put("SPICCHIO", valoreUltimoGiro);
        }
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
            giocatoreCorrente.setPuntiTotale(giocatoreCorrente.getPuntiTotale() + giocatoreCorrente.getPuntiManche() + (mancheCorrente == manches.size() - 1 ? 0 : 1000));
            giocatoreCorrente.setPuntiManche(0);

            if (mancheCorrente == 0) {
                giocatoreIniziaManche =giocatoreCorrente;
            } else{
                giocatoreIniziaManche =askNextGiocatore(giocatoreIniziaManche);
            }
            if (mancheCorrente + 1 < manches.size()) {
                mancheCorrente++;
                avvia(giocatoreIniziaManche.getNome());
                if (valoreUltimoGiro == null) {
                    fase = Fase.GIRA;
                } else {
                    ret.put("SPICCHIO", valoreUltimoGiro);

                }
            } else {
                ret.put("FINE", "OK");
                fase = Fase.FINE;
            }
        } else {
            ret.put("ESITO", "KO");
            nextGiocatore();
            if (valoreUltimoGiro == null) {
                fase = Fase.GIRA;
            } else {
                ret.put("SPICCHIO", valoreUltimoGiro);
                fase = Fase.PARLA;
            }
        }
        return ret;
    }

    /**
     * Fase TENTA scaduta: nessuno ha dato la soluzione, il turno passa al giocatore successivo.
     */
    public Map<String, Object> passa() {
        Map<String, Object> ret = new HashMap<>();
        if (fase != Fase.TENTA) {
            throw new RuntimeException("Puoi passare solo se sei nella fase TENTA, ora sei in fase: " + fase.name());
        }
        ret.put("ESITO", "KO");
        nextGiocatore();
        fase = Fase.PARLA;
        return ret;
    }

    public Map<String, Object> prenota(String nome) {
        Map<String, Object> ret = new HashMap<>();
        this.nomeGiocatorePrenotato = nome;
        ret.put("GiocatorePrenotato", nome);
        return ret;
    }

    public Map<String, Object> autoSingolaChiamata(boolean nascondi) {
        if (nascondi) {
            contaChiamateNascoste++;
        }
        if (contaChiamateNascoste == 10) {
            Manche manche = manches.get(mancheCorrente);
            manche.tipoManche = TipoManche.AUTO_SINGOLA_CHIAMATA;
            setTabelloneTurno(tabelloneTurno);
            contaChiamateNascoste = 0;

        }
        Map<String, Object> ret = new HashMap<>();
        if (posLettere.size() > 0) {
            //casuale da 1 a 20. Se maggiore di 3 vocale altrimenti consonante
            List<Character> caratteri = posLettere.keySet().stream().toList();
            boolean isVocale = true;
            int randomed = utility.randomUntil(20);
            if (randomed < 3) {
                isVocale = false;
            }
            //Se isVocale ma le vocali sono finite allora lo forzo a false
            if (isVocale && caratteri.stream().filter(c -> isVocale(c)).count() == 0) {
                isVocale = false;
            }
            //Se !isVocale ma le consonanti sono finite allora lo forzo a true
            if (!isVocale && caratteri.stream().filter(c -> isConsonante(c)).count() == 0) {
                isVocale = true;
            }
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
            adaptLetteraPosizione(posizione, nascondi);
            posizioniLettera.remove(posizione);
            if (posizioniLettera.size() == 0) {
                posLettere.remove(lettera);
            }
            ret.put("POSIZIONE", posizione);
            caratteri = posLettere.keySet().stream().toList();
            getTabelloneInProgress().setConsonantiFinite(caratteri.stream().filter(c -> isConsonante(c)).count() == 0);
            getTabelloneInProgress().setVocaliFinite(caratteri.stream().filter(c -> isVocale(c)).count() == 0);
        } else {
            getTabelloneInProgress().setConsonantiFinite(true);
            getTabelloneInProgress().setVocaliFinite(true);
        }
        return ret;
    }

    enum Fase {SETUP, GIRA, PARLA, FINE, TENTA}

    enum TipoManche {AUTO_SINGOLA_CHIAMATA, AUTO_SINGOLA_CHIAMATA_NASCONDI, STANDARD, DOPO_CAMPANELLA}

    public enum SpicchiCustom {PASSA, GARAGE, TRIPLO, BANCAROTTA, JOLLY, CRESCE, RADDOPPIA, CINEMA, CIAK, POPCORN}

    public enum CategoriaManche {PIATTI_ESTIVI, TRIPLETE, ULTIMO_TURNO, CIAK_SI_GIRA, TORMENTONI, IN_FONDO_AL_MAR, COMPITI_PER_LE_VACANZE,}

    @Data
    @AllArgsConstructor
    static class Manche {
        CategoriaManche categoriaManche;
        TipoManche tipoManche;
        Integer valoreCresce;
        Integer mancheTriplete;
        Boolean isCinema;
    }

    public enum VocaliAmmesse {
        A, E, I, O, U
    }

    public enum ConsonantiAmmesse {B, C, D, F, G, H, L, M, N, P, Q, R, S, T, V, Z, J, K, W, X, Y}

}
