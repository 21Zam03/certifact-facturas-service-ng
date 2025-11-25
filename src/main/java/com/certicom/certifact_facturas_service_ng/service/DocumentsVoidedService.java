package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.others.Voided;
import com.certicom.certifact_facturas_service_ng.model.VoidedDocumentsModel;
import com.certicom.certifact_facturas_service_ng.request.VoucherAnnularRequest;
import com.certicom.certifact_facturas_service_ng.dto.others.ResponsePSE;

import java.util.List;

public interface DocumentsVoidedService {

    VoidedDocumentsModel registrarVoidedDocuments(Voided voided, Long idRegisterFile, String usuario, String ticket);
    ResponsePSE anularDocuments(List<VoucherAnnularRequest> documents, String rucEmisor, String userName, List<String> ticketsVoidedProcess);
    Boolean processVoidedTicket(String ticket, String useName, String rucEmisor);

}
