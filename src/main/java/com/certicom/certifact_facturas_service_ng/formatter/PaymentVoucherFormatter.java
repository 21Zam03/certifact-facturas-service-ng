package com.certicom.certifact_facturas_service_ng.formatter;

import com.certicom.certifact_facturas_service_ng.model.PaymentVoucher;
import com.certicom.certifact_facturas_service_ng.dto.others.Anticipo;
import com.certicom.certifact_facturas_service_ng.dto.others.ComprobanteItem;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Clase responsable de dar formato y normalizar valores del PaymentVoucherDto
 * antes de que sea procesado o guardado en la base de datos.
 *
 * No realiza validaciones de negocio ni reglas de dominio.
 */
@Component
@RequiredArgsConstructor
public class PaymentVoucherFormatter {

    private final PaymentVoucherDetailFormatter paymentVoucherDetailFormatter;

    public void formatPaymentVoucher(PaymentVoucher paymentVoucher) {
        calculateTotalValorVenta(paymentVoucher);
        formatTotalValorVenta(paymentVoucher);
        formatItems(paymentVoucher);
        formatAnticipos(paymentVoucher.getAnticipos());
        formatData(paymentVoucher);
    }

    private void formatData(PaymentVoucher paymentVoucher) {
        paymentVoucher.setRucEmisor(StringUtils.trimToNull(paymentVoucher.getRucEmisor()));
        paymentVoucher.setSerie(paymentVoucher.getSerie().toUpperCase());
        paymentVoucher.setHoraEmision(StringUtils.trimToNull(paymentVoucher.getHoraEmision()));
        paymentVoucher.setCodigoMoneda(StringUtils.trimToNull(paymentVoucher.getCodigoMoneda()));
        paymentVoucher.setCodigoLocalAnexoEmisor(StringUtils.trimToNull(paymentVoucher.getCodigoLocalAnexoEmisor()));
        paymentVoucher.setDenominacionReceptor(StringUtils.trimToNull(paymentVoucher.getDenominacionReceptor()));

        paymentVoucher.setCodigoTipoOtroDocumentoRelacionado(StringUtils.trimToNull(
                paymentVoucher.getCodigoTipoOtroDocumentoRelacionado()));
        paymentVoucher.setSerieNumeroOtroDocumentoRelacionado(StringUtils.trimToNull(
                paymentVoucher.getSerieNumeroOtroDocumentoRelacionado()));
        paymentVoucher.setCodigoTipoOperacion(StringUtils.trimToNull(paymentVoucher.getCodigoTipoOperacion()));
        paymentVoucher.setMotivoNota(StringUtils.trimToNull(paymentVoucher.getMotivoNota()));
        //paymentVoucher.setIdentificadorDocumento(identificadorDocumento);

        if (StringUtils.isBlank(paymentVoucher.getDenominacionReceptor())) {
            paymentVoucher.setDenominacionReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucher.getNumeroDocumentoReceptor())) {
            paymentVoucher.setNumeroDocumentoReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucher.getTipoDocumentoReceptor())) {
            paymentVoucher.setTipoDocumentoReceptor("-");
        }
    }

    private void calculateTotalValorVenta(PaymentVoucher paymentVoucher) {
        if (isNullOrZero(paymentVoucher.getTotalValorVentaGravada())
                && isNullOrZero(paymentVoucher.getTotalValorVentaExportacion())
                && isNullOrZero(paymentVoucher.getTotalValorVentaExonerada())
                && isNullOrZero(paymentVoucher.getTotalImpOperGratuita())
                && isNullOrZero(paymentVoucher.getTotalValorVentaInafecta())) {
            for (ComprobanteItem line: paymentVoucher.getItems() ) {
                switch (line.getCodigoTipoAfectacionIGV()){
                    case "20":
                        if(paymentVoucher.getTotalValorVentaExonerada()==null) {
                            paymentVoucher.setTotalValorVentaExonerada(BigDecimal.ZERO);
                        }
                        paymentVoucher.setTotalValorVentaExonerada(paymentVoucher.getTotalValorVentaExonerada().add(line.getValorVenta()));
                        break;
                }
            }
        }
    }

    private void formatAnticipos(List<Anticipo> anticipos) {
        int correlativo = 1;
        if (anticipos != null && !anticipos.isEmpty()) {
            for (int i=0; i<anticipos.size(); i++) {
                if (correlativo < 10) {
                    anticipos.get(i).setIdentificadorPago("0" + correlativo);
                } else {
                    anticipos.get(i).setIdentificadorPago(Integer.toString(correlativo));
                }
                correlativo++;
            }
        }
    }

    private void formatTotalValorVenta(PaymentVoucher paymentVoucher) {
        if (isNotNullAndZero(paymentVoucher.getTotalValorVentaGravada())) {
            paymentVoucher.setTotalValorVentaGravada(null);
        }
        if (isNotNullAndZero(paymentVoucher.getTotalValorVentaGratuita())) {
            paymentVoucher.setTotalValorVentaGratuita(null);
        }
        if (isNotNullAndZero(paymentVoucher.getTotalValorVentaExonerada())) {
            paymentVoucher.setTotalValorVentaExonerada(null);
        }
        if (isNotNullAndZero(paymentVoucher.getTotalValorVentaExportacion() )) {
            paymentVoucher.setTotalValorVentaExportacion(null);
        }
        if (isNotNullAndZero(paymentVoucher.getTotalValorVentaInafecta())) {
            paymentVoucher.setTotalValorVentaInafecta(null);
        }
        if (isNotNullAndZero(paymentVoucher.getTotalIgv())) {
            paymentVoucher.setTotalIgv(null);
        }
        if (paymentVoucher.getMontoDetraccion() != null) {
            paymentVoucher.setMontoDetraccion(paymentVoucher.getMontoDetraccion().setScale(2, RoundingMode.CEILING));
        }
        if (paymentVoucher.getTipoTransaccion() == null) {
            paymentVoucher.setTipoTransaccion(BigDecimal.ONE);
        }
    }

    private void formatItems(PaymentVoucher paymentVoucher) {
        for (ComprobanteItem line: paymentVoucher.getItems() ) {
            paymentVoucherDetailFormatter.format(line);
        }
    }

    private boolean isNotNullAndZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isNullOrZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }
}
