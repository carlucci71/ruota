# Avvio Applicazione Ruota della Fortuna

## 1. Avviare il Backend

```bash
cd backend
mvn spring-boot:run
```

Il backend sarà disponibile su: http://localhost:8083/api/ruota

## 2. Avviare il Frontend

In un nuovo terminale:

```bash
cd frontend
npm start
```

Il frontend sarà disponibile su: http://localhost:4200

## 3. Configurazione CORS nel Backend

Se riscontri problemi CORS, assicurati che il file `SecurityConfig.java` contenga:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

## Come Giocare

1. **Aggiungi Giocatori**: Inserisci i nomi dei giocatori (almeno 1)
2. **Avvia il Gioco**: Clicca su "Avvia Gioco" (puoi specificare un nome per il tabellone)
3. **Gira la Ruota**: Clicca su "GIRA LA RUOTA" per ottenere uno spicchio
4. **Fai la tua mossa**:
   - **Chiama Consonante**: Se ottieni punti, chiama una consonante
   - **Compra Vocale**: Costa punti, ma rivela vocali
   - **Tenta la Soluzione**: Se pensi di conoscere la frase completa
5. **Continua a giocare**: Il turno passa automaticamente in caso di errore o "PASSA"

## Spicchi Speciali

- **JOLLY**: Ti protegge dalla bancarotta (una volta)
- **GARAGE**: Ti protegge dalla bancarotta (sostituisce lo spicchio)
- **BANCAROTTA**: Perdi tutti i punti!
- **PASSA**: Il turno passa al giocatore successivo
- **TRIPLO**: Triplica i punti!
- **CRESCE**: Valore che aumenta ogni volta

## Note

- Solo il giocatore di turno può fare azioni
- Le vocali costano punti (vengono sottratti dal totale)
- La soluzione deve essere esatta (inclusi accenti e punteggiatura)
