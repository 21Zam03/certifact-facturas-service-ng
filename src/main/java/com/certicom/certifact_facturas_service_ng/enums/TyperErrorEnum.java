package com.certicom.certifact_facturas_service_ng.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TyperErrorEnum {

    ERROR("ERROR"),
    WARNING("OBSERV");
    private final String type;

}
