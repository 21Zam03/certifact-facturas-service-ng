package com.certicom.certifact_facturas_service_ng.dto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OseDto {

    private Integer Id;
    private String urlFacturas;
    private String urlGuias;

}
