package com.certicom.certifact_facturas_service_ng.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoidedFileModel {

    private Long id;
    private String tipoArchivo;
    private String estadoArchivo;
    private Integer orden;
    private Long idRegisterFileSend;
    private Long idDocumentVoided;

    //private RegisterFileUploadEntity registerFileUpload;
    //private VoidedDocumentsEntity voidedDocument;

}
