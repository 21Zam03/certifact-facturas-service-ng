package com.certicom.certifact_facturas_service_ng.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class OseDto {

    private Integer Id;
    private String urlFacturas;
    private String urlGuias;

}
