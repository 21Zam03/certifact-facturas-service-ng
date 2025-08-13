package com.certicom.certifact_facturas_service_ng.dto.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EmpresaDto {

    private Integer Id;

    private String getRuc;
    private Boolean getViewCode;
    private String getRazon;
    private String getEmail;
    private String getTelefono;
    private String getCadena;
    private Boolean getRubroHoreal;
    private String getNombreComer;
    private String getUblVersion;
    private Integer OseId;
    private String getBucket;
    private Boolean getEnvioAutomaticoSunat;
    private Boolean getEnvioAutomaticoGuia;
    private Boolean getEnvioAutomaticoReteperse;
    private Boolean getEnvioDirecto;
    private String getDireccion;
    private Boolean getSimultaneo;
    private Boolean allowSaveOficina;
    private Integer getCantComproDina;
    private BigDecimal getIdRegisterFileSend;
    private Integer getFormat;

}
