package com.certicom.certifact_facturas_service_ng.formatter;

import com.certicom.certifact_facturas_service_ng.dto.PaymentVoucherDto;
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

    public void formatPaymentVoucher(PaymentVoucherDto paymentVoucherDto) {
        calculateTotalValorVenta(paymentVoucherDto);
        formatTotalValorVenta(paymentVoucherDto);
        formatItems(paymentVoucherDto);
        formatAnticipos(paymentVoucherDto.getAnticipos());
        formatData(paymentVoucherDto);
    }

    private void calculateTotalValorVenta(PaymentVoucherDto paymentVoucherDto) {
        if (isNullOrZero(paymentVoucherDto.getTotalValorVentaGravada())
                && isNullOrZero(paymentVoucherDto.getTotalValorVentaExportacion())
                && isNullOrZero(paymentVoucherDto.getTotalValorVentaExonerada())
                && isNullOrZero(paymentVoucherDto.getTotalImpOperGratuita())
                && isNullOrZero(paymentVoucherDto.getTotalValorVentaInafecta())) {
            for (ComprobanteItem line: paymentVoucherDto.getItems() ) {
                switch (line.getCodigoTipoAfectacionIGV()){
                    case "20":
                        if(paymentVoucherDto.getTotalValorVentaExonerada()==null) {
                            paymentVoucherDto.setTotalValorVentaExonerada(BigDecimal.ZERO);
                        }
                        paymentVoucherDto.setTotalValorVentaExonerada(paymentVoucherDto.getTotalValorVentaExonerada().add(line.getValorVenta()));
                        break;
                }
            }
        }
    }

    private void formatTotalValorVenta(PaymentVoucherDto paymentVoucherDto) {
        if (isNotNullAndZero(paymentVoucherDto.getTotalValorVentaGravada())) {
            paymentVoucherDto.setTotalValorVentaGravada(null);
        }
        if (isNotNullAndZero(paymentVoucherDto.getTotalValorVentaGratuita())) {
            paymentVoucherDto.setTotalValorVentaGratuita(null);
        }
        if (isNotNullAndZero(paymentVoucherDto.getTotalValorVentaExonerada())) {
            paymentVoucherDto.setTotalValorVentaExonerada(null);
        }
        if (isNotNullAndZero(paymentVoucherDto.getTotalValorVentaExportacion() )) {
            paymentVoucherDto.setTotalValorVentaExportacion(null);
        }
        if (isNotNullAndZero(paymentVoucherDto.getTotalValorVentaInafecta())) {
            paymentVoucherDto.setTotalValorVentaInafecta(null);
        }
        if (isNotNullAndZero(paymentVoucherDto.getTotalIgv())) {
            paymentVoucherDto.setTotalIgv(null);
        }
        if (paymentVoucherDto.getMontoDetraccion() != null) {
            paymentVoucherDto.setMontoDetraccion(paymentVoucherDto.getMontoDetraccion().setScale(2, RoundingMode.CEILING));
        }
        if (paymentVoucherDto.getTipoTransaccion() == null) {
            paymentVoucherDto.setTipoTransaccion(BigDecimal.ONE);
        }
    }

    private void formatItems(PaymentVoucherDto paymentVoucherDto) {
        for (ComprobanteItem line: paymentVoucherDto.getItems() ) {
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

    private void formatData(PaymentVoucherDto paymentVoucherDto) {
        paymentVoucherDto.setRucEmisor(StringUtils.trimToNull(paymentVoucherDto.getRucEmisor()));
        paymentVoucherDto.setSerie(paymentVoucherDto.getSerie().toUpperCase());
        paymentVoucherDto.setHoraEmision(StringUtils.trimToNull(paymentVoucherDto.getHoraEmision()));
        paymentVoucherDto.setCodigoMoneda(StringUtils.trimToNull(paymentVoucherDto.getCodigoMoneda()));
        paymentVoucherDto.setCodigoLocalAnexoEmisor(StringUtils.trimToNull(paymentVoucherDto.getCodigoLocalAnexoEmisor()));
        paymentVoucherDto.setDenominacionReceptor(StringUtils.trimToNull(paymentVoucherDto.getDenominacionReceptor()));

        paymentVoucherDto.setCodigoTipoOtroDocumentoRelacionado(StringUtils.trimToNull(
                paymentVoucherDto.getCodigoTipoOtroDocumentoRelacionado()));
        paymentVoucherDto.setSerieNumeroOtroDocumentoRelacionado(StringUtils.trimToNull(
                paymentVoucherDto.getSerieNumeroOtroDocumentoRelacionado()));
        paymentVoucherDto.setCodigoTipoOperacion(StringUtils.trimToNull(paymentVoucherDto.getCodigoTipoOperacion()));
        paymentVoucherDto.setMotivoNota(StringUtils.trimToNull(paymentVoucherDto.getMotivoNota()));
        //paymentVoucher.setIdentificadorDocumento(identificadorDocumento);

        if (StringUtils.isBlank(paymentVoucherDto.getDenominacionReceptor())) {
            paymentVoucherDto.setDenominacionReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucherDto.getNumeroDocumentoReceptor())) {
            paymentVoucherDto.setNumeroDocumentoReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucherDto.getTipoDocumentoReceptor())) {
            paymentVoucherDto.setTipoDocumentoReceptor("-");
        }
    }

    private boolean isNotNullAndZero(BigDecimal value) {
        return value != null && value.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isNullOrZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }
}
