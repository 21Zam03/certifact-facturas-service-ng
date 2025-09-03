package com.certicom.certifact_facturas_service_ng.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoidedDocumentsDto {

    private Long idDocumentVoided;
    private Integer correlativoGeneracionDia;
    private String estado;
    private String fechaBajaDocs;
    private String fechaGeneracionBaja;
    private String idDocument;
    private String rucEmisor;
    private String ticketSunat;
    private String codigoRespuesta;
    private String descripcionRespuesta;
    private Timestamp fechaGeneracionResumen;
    private Timestamp fechaModificacion;
    private String userName;
    private String userNameModify;
    private String estadoComprobante;
    private List<DetailsDocsVoidedDto> detailBajaDocumentos;
    //private List<VoidedFileEntity> voidedFiles;
    private Integer intentosGetStatus;

    @JsonIgnore
    public List<DetailsDocsVoidedDto> getBajaDocumentos() {
        if (this.detailBajaDocumentos == null) {
            this.detailBajaDocumentos = new ArrayList<DetailsDocsVoidedDto>();
        }
        return this.detailBajaDocumentos;
    }

    @JsonIgnore
    public DetailsDocsVoidedDto addDetailDocsVoided(DetailsDocsVoidedDto detailDocsVoided) {
        getBajaDocumentos().add(detailDocsVoided);
        detailDocsVoided.setVoidedDocument(this);
        return detailDocsVoided;
    }

}
