package com.certicom.certifact_facturas_service_ng.formatter;

import com.certicom.certifact_facturas_service_ng.dto.others.ComprobanteItem;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PaymentVoucherDetailFormatter {

    public void format(ComprobanteItem item) {
        calculateValorReferencial(item);

        item.setCodigoUnidadMedida(StringUtils.trimToNull(item.getCodigoUnidadMedida()));
        item.setCodigoProducto(StringUtils.trimToNull(item.getCodigoProducto()));
        item.setCodigoProductoSunat(StringUtils.trimToNull(item.getCodigoProductoSunat()));
        item.setDescripcion(StringUtils.trimToNull(item.getDescripcion()));
        item.setCodigoTipoAfectacionIGV(StringUtils.trimToNull(item.getCodigoTipoAfectacionIGV()));
        item.setCodigoTipoCalculoISC(StringUtils.trimToNull(item.getCodigoTipoCalculoISC()));
    }

    private void calculateValorReferencial(ComprobanteItem item) {
        if ((item.getCodigoTipoAfectacionIGV().equals(ConstantesSunat.TIPO_AFCETACION_IGV_EXONERADO))&&item.getValorReferencialUnitario()==null){
            item.setValorReferencialUnitario(item.getValorUnitario());
            item.setMontoBaseExportacion(null);
            item.setImpuestoVentaGratuita(BigDecimal.ZERO);
            if (item.getCodigoTipoAfectacionIGV().equals(ConstantesSunat.TIPO_AFCETACION_IGV_EXONERADO)){
                item.setMontoBaseExonerado(item.getValorVenta());
            }
        }
    }

}
