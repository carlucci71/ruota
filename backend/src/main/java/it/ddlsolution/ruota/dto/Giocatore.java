package it.ddlsolution.ruota.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class Giocatore {
    private String nome;
    private int puntiTotale;
    private int puntiManche;
    private boolean withJolly;

    public Giocatore(String nome){
        this.nome= nome;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Giocatore giocatore)) return false;

        return Objects.equals(getNome().toUpperCase(), giocatore.getNome().toUpperCase());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getNome().toUpperCase());
    }
}
