package com.certicom.certifact_facturas_service_ng.formatter;

import com.certicom.certifact_facturas_service_ng.model.PaymentVoucherModel;
import com.certicom.certifact_facturas_service_ng.dto.others.Anticipo;
import com.certicom.certifact_facturas_service_ng.dto.others.ComprobanteItem;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Clase responsable de dar formato y normalizar valores del PaymentVoucherModel
 * antes de que sea procesado o guardado en la base de datos.
 *
 * No realiza validaciones de negocio ni reglas de dominio.
 */
@Component
@RequiredArgsConstructor
public class PaymentVoucherFormatter {

    private final PaymentVoucherDetailFormatter paymentVoucherDetailFormatter;

    public void formatPaymentVoucher(PaymentVoucherModel paymentVoucherModel) {
        calculateTotalValorVenta(paymentVoucherModel);
        formatTotalValorVenta(paymentVoucherModel);
        formatItems(paymentVoucherModel);
        formatAnticipos(paymentVoucherModel.getAnticipos());
        formatData(paymentVoucherModel);
    }

    private void calculateTotalValorVenta(PaymentVoucherModel paymentVoucherModel) {
        if (isNullOrZero(paymentVoucherModel.getTotalValorVentaGravada())
                && isNullOrZero(paymentVoucherModel.getTotalValorVentaExportacion())
                && isNullOrZero(paymentVoucherModel.getTotalValorVentaExonerada())
                && isNullOrZero(paymentVoucherModel.getTotalImpOperGratuita())
                && isNullOrZero(paymentVoucherModel.getTotalValorVentaInafecta())) {
            for (ComprobanteItem line: paymentVoucherModel.getItems() ) {
                switch (line.getCodigoTipoAfectacionIGV()){
                    case "20":
                        if(paymentVoucherModel.getTotalValorVentaExonerada()==null) {
                            paymentVoucherModel.setTotalValorVentaExonerada(BigDecimal.ZERO);
                        }
                        paymentVoucherModel.setTotalValorVentaExonerada(paymentVoucherModel.getTotalValorVentaExonerada().add(line.getValorVenta()));
                        break;
                }
            }
        }
    }

    private void formatTotalValorVenta(PaymentVoucherModel paymentVoucherModel) {
        if (isNotNullAndZero(paymentVoucherModel.getTotalValorVentaGravada())) {
            paymentVoucherModel.setTotalValorVentaGravada(null);
        }
        if (isNotNullAndZero(paymentVoucherModel.getTotalValorVentaGratuita())) {
            paymentVoucherModel.setTotalValorVentaGratuita(null);
        }
        if (isNotNullAndZero(paymentVoucherModel.getTotalValorVentaExonerada())) {
            paymentVoucherModel.setTotalValorVentaExonerada(null);
        }
        if (isNotNullAndZero(paymentVoucherModel.getTotalValorVentaExportacion() )) {
            paymentVoucherModel.setTotalValorVentaExportacion(null);
        }
        if (isNotNullAndZero(paymentVoucherModel.getTotalValorVentaInafecta())) {
            paymentVoucherModel.setTotalValorVentaInafecta(null);
        }
        if (isNotNullAndZero(paymentVoucherModel.getTotalIgv())) {
            paymentVoucherModel.setTotalIgv(null);
        }
        if (paymentVoucherModel.getMontoDetraccion() != null) {
            paymentVoucherModel.setMontoDetraccion(paymentVoucherModel.getMontoDetraccion().setScale(2, RoundingMode.CEILING));
        }
        if (paymentVoucherModel.getTipoTransaccion() == null) {
            paymentVoucherModel.setTipoTransaccion(BigDecimal.ONE);
        }
    }

    private void formatItems(PaymentVoucherModel paymentVoucherModel) {
        for (ComprobanteItem line: paymentVoucherModel.getItems() ) {
            paymentVoucherDetailFormatter.format(line);
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

    private void formatData(PaymentVoucherModel paymentVoucherModel) {
        paymentVoucherModel.setRucEmisor(StringUtils.trimToNull(paymentVoucherModel.getRucEmisor()));
        paymentVoucherModel.setSerie(paymentVoucherModel.getSerie().toUpperCase());
        paymentVoucherModel.setHoraEmision(StringUtils.trimToNull(paymentVoucherModel.getHoraEmision()));
        paymentVoucherModel.setCodigoMoneda(StringUtils.trimToNull(paymentVoucherModel.getCodigoMoneda()));
        paymentVoucherModel.setCodigoLocalAnexoEmisor(StringUtils.trimToNull(paymentVoucherModel.getCodigoLocalAnexoEmisor()));
        paymentVoucherModel.setDenominacionReceptor(StringUtils.trimToNull(paymentVoucherModel.getDenominacionReceptor()));

        paymentVoucherModel.setCodigoTipoOtroDocumentoRelacionado(StringUtils.trimToNull(
                paymentVoucherModel.getCodigoTipoOtroDocumentoRelacionado()));
        paymentVoucherModel.setSerieNumeroOtroDocumentoRelacionado(StringUtils.trimToNull(
                paymentVoucherModel.getSerieNumeroOtroDocumentoRelacionado()));
        paymentVoucherModel.setCodigoTipoOperacion(StringUtils.trimToNull(paymentVoucherModel.getCodigoTipoOperacion()));
        paymentVoucherModel.setMotivoNota(StringUtils.trimToNull(paymentVoucherModel.getMotivoNota()));
        //paymentVoucher.setIdentificadorDocumento(identificadorDocumento);

        if (StringUtils.isBlank(paymentVoucherModel.getDenominacionReceptor())) {
            paymentVoucherModel.setDenominacionReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucherModel.getNumeroDocumentoReceptor())) {
            paymentVoucherModel.setNumeroDocumentoReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucherModel.getTipoDocumentoReceptor())) {
            paymentVoucherModel.setTipoDocumentoReceptor("-");
        }
    }

    private boolean isNotNullAndZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isNullOrZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }
}
