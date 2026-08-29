package it.ddlsolution.ruota.dto;

import it.ddlsolution.ruota.service.GameService;
import lombok.Data;

@Data
public class Tabellone {
    private String titolo;
    private String frase;

    public Tabellone(String riga){
        String[] split = riga.split(",");
        titolo = split[0].toUpperCase();
        frase = split[1].toUpperCase();
    }

    public String getFraseOK() {
        StringBuffer sb = new StringBuffer();
        char[] charArray = frase.toCharArray();
        for (int i = 0;i<charArray.length;i++){
            char c = charArray[i];
            //Scrivo ' solo se il carattere prima non è placeholder
            if (c != '\'' || (c == '\'' && charArray[i-1] != GameService.PLACEHOLDER)){
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
