package com.certicom.certifact_facturas_service_ng.dto.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class RegisterFileUploadDto {

    private String bucket;
    private String nombreOriginal;
    private String nombreGenerado;
    private Integer codCompany;
    private Boolean isOld;
    private String uuid;
    private String extensiones;
    private String rucCompany;

}
