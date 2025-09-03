package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.model.Voided;
import com.certicom.certifact_facturas_service_ng.model.VoidedDocumentsDto;
import com.certicom.certifact_facturas_service_ng.dto.request.VoucherAnnularRequest;
import com.certicom.certifact_facturas_service_ng.dto.response.ResponsePSE;

import java.util.List;

public interface DocumentsVoidedService {

    VoidedDocumentsDto registrarVoidedDocuments(Voided voided, Long idRegisterFile, String usuario, String ticket);
    ResponsePSE anularDocuments(List<VoucherAnnularRequest> documents, String rucEmisor, String userName, List<String> ticketsVoidedProcess);

}
