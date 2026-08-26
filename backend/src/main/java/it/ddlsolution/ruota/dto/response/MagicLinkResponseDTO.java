package it.ddlsolution.ruota.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MagicLinkResponseDTO {
    private String message;
    private boolean success;
    /** Valorizzato solo per l'account riservato alla review Apple/Google: permette il login senza email reale. */
    private String reviewToken;
    private String reviewCodiceTipoMagicLink;

    public MagicLinkResponseDTO(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}

