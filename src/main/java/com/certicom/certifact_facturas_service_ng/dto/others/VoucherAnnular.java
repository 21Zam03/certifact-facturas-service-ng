package com.certicom.certifact_facturas_service_ng.dto.others;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherAnnular {

    private String tipoComprobante;
    private String serie;
    private Integer numero;
    private String rucEmisor;
    private String fechaEmision;
    private String tipoComprobanteRelacionado;
    private String motivoAnulacion;

}
