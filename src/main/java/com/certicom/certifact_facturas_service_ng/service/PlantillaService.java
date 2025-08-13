package com.certicom.certifact_facturas_service_ng.service;

import com.certicom.certifact_facturas_service_ng.dto.model.ComprobanteDto;
import com.certicom.certifact_facturas_service_ng.exceptions.FirmaException;
import com.certicom.certifact_facturas_service_ng.exceptions.PlantillaException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public interface PlantillaService {

    public Map<String, String> buildPaymentVoucherSignOse(ComprobanteDto comprobanteDto);
    public Map<String, String> buildPaymentVoucherSignOseBliz(ComprobanteDto comprobanteDto);
    public Map<String, String> buildPaymentVoucherSignCerti(ComprobanteDto comprobanteDto) throws PlantillaException, FirmaException, IOException, NoSuchAlgorithmException;
    public Map<String, String> buildPaymentVoucherSign(ComprobanteDto comprobanteDto);

}
