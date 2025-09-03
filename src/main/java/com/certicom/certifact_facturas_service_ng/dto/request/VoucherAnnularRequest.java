package com.certicom.certifact_facturas_service_ng.dto.request;

import com.certicom.certifact_facturas_service_ng.deserializer.VoucherAnnularDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = VoucherAnnularDeserializer.class)
public class VoucherAnnularRequest {

    private String tipoComprobante;
    private String serie;
    private Integer numero;
    private String rucEmisor;
    private String fechaEmision;
    private String tipoComprobanteRelacionado;
    private String motivoAnulacion;

}
