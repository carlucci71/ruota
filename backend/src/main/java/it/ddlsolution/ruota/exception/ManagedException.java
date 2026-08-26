package it.ddlsolution.ruota.exception;

import lombok.Data;

@Data
public class ManagedException extends RuntimeException{

    private InternalCode internalCode;
    public ManagedException(String msg, InternalCode internalCode){
        super(msg);
        this.internalCode=internalCode;
    }

    public enum InternalCode{
        PWD_LEGA_ERRATA, ALREADY_JOINED, CODE_LEGA_PRESENTE, LEGA_NOT_FOUND, NOT_LEADER,
        REQUEST_NOT_FOUND, REQUEST_ALREADY_EXISTS, LEGA_FULL, LEGA_NOT_PUBBLICA,
        GIOCATA_NOT_FOUND, EMOJI_NON_VALIDA, OPERAZIONE_NON_CONSENTITA
    }
}
