# Configurazione CORS per il Frontend

Per permettere al frontend Angular di comunicare con il backend, aggiungi questa variabile d'ambiente:

## Windows (PowerShell)
```powershell
$env:ALLOWED_SERVERS = "http://localhost:4200"
```

## Windows (CMD)
```cmd
set ALLOWED_SERVERS=http://localhost:4200
```

## Linux/Mac
```bash
export ALLOWED_SERVERS=http://localhost:4200
```

## Oppure modifica application.yaml

Aggiungi alla fine del file `backend/src/main/resources/application.yaml`:

```yaml
# CORS Configuration
ALLOWED_SERVERS: http://localhost:4200,http://localhost:8083
```

Poi riavvia il backend.
