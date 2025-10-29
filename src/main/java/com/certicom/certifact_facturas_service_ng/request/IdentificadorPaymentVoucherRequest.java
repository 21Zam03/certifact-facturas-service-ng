package com.certicom.certifact_facturas_service_ng.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IdentificadorPaymentVoucherRequest {

    @NotNull
    private String tipo;
    @NotNull
    private String serie;
    @NotNull
    private String ruc;
    @NotNull
    private Integer numero;

}
