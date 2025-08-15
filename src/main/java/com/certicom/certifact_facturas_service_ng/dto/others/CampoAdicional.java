package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampoAdicional {

    private String nombreCampo;
    private String valorCampo;

    /*
    public static List<CampoAdicional> transformToBeanList(List<CampoAdicionalEntity> adds) {
        List<CampoAdicional> resp = new ArrayList<>();
        if (adds == null) return resp;

        adds.forEach(aditionalFieldEntity -> {
            resp.add(CampoAdicional.builder()
                    .nombreCampo(aditionalFieldEntity.getTypeField().getName())
                    .valorCampo(aditionalFieldEntity.getValorCampo())
                    .build());
        });

        return resp;
    }*/

}
