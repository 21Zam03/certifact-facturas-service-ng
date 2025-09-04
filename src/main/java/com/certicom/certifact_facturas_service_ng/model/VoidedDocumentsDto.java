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
    private Integer intentosGetStatus;

    private List<DetailsDocsVoidedDto> detailBajaDocumentos;
    private List<VoidedFileDto> voidedFiles;

    @JsonIgnore
    public List<DetailsDocsVoidedDto> getOrCreateDocumentos() {
        if (this.detailBajaDocumentos == null) {
            this.detailBajaDocumentos = new ArrayList<DetailsDocsVoidedDto>();
        }
        return this.detailBajaDocumentos;
    }

    @JsonIgnore
    public List<VoidedFileDto> getOrCreateVoidedFiles() {
        if(this.voidedFiles == null) {
            this.voidedFiles = new ArrayList<>();
        }
        return this.voidedFiles;
    }

    @JsonIgnore
    public DetailsDocsVoidedDto addDetailDocsVoided(DetailsDocsVoidedDto detailDocsVoided) {
        getOrCreateDocumentos().add(detailDocsVoided);
        //detailDocsVoided.setVoidedDocument(this);
        return detailDocsVoided;
    }

    @JsonIgnore
    public void addVoidFile(VoidedFileDto voidedFileDto) {
        getOrCreateVoidedFiles().add(voidedFileDto);
        this.voidedFiles.add(voidedFileDto);
    }

}
