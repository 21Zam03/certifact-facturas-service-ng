package com.certicom.certifact_facturas_service_ng.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoPdfEnum {

    A4("a4"),
    TICKET("ticket"),
    OTHER_CPE("other_cpe"),
    GUIA("guia"),
    GUIATRANSP("guia-transporte"),
    GUIA_TICKET("guia-ticket"),
    COTI("coti"),
    COTI_TICKET("coti-ticket");
    private final String tipo;

}
