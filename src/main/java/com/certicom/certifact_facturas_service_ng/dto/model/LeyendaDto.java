package com.certicom.certifact_facturas_service_ng.dto.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LeyendaDto {

    private String descripcion;
    private String codigo;

}
