package it.ddlsolution.ruota.dto;

import lombok.Data;

@Data
public class Giocatore {
    private String nome;

    public Giocatore(String nome){
        this.nome= nome;
    }

    public Giocatore(){
        this.nome= "GIOCATORE";
    }

}
