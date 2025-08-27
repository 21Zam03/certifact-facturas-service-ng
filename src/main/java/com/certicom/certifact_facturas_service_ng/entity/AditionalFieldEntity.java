package com.certicom.certifact_facturas_service_ng.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AditionalFieldEntity {

    private Integer id;
    private String nombreCampo;
    private String valorCampo;
    private Integer campoAdicionalEntityId;
    private Long comprobanteEntityId;

}
