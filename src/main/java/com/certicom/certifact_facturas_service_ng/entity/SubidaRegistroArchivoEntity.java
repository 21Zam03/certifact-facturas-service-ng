package com.certicom.certifact_facturas_service_ng.entity;

import lombok.*;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SubidaRegistroArchivoEntity {

    private static final long serialVersionUID = 1L;

    private Long idRegisterFileSend;
    private String extension;
    private Timestamp fechaUpload;
    private String bucket;
    private String nombreOriginal;
    private String nombreGenerado;
    private String rucCompany;
    private String tipoArchivo;
    private String usuarioUpload;
    private String uuid;
    //private CompanyEntity company;
    private String estado;
    private Timestamp fechaModificacion;
    private String userNameModify;

    private Boolean isOld;

}
