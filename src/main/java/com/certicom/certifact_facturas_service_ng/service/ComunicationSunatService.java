package com.certicom.certifact_facturas_service_ng.service;

import java.util.Map;

public interface ComunicationSunatService {

    public Map<String, Object> sendDocumentBill(String ruc, Long idPaymentVoucher);

}
