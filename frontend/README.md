# Ruota della Fortuna - Frontend Angular

Client Angular per giocare alla Ruota della Fortuna.

## Prerequisiti

- Node.js (v18 o superiore)
- npm
- Backend Java in esecuzione su http://localhost:8083

## Installazione

```bash
npm install
```

## Avvio

```bash
npm start
```

L'applicazione sarà disponibile su http://localhost:4200

## Funzionalità

### Gestione Giocatori
- ➕ Aggiungi giocatori
- ❌ Elimina giocatori
- 🔄 Reset tutti i giocatori

### Gioco
- 🎬 Avvia una nuova partita
- 🎰 Gira la ruota
- 📢 Chiama consonanti
- 🅰️ Compra vocali (costo punti)
- 🎯 Tenta la soluzione completa

### Tabellone
- Visualizza la categoria
- Mostra le lettere scoperte
- Indica consonanti/vocali disponibili
- Mostra giocatore di turno e punteggi

### Stati del Gioco
- **GIRA**: Devi girare la ruota
- **PARLA**: Puoi chiamare consonante, comprare vocale o tentare la soluzione
- **FINE**: Partita terminata

## Struttura

```
frontend/
├── src/
│   ├── app/
│   │   ├── components/
│   │   │   ├── giocatori.component.ts    # Gestione giocatori
│   │   │   ├── tabellone.component.ts    # Visualizzazione tabellone
│   │   │   └── azioni.component.ts       # Azioni di gioco
│   │   ├── models/
│   │   │   └── game.model.ts             # Modelli TypeScript
│   │   ├── services/
│   │   │   └── game.service.ts           # Servizio API
│   │   └── app.component.ts              # Componente principale
│   ├── styles.scss                       # Stili globali
│   ├── index.html
│   └── main.ts
├── angular.json
├── package.json
└── tsconfig.json
```

## Note

- Assicurati che il backend sia avviato prima di lanciare il frontend
- Il backend deve essere in ascolto su http://localhost:8083/api/ruota
- Per problemi CORS, il backend deve configurare i CORS headers appropriati
