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

L'applicazione sarà disponibile su http://localhost:4800

### Accesso da un altro PC (rete locale)

Il dev server è configurato per ascoltare su tutte le interfacce di rete:

```bash
npm start
```

Dall'altro PC apri nel browser: `http://<IP-DEL-PC-SERVER>:4800` (es. `http://192.168.0.43:4800`).
Per scoprire l'IP del server: `ipconfig getifaddr en0` (macOS) o `ipconfig` (Windows).

> Le chiamate al backend (`/api`) vengono inoltrate automaticamente dal proxy di sviluppo
> (`proxy.conf.js`) a `http://localhost:8083` **sul PC che esegue `ng serve`**,
> quindi il backend deve essere avviato solo sul PC server.
> Il proxy rimuove anche l'header `Origin` del browser, evitando l'errore
> `Invalid CORS request` del backend anche quando il frontend è aperto
> da un altro PC / da un IP pubblico (es. `http://85.235.148.177:4800`).

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

### Aggiornamenti Real-Time (WebSocket)
- Il frontend si connette a `ws://<host>/api/ruota/game/ws`
- Ogni azione (giro ruota, chiamata lettera, giocatori, …) viene diffusa a
  tutti i client connessi: i tabelloni aperti su più dispositivi restano
  sincronizzati senza ricaricare la pagina
- La connessione si riconnette automaticamente (backoff esponenziale) se cade

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
