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
public class VoidedDocumentsModel {

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

    private List<DetailsDocsVoidedModel> detailBajaDocumentos;
    private List<VoidedFileModel> voidedFileModelList;

    @JsonIgnore
    public List<DetailsDocsVoidedModel> getOrCreateDocumentos() {
        if (this.detailBajaDocumentos == null) {
            this.detailBajaDocumentos = new ArrayList<DetailsDocsVoidedModel>();
        }
        return this.detailBajaDocumentos;
    }

    @JsonIgnore
    public List<VoidedFileModel> getOrCreateVoidedFiles() {
        if(this.voidedFileModelList == null) {
            this.voidedFileModelList = new ArrayList<>();
        }
        return this.voidedFileModelList;
    }

    @JsonIgnore
    public DetailsDocsVoidedModel addDetailDocsVoided(DetailsDocsVoidedModel detailDocsVoided) {
        getOrCreateDocumentos().add(detailDocsVoided);
        //detailDocsVoided.setVoidedDocument(this);
        return detailDocsVoided;
    }

    @JsonIgnore
    public void addVoidFile(VoidedFileModel voidedFileModel) {
        System.out.println("voided file dto: "+ voidedFileModel);
        if (getOrCreateVoidedFiles().isEmpty()) {
            voidedFileModel.setOrden(1);
        } else {
            voidedFileModel.setOrden(getOrCreateVoidedFiles().size()+1);
        }
        getOrCreateVoidedFiles().add(voidedFileModel);
    }

}
