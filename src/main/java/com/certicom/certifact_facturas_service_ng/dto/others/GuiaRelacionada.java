package com.certicom.certifact_facturas_service_ng.dto.others;

import com.certicom.certifact_facturas_service_ng.entity.GuiaRelacionadaEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GuiaRelacionada {

    private String codigoTipoGuia;
    private String serieNumeroGuia;
    private Long idguiaremision;

    public static List<GuiaRelacionada> transformToBeanList(List<GuiaRelacionadaEntity> ebgs) {
        List<GuiaRelacionada> response = new ArrayList<>();
        if (ebgs == null) return null;

        ebgs.forEach(guiaRelacionadaEntity -> {
            response.add(GuiaRelacionada.builder()
                    .codigoTipoGuia(guiaRelacionadaEntity.getCodigoTipoGuia())
                    .serieNumeroGuia(guiaRelacionadaEntity.getSerieNumeroGuia())
                    .idguiaremision(guiaRelacionadaEntity.getIdguiaremision())
                    .build());
        });
        return response;
    }

}
