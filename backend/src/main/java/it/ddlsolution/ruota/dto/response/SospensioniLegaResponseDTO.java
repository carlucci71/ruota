package it.ddlsolution.ruota.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class SospensioniLegaResponseDTO {
    private Long idLega;
    private List<Integer> giornate;
}
