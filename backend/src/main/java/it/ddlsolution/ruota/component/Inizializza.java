package it.ddlsolution.ruota.component;

import it.ddlsolution.ruota.dto.Tabellone;
import it.ddlsolution.ruota.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


@Component
public class Inizializza implements CommandLineRunner {
    @Autowired
    GameService gameService;

    @Override
    public void run(String... args) throws Exception {
        List<Tabellone> tabelloni = new ArrayList<>();
        try (InputStream inputStream = new ClassPathResource("ruota_fortuna_definizioni.csv").getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("windows-1252")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                tabelloni.add(new Tabellone(line));
            }

        } catch (Exception e) {
            throw new RuntimeException("Errore nella lettura del CSV da classpath", e);
        }
        gameService.resetGiocatori();
        gameService.setTabelloni(tabelloni);
        gameService.resetFase();
    }
}