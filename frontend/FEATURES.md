# Screenshot e Funzionalità

## Interfaccia Principale

L'applicazione è divisa in 3 sezioni principali:

### 1. 👥 Gestione Giocatori
- Campo input per aggiungere nuovi giocatori
- Lista giocatori con:
  - Nome giocatore
  - Punteggio totale (💰)
  - Punteggio manche corrente (🎯)
  - Badge speciali (🃏 JOLLY, 🚗 GARAGE)
- Pulsante per eliminare singoli giocatori
- Pulsante per resettare tutti i giocatori

### 2. 🎲 Tabellone di Gioco
- **Categoria**: Mostra la categoria della frase da indovinare
- **Frase**: Visualizza le lettere scoperte e quelle nascoste (?)
  - Ogni lettera è in un box colorato
  - Le lettere scoperte appaiono con animazione
  - Gli spazi sono rispettati
- **Status**: Indica se consonanti/vocali sono ancora disponibili
- **Turno Corrente**: Mostra quale giocatore deve giocare
  - Evidenziato in viola
  - Mostra punteggio corrente
  - Indica eventuali bonus attivi
- **Fase di Gioco**: Badge colorato che indica la fase
  - 🔵 GIRA: Devi girare la ruota
  - 🔴 PARLA: Puoi chiamare lettera o tentare soluzione
  - 🟢 FINE: Partita conclusa

### 3. 🎮 Azioni di Gioco

#### Prima della partita:
- Input per nome tabellone (opzionale)
- 🎬 **Avvia Gioco**: Inizia una nuova partita
- 🔄 **Reset Completo**: Resetta tutto (giocatori + gioco)

#### Durante la partita:

**Gira la Ruota** (solo in fase GIRA):
- Pulsante grande colorato
- Mostra il risultato dello spin
- Possibili risultati:
  - Numeri (100, 200, 300, 500, ecc.)
  - JOLLY (protezione bancarotta)
  - GARAGE (protezione bancarotta)
  - TRIPLO (triplica i punti)
  - BANCAROTTA (perdi tutto!)
  - PASSA (turno passa)
  - CRESCE (valore incrementale)

**Chiama Consonante** (solo in fase PARLA, dopo aver girato):
- Input per la consonante
- Il valore ottenuto dalla ruota viene moltiplicato per il numero di lettere trovate
- Se la lettera non c'è, passa il turno

**Compra Vocale** (solo in fase PARLA):
- 5 pulsanti grandi con le vocali: A, E, I, O, U
- Costa punti dal punteggio del giocatore
- Rivela tutte le occorrenze della vocale
- Se la vocale non c'è, passa il turno

**Tenta la Soluzione** (solo in fase PARLA):
- Input per scrivere la frase completa
- Se corretta: VINCI! 🎉
- Se sbagliata: passa il turno

## Messaggi

I messaggi appaiono in alto nelle azioni con colori diversi:
- 🟢 **Verde (Success)**: Azione riuscita, lettere trovate, vittoria
- 🔴 **Rosso (Error)**: Errore, lettera già chiamata, soluzione sbagliata
- 🔵 **Blu (Info)**: Informazioni generali, reset

## Esempio di Flusso di Gioco

1. **Setup**:
   - Aggiungi "Mario" → ✅ Giocatore Mario aggiunto!
   - Aggiungi "Luigi" → ✅ Giocatore Luigi aggiunto!
   - Clicca "Avvia Gioco" → ✅ Gioco avviato! Gira la ruota!

2. **Turno di Mario**:
   - Stato: "🎯 Turno di: MARIO"
   - Fase: GIRA
   - Clicca "GIRA LA RUOTA" → Risultato: 500
   - Fase diventa: PARLA
   - Chiama consonante "R" → Trovate 3 R! (+1500 punti)
   - Fase ritorna: GIRA

3. **Mario rigira**:
   - Clicca "GIRA LA RUOTA" → Risultato: PASSA
   - Turno passa automaticamente a Luigi

4. **Turno di Luigi**:
   - Stato: "🎯 Turno di: LUIGI"
   - Clicca "GIRA LA RUOTA" → Risultato: 300
   - Compra vocale "A" → Trovate 2 A! (costa punti)
   - Clicca "GIRA LA RUOTA" → Risultato: BANCAROTTA
   - Luigi perde tutti i punti! Turno passa a Mario

5. **Mario tenta la soluzione**:
   - Clicca "GIRA LA RUOTA" → Risultato: 200
   - Scrive nel campo soluzione: "ROMA CAPUT MUNDI"
   - Clicca "RISOLVI" → 🎉 SOLUZIONE CORRETTA! HAI VINTO! 🎉

## Caratteristiche UX

- **Design Responsivo**: Funziona su desktop e mobile
- **Animazioni**: Le lettere appaiono con effetto scale
- **Colori Vivaci**: Gradient colorati per una UX accattivante
- **Feedback Immediato**: Messaggi chiari per ogni azione
- **Stati Disabilitati**: I pulsanti sono disabilitati quando non utilizzabili
- **Conferme**: Dialoghi di conferma per azioni importanti (reset, eliminazioni)
