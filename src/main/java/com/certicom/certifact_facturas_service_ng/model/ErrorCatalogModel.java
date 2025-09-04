package com.certicom.certifact_facturas_service_ng.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorCatalogModel {

    private Integer id;
    private String code;
    private String description;
    private String type;
    private String document;

}
