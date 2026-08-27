package it.ddlsolution.ruota.component;

import it.ddlsolution.ruota.dto.Tabellone;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class InitTabelloni {
    @Bean
    public List<Tabellone> lista() {
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
        return tabelloni;
    }
}