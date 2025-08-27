package com.certicom.certifact_facturas_service_ng.validation.business;

import com.certicom.certifact_facturas_service_ng.dto.others.ComprobanteItem;
import com.certicom.certifact_facturas_service_ng.exceptions.ValidationException;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PaymentVoucherDetailValidator extends CamposEntrada<Object> {

    public void validate(ComprobanteItem item, String tipoComprobante, String ublVersion, String ruc) {
        validateUnidadMedida(item.getCodigoUnidadMedida(), tipoComprobante);
        validateCantidad(item.getCantidad(), tipoComprobante, item.getCodigoUnidadMedida());
        validateCodigoProducto(item.getCodigoProducto());
        validateCodigoProductoSunat(item.getCodigoProductoSunat());
        validateDescription(item.getDescripcion(), tipoComprobante, ublVersion);
        validateValorUnitario(item.getValorUnitario(), tipoComprobante);
        validateValorVenta(item.getValorVenta(), tipoComprobante);
        if(ublVersion.equals(ConstantesSunat.UBL_VERSION_2_1)) {
            validateDescuento(item.getDescuento(), item.getCodigoDescuento());
            validateOperacionGravada(item.getMontoBaseIgv(), item.getIgv(), item.getPorcentajeIgv(), item.getCodigoTipoAfectacionIGV());
            if (item.getCodigoTipoAfectacionIGV().equals("10") && !ruc.equals("30601762219")){
                validateGravadaVentaCantidad(item.getCantidad(),item.getValorUnitario(),item.getValorVenta(),item.getDescuento());
            }
        }
    }

    protected boolean validateOperacionOtrosTributos(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje) {
        boolean operacionExiste = false;
        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                throw new ValidationException("El campo [" + montoBaseOtrosTributosLabel + "] es obligatorio, al ingresar: " +
                        otrosTributosLabel + " o " + porcentajeOtrosTributosLabel);
            }
            if (tributo == null) {
                throw new ValidationException("El campo [" + otrosTributosLabel + "] es obligatorio, al ingresar: " +
                        montoBaseOtrosTributosLabel + " o " + porcentajeOtrosTributosLabel);
            }
            if (porcentaje == null) {
                throw new ValidationException("El campo [" + porcentajeOtrosTributosLabel + "] es obligatorio, al ingresar: " +
                        montoBaseOtrosTributosLabel + " o " + otrosTributosLabel);
            }
            operacionExiste = true;
        }
        return operacionExiste;
    }

    private void validateCodigoProductoSunat(String codigoProductoSunat) {
        if (StringUtils.isNotBlank(codigoProductoSunat)) {
            if (StringUtils.length(codigoProductoSunat) > 20) {
                throw new ValidationException("El campo [" + codigoProductoSunatLabel + "] debe tener un maximo de 20 caracteres.");
            }
        }
    }

    private void validateCodigoProducto(String codigoProducto) {
        if (StringUtils.isNotBlank(codigoProducto)) {
            if (StringUtils.length(codigoProducto) > 30) {
                throw new ValidationException("El campo [" + codigoProductoLabel + "] debe tener un maximo de 30 caracteres.");
            }
        }
    }

    protected boolean validateOperacionGravada(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje, String codigoAfectacion) {
        boolean existeOperacionGravada = false;
        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                throw new ValidationException("El campo [" + montoBaseIgvLabel + "] es obligatorio, al ingresar: " +
                        igvLabel + " o " + porcentajeIgvLabel);
            }
            if (tributo == null) {
                throw new ValidationException("El campo [" + igvLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIgvLabel + " o " + porcentajeIgvLabel);
            }
            if (porcentaje == null) {
                throw new ValidationException("El campo [" + porcentajeIgvLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIgvLabel + " o " + igvLabel);
            }
            existeOperacionGravada = true;
        }
        if (existeOperacionGravada) {
            validateAfectacionIGV(codigoAfectacion, tributo);
        }
        return existeOperacionGravada;
    }

    private void validateAfectacionIGV(String tipoIGV, BigDecimal igv) {
        if (igv != null) {
            if (StringUtils.isBlank(tipoIGV)) {
                throw new ValidationException("El campo [" + tipoAfectacionIGVLabel + "] es obligatorio.");
            }
            if (!StringUtils.isNumeric(tipoIGV)) {
                throw new ValidationException("El campo [" + tipoAfectacionIGVLabel + "] debe contener caracteres numericos.");
            }
            if (StringUtils.length(tipoIGV) != 2) {
                throw new ValidationException("El campo [" + tipoAfectacionIGVLabel + "] debe contener solo 2 caracteres numericos.");
            }
        }
    }

    private void validateDescuento(BigDecimal descuento, String tipoDescuento) {
        if (descuento != null && (descuento.compareTo(BigDecimal.ZERO) > 0)) {
            if (StringUtils.isBlank(tipoDescuento)) {
                throw new ValidationException("El campo [" + codigoDescuentoLabel + "] es obligatorio, al ingresar un descuento.");
            }
            if (!StringUtils.isNumeric(tipoDescuento)) {
                throw new ValidationException("El campo [" + codigoDescuentoLabel + "] debe contener caracteres numericos.");
            }
            if (StringUtils.length(tipoDescuento) != 2) {
                throw new ValidationException("El campo [" + codigoDescuentoLabel + "] debe contener solo 2 caracteres numericos.");
            }
        }
    }

    private void validateValorVenta(BigDecimal valorVenta, String tipoComprobante) {
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
            if (valorVenta == null) {
                throw new ValidationException("El campo [" + valorVentaLabel + "] es obligatorio.");
            }
        }
    }

    private void validateValorUnitario(BigDecimal valorUnitario, String tipoComprobante) {
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
            if (valorUnitario == null) {
                throw new ValidationException("El campo [" + valorUnitarioLabel + "] es obligatorio.");
            }
        }
    }

    private void validateDescription(String descripcion, String tipoComprobante, String ublVersion) {
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
            if (StringUtils.isBlank(descripcion)) {
                throw new ValidationException("El campo [" + descripcionLabel + "] es obligatorio.");
            }
        }
        if (StringUtils.isNotBlank(descripcion)) {
            if (ublVersion.equals(ConstantesSunat.UBL_VERSION_2_1)) {
                if (StringUtils.length(descripcion) > 500) {
                    throw new ValidationException("El campo [" + descripcionLabel + "] debe tener un maximo de 500 caracteres.");
                }
            } else {
                if (StringUtils.length(descripcion) > 250) {
                    throw new ValidationException("El campo [" + descripcionLabel + "] debe tener un maximo de 250 caracteres.");
                }
            }
        }
    }

    private void validateCantidad(BigDecimal cantidad, String tipoComprobante, String unidadMedida) {
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
            if (cantidad == null) {
                throw new ValidationException("El campo [" + cantidadLabel + "] es obligatorio");
            }
        }
        if (cantidad != null) {
            if (cantidad.equals(BigDecimal.ZERO)) {
                throw new ValidationException("El campo [" + cantidadLabel + "] es debe ser diferente de cero.");
            }
            if (StringUtils.isBlank(unidadMedida)) {
                throw new ValidationException("El campo [" + codigoUnidadMedidaLabel + "] es obligatorio, cuando "
                        + "ingresa un valor en el campo [" + cantidadLabel + "]");
            }
        }
    }

    private void validateUnidadMedida(String unidadMedida, String tipoComprobante) {
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
            if (StringUtils.isBlank(unidadMedida)) {
                throw new ValidationException("El campo [" + codigoUnidadMedidaLabel + "] es obligatorio.");
            }
        }
        if (StringUtils.isNotBlank(unidadMedida)) {
            if (!StringUtils.isAlphanumeric(unidadMedida)) {
                throw new ValidationException("El campo [" + codigoUnidadMedidaLabel + "] debe ser alfanumerico.");
            }
            if (StringUtils.length(unidadMedida) > 3) {
                throw new ValidationException("El campo [" + codigoUnidadMedidaLabel + "] debe tener un maximo de 3 caracteres.");
            }
        }
    }

    private void validateGravadaVentaCantidad(BigDecimal cantidad, BigDecimal valorUnitario, BigDecimal valorVenta,
                                              BigDecimal descu) {
        BigDecimal montoB2 = cantidad.multiply(valorUnitario);
        if (descu!=null){
            montoB2 = montoB2.subtract(descu);
        }
        String mensajeValidacion = null;
        if (!(montoB2.setScale(2, RoundingMode.HALF_UP).compareTo(valorVenta.setScale(2,RoundingMode.HALF_UP))==0)){
            throw new ValidationException(
                    "El valor unitario "+valorUnitario+" por la cantidad "+cantidad+" es diferente de su valor de venta -> "+
                            valorVenta +" y valxcant "+montoB2
            );
        }
    }

    protected boolean validateOperacionGratuita(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje, BigDecimal valorReferencial) {
        boolean existeItemGratuita = false;
        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                throw new ValidationException("El campo [" + montoBaseGratuitoLabel + "] es obligatorio, al ingresar: " +
                        impuestoVentaGratuitaLabel + " o " + porcentajeTributoVentaGratuitaLabel);
            }
            if (tributo == null) {
                throw new ValidationException("El campo [" + impuestoVentaGratuitaLabel + "] es obligatorio, al ingresar: " +
                        montoBaseGratuitoLabel + " o " + porcentajeTributoVentaGratuitaLabel);
            }
            if (porcentaje == null) {
                throw new ValidationException("El campo [" + porcentajeTributoVentaGratuitaLabel + "] es obligatorio, al ingresar: " +
                        montoBaseGratuitoLabel + " o " + impuestoVentaGratuitaLabel);
            }
            existeItemGratuita = true;
        }
        if (valorReferencial == null && existeItemGratuita) {
            throw new ValidationException("El campo [" + valorReferencialUnitarioLabel + "] es obligatorio, al ingresar una operación gratuita.");
        }
        return existeItemGratuita;
    }

    protected boolean validateOperacionISC(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje, String tipoISC) {
        String mensajeValidacion = null;
        boolean existeOperacionIsc = false;
        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                throw new ValidationException("El campo [" + montoBaseIscLabel + "] es obligatorio, al ingresar: " +
                        iscLabel + " o " + porcentajeIscLabel);
            }
            if (tributo == null) {
                throw new ValidationException("El campo [" + iscLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIscLabel + " o " + porcentajeIscLabel);
            }
            if (porcentaje == null) {
                throw new ValidationException("El campo [" + porcentajeIscLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIscLabel + " o " + iscLabel);
            }
            existeOperacionIsc = true;
        }
        if (existeOperacionIsc) {
            validateTipoISC(tipoISC);
        }

        return existeOperacionIsc;
    }

    private void validateTipoISC(String tipoISC) {
        if (StringUtils.isBlank(tipoISC)) {
            throw new ValidationException("El campo [" + tipoCalculoISCLabel + "] es obligatorio.");
        }
        if (!StringUtils.isNumeric(tipoISC)) {
            throw new ValidationException("El campo [" + tipoCalculoISCLabel + "] debe contener caracteres numericos.");
        }
        if (StringUtils.length(tipoISC) != 2) {
            throw new ValidationException("El campo [" + tipoCalculoISCLabel + "] debe contener solo 2 caracteres numericos.");
        }

    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return null;
    }

}
