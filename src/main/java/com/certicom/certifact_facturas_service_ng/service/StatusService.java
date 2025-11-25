package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.others.ResponsePSE;

public interface StatusService {

    public ResponsePSE getStatus(String numeroTicket, String tipoResumen,
                                 String userName, String rucEmisor);
    public String getEstadoDocumentoResumenInBD(String tipoDocumento, String numeroTicket);
}
