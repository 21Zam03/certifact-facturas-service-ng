package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.exceptions.QRGenerationException;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.CompanyData;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherData;
import com.certicom.certifact_facturas_service_ng.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.text.ParseException;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentVoucherData paymentVoucherData;
    private final CompanyData companyData;

    @Override
    public ByteArrayInputStream getPdfComprobanteuid(Long idPaymentVoucher, String uuid, String nameDocument, String tipoPdf) throws QRGenerationException, ParseException {
        /*
        PaymentVoucher comprobante = paymentVoucherFeign.findByIdPaymentVoucherAndUuid(idPaymentVoucher, uuid);
        if (comprobante == null) {
            throw new ServiceException("COMPROBANTE NO ENCONTRADO NULL");
        }
        if (!comprobante.getIdentificadorDocumento().trim().equalsIgnoreCase(nameDocument))
            throw new ServiceException("COMPROBANTE NO ENCONTRADO NAMEDOCUMENT");

        if (tipoPdf.equalsIgnoreCase(TipoPdfEnum.TICKET.getTipo()))
            return getPdfComprobanteTicket(comprobante.getRucEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
        else if (tipoPdf.equalsIgnoreCase(TipoPdfEnum.A4.getTipo()))
            return getPdfComprobanteA4(comprobante.getEmisor(), comprobante.getTipoComprobante(), comprobante.getSerie(), comprobante.getNumero());
        else throw new ServiceException("COMPROBANTE NO ENCONTRADO TICKETA4");
        * */
        return null;
    }

    @Override
    public ByteArrayInputStream getPdfComprobanteTicket(String ruc, String tipo, String serie, Integer numero) throws ServiceException, QRGenerationException, ParseException {
        /*
        PaymentVoucher interDto = paymentVoucherFeign
                .findByRucEmisorAndTipoComprobanteAndSerieAndNumero(ruc, tipo, serie, numero);
        if (interDto == null)
            throw new ServiceException("El comprobante que desea descargar no existe.");

        Company company = companyFeign.findCompanyByRuc(interDto.getRucEmisor());
        return new ByteArrayInputStream(getPdfComprobantePorTipoFormatoInter(interDto, company, TipoPdfEnum.TICKET.getTipo()));
        * */

        return null;
    }


}
