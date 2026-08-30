# 🚀 Quick Start - Ruota della Fortuna

## 1️⃣ Prerequisiti

Verifica di avere installato:
- ✅ Java 17 o superiore
- ✅ Maven
- ✅ Node.js 18 o superiore
- ✅ npm

## 2️⃣ Avvio (2 comandi)

### Windows

**Terminal 1:**
```bash
start-backend.bat
```

**Terminal 2:**
```bash
start-frontend.bat
```

### Linux/Mac

**Terminal 1:**
```bash
cd backend && mvn spring-boot:run
```

**Terminal 2:**
```bash
cd frontend && npm install && npm start
```

## 3️⃣ Gioca!

Apri il browser su: **http://localhost:4200**

## 🎮 Primi Passi

1. Clicca sull'input "Nome giocatore"
2. Scrivi un nome (es. "Mario")
3. Clicca "Aggiungi Giocatore"
4. Aggiungi altri giocatori se vuoi (opzionale)
5. Clicca "🎬 Avvia Gioco"
6. Clicca "🎰 GIRA LA RUOTA"
7. Segui le istruzioni a schermo!

## 📋 Regole Rapide

- **GIRA**: Premi il pulsante per girare la ruota
- **PARLA**: Puoi scegliere:
  - 📢 Chiamare una consonante (guadagni punti se la trovi)
  - 🅰️ Comprare una vocale (costa punti)
  - 🎯 Tentare la soluzione completa
- Se sbagli o becchi "PASSA" → turno passa
- Se becchi "BANCAROTTA" → perdi tutti i punti! 💥

## 🆘 Problemi?

### Backend non parte
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend non parte
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
npm start
```

### Errore CORS
Il file `backend/src/main/resources/application.yaml` deve contenere:
```yaml
ALLOWED_SERVERS: http://localhost:4200,http://localhost:8083
```

## 📚 Più Info

- **Istruzioni complete**: [ISTRUZIONI.md](ISTRUZIONI.md)
- **Documentazione tecnica**: [frontend/README.md](frontend/README.md)
- **Summary completo**: [SUMMARY.md](SUMMARY.md)

---

**Buon Divertimento! 🎉**
