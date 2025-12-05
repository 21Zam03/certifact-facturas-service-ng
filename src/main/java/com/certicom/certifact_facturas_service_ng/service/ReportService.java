package com.certicom.certifact_facturas_service_ng.service;


import com.certicom.certifact_facturas_service_ng.exceptions.QRGenerationException;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;

import java.io.ByteArrayInputStream;
import java.text.ParseException;

public interface ReportService {

    ByteArrayInputStream getPdfComprobanteA4(String ruc, String tipo, String serie, Integer numero) throws QRGenerationException, ParseException;
    ByteArrayInputStream getPdfComprobanteuid(Long idPaymentVoucher, String uuid, String nameDocument, String tipo) throws QRGenerationException, ParseException;
    ByteArrayInputStream getPdfComprobanteTicket(String ruc, String tipo, String serie, Integer numero) throws ServiceException, QRGenerationException, ParseException;


}
