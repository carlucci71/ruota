# 🎡 Ruota della Fortuna - Applicazione Completa

Gioco della Ruota della Fortuna con backend Java Spring Boot e frontend Angular.

## 📋 Struttura del Progetto

```
ruota/
├── backend/          # Server Java Spring Boot
├── frontend/         # Client Angular
├── testerApi/        # Test API utilities
└── README.md         # Questo file
```

## 🚀 Avvio Rapido

### Windows

1. **Avvia il Backend** (in un terminale):
   ```bash
   start-backend.bat
   ```
   Il backend sarà disponibile su: http://localhost:8083/api/ruota

2. **Avvia il Frontend** (in un altro terminale):
   ```bash
   start-frontend.bat
   ```
   Il frontend sarà disponibile su: http://localhost:4200

### Manuale

1. **Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

2. **Frontend**:
   ```bash
   cd frontend
   npm install
   npm start
   ```

## 🎮 Come Giocare

1. Apri il browser su http://localhost:4200
2. Aggiungi almeno un giocatore
3. Clicca su "Avvia Gioco"
4. Gira la ruota e gioca!

Vedi [ISTRUZIONI.md](ISTRUZIONI.md) per dettagli completi sul gioco.

## 🔧 Tecnologie

- **Backend**: Java 17+, Spring Boot, Maven
- **Frontend**: Angular 18, TypeScript, SCSS
- **API**: REST, JSON

## 📚 Documentazione API

Swagger UI disponibile su:
http://localhost:8083/api/ruota/swagger-ui/index.html

## 🔗 Endpoint Principali

### Giocatori
- `POST /giocatore` - Aggiungi giocatore
- `DELETE /giocatore/{nome}` - Elimina giocatore
- `PUT /giocatore/{nome}` - Rinomina giocatore
- `DELETE /giocatore` - Reset tutti i giocatori

### Gioco
- `GET /game` - Info stato gioco
- `DELETE /game` - Reset gioco
- `POST /game` - Avvia partita
- `GET /game/gira` - Gira la ruota
- `GET /game/consonante` - Chiama consonante
- `GET /game/vocale` - Compra vocale
- `GET /game/soluzione` - Tenta soluzione

## 🐛 Risoluzione Problemi

### CORS Error
Se vedi errori CORS, verifica che nel file `backend/src/main/resources/application.yaml` ci sia:
```yaml
ALLOWED_SERVERS: http://localhost:4200,http://localhost:8083
```

### Port già in uso
- Backend: Cambia `PORT` in application.yaml
- Frontend: Modifica `angular.json` o usa `ng serve --port 4201`

## 📝 Note

- Il backend deve essere avviato PRIMA del frontend
- Assicurati di avere Java 17+ e Node.js 18+ installati
- Per ambienti di produzione, configura le variabili d'ambiente appropriate

## 📄 File di Configurazione

- `backend/src/main/resources/application.yaml` - Configurazione backend
- `backend/src/main/resources/ruota_fortuna_definizioni.csv` - Frasi del gioco
- `frontend/src/app/services/game.service.ts` - URL API frontend