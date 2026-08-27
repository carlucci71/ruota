package it.ddlsolution.ruota.dto;

import lombok.Data;

@Data
public class Tabellone {
    private String titolo;
    private String frase;

    public Tabellone(String riga){
        String[] split = riga.split(",");
        titolo = split[0];
        frase = split[1];
    }

}
