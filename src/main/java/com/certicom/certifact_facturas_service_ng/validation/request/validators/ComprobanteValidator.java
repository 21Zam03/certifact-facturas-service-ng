package com.certicom.certifact_facturas_service_ng.validation.request.validators;

import com.certicom.certifact_facturas_service_ng.dto.others.Anticipo;
import com.certicom.certifact_facturas_service_ng.dto.others.ComprobanteItem;
import com.certicom.certifact_facturas_service_ng.dto.request.PaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.util.UtilFormat;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.certicom.certifact_facturas_service_ng.validation.request.anottations.ComprobanteValidation;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ComprobanteValidator extends CamposEntrada<Object>
        implements ConstraintValidator<ComprobanteValidation, PaymentVoucherRequest> {

    private final Boolean datosReceptorObligatorio = true;

    @Override
    public boolean isValid(PaymentVoucherRequest paymentVoucherRequest, ConstraintValidatorContext context) {
        /*BOOLEAN -> true o false, si es true quiere decir que la validacion es exitosa, si es false quiere decir que hubo error en la validacion*/
        /*STRING -> representa el mensaje de validacion indicando la razon por la cual no paso la validacion*/
        Pair<Boolean, String> resultado;
        resultado = validarTipoComprobante(paymentVoucherRequest.getTipoComprobante());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarSerie(paymentVoucherRequest.getSerie());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarNumero(paymentVoucherRequest.getNumero());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarFechaEmision(paymentVoucherRequest.getFechaEmision());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarHoraEmision(paymentVoucherRequest.getHoraEmision(), paymentVoucherRequest.getFechaEmision());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarTipoMoneda(paymentVoucherRequest.getCodigoMoneda());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarImportaTotal(paymentVoucherRequest.getImporteTotalVenta());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarTipoDocumentoReceptor(paymentVoucherRequest.getTipoDocumentoReceptor(), datosReceptorObligatorio);
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarNumeroDocumentoReceptor(
                paymentVoucherRequest.getNumeroDocumentoReceptor(), paymentVoucherRequest.getTipoDocumentoReceptor(),
                datosReceptorObligatorio);
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarDenominacionReceptor(paymentVoucherRequest.getDenominacionReceptor(), datosReceptorObligatorio);
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }

        resultado = validarDomicilioFiscalEmisor(paymentVoucherRequest.getCodigoLocalAnexoEmisor());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }

        resultado = validarTipoDocumentoRelacionado(paymentVoucherRequest.getCodigoTipoOtroDocumentoRelacionado());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }

        resultado = validarNumeroDocumentoRelacionado(
                paymentVoucherRequest.getSerieNumeroOtroDocumentoRelacionado(), paymentVoucherRequest.getCodigoTipoOtroDocumentoRelacionado());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }

        resultado = validarAnticipos(paymentVoucherRequest.getAnticipos());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
/*
        resultado = validarItems(comprobanteRequest.getItems(), comprobanteRequest.getTipoComprobante(), comprobanteRequest.getUblVersion(), comprobanteRequest.getRucEmisor());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }

        if(comprobanteRequest.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
            resultado = validarTotalIsc(comprobanteRequest.getTotalValorBaseIsc(), comprobanteRequest.getTotalIsc(), comprobanteRequest.getItems());
            if(!resultado.getLeft()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
                return resultado.getLeft();
            }
        }

        */
        return true;
    }

    private Pair<Boolean, String> validarTipoComprobante(String tipoComprobante) {
        if(tipoComprobante == null || tipoComprobante.isEmpty()) {
            return Pair.of(false, "El campo ["+tipoComprobanteLabel+"] es obligatorio");
        }
        return Pair.of(true, "");
    }

    private Pair<Boolean, String> validarSerie(String serie) {
        String mensaje = "";
        String primeraLetra = "";

        if(StringUtils.isBlank(serie)) {
            mensaje = "El campo ["+serieLabel+"] es obligatorio.";
            return Pair.of(false,  mensaje);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensaje = "El campo ["+serieLabel+"] recibe caracteres del alfabeto y números.";
            return Pair.of(false,  mensaje);
        }
        if (StringUtils.length(serie) != 4) {
            mensaje = "El campo ["+serieLabel+"] debe ser alfanumerico de 4 caracteres.";
            return Pair.of(false,  mensaje);
        }
        primeraLetra = StringUtils.substring(serie, 0, 1).toUpperCase();
        if(!primeraLetra.equals("F") && !StringUtils.isNumeric(serie)) {
            mensaje = "El campo ["+serieLabel+"] puede ser numerico o empezar con el caracter F.";
            return Pair.of(false,  mensaje);
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarNumero(Integer numero) {
        String mensaje = "";
        if(numero == null) {
            mensaje = "El campo ["+numeroLabel+"] es obligatorio.";
            return Pair.of(false,  mensaje);
        }
        if (numero < 1) {
            mensaje = "El campo ["+numeroLabel+"] debe ser mayor que cero.";
            return Pair.of(false,  mensaje);
        }
        if(StringUtils.length(numero.toString()) > 8) {
            mensaje = "El campo ["+numeroLabel+"] debe tener como maximo 8 digitos.";
            return Pair.of(false,  mensaje);
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarFechaEmision(String fechaEmision) {
        String mensaje = "";
        if(fechaEmision == null) {
            mensaje = "El campo ["+fechaEmisionLabel+"] es obligatorio.";
            return Pair.of(false,  mensaje);
        }
        if (UtilFormat.fechaDate(fechaEmision) == null) {
            mensaje = "El campo ["+fechaEmisionLabel+"] debe tener el formato yyyy-MM-dd.";
            return Pair.of(false,  mensaje);
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaEmisionDate = formatter.parse(fechaEmision);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, -4);
            if (calendar.getTime().compareTo(fechaEmisionDate) > 0) {
                mensaje = "El campo ["+fechaEmisionLabel+"] no debe tener más de 3 días de anterioridad.";
                return Pair.of(false,  mensaje);
            }
        } catch (ParseException e) {
            throw new ValidationException(e.getMessage());
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarHoraEmision(String horaEmision, String fechaEmision) {
        String mensaje = "";
        if(StringUtils.isBlank(horaEmision)) {
            mensaje = "El campo ["+horaEmisionLabel+"] es obligatorio.";
            return Pair.of(false,  mensaje);
        }
        if(StringUtils.isBlank(UtilFormat.hora(fechaEmision + " " + horaEmision))) {
            mensaje = "El campo [" + horaEmisionLabel + "] debe tener el formato hh:mm:ss";
            return Pair.of(false,  mensaje);
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarTipoMoneda(String codigoMoneda) {
        String mensaje = "";
        if(codigoMoneda == null) {
            mensaje = "El campo ["+codigoMonedaLabel+"] es obligatorio.";
            return Pair.of(false,  mensaje);
        }
        if (!StringUtils.isAlphanumeric(codigoMoneda)) {
            mensaje = "El campo ["+codigoMonedaLabel+"] es alfanumerico.";
            return Pair.of(false,  mensaje);
        }
        if (StringUtils.length(codigoMoneda) != 3) {
            mensaje = "El campo ["+codigoMonedaLabel+"] debe tener 3 caracteres.";
            return Pair.of(false,  mensaje);
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarImportaTotal(BigDecimal importeTotal) {
        String mensaje = "";
        if(importeTotal == null) {
            mensaje = "El campo ["+importeTotalLabel+"] es obligatorio.";
            return Pair.of(false,  mensaje);
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarTipoDocumentoReceptor(String tipoDocumentoReceptor, Boolean datosReceptorObligatorio) {
        String mensaje = "";
        if(StringUtils.isBlank(tipoDocumentoReceptor) && datosReceptorObligatorio) {
            mensaje = "El campo [" + tipoDocumentoReceptorLabel + "] es obligatorio.";
            return Pair.of(false,  mensaje);
        }
        if(StringUtils.isNotBlank(tipoDocumentoReceptor) && !StringUtils.isAlphanumeric(tipoDocumentoReceptor)) {
            mensaje = "El campo [" + tipoDocumentoReceptorLabel + "] debe ser alfanumerico.";
            return Pair.of(false,  mensaje);
        }
        if (StringUtils.isNotBlank(tipoDocumentoReceptor) && StringUtils.length(tipoDocumentoReceptor) > 1) {
            mensaje = "El campo [" + tipoDocumentoReceptorLabel + "] debe tener un solo caracter.";
            return Pair.of(false,  mensaje);
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarNumeroDocumentoReceptor(
            String numeroDocumentoReceptor, String tipoDocumentoReceptor, Boolean datosReceptorObligatorio
    ) {
        String mensaje = "";
        if(datosReceptorObligatorio) {
            if(StringUtils.isBlank(numeroDocumentoReceptor)) {
                mensaje = "El campo [" + numeroDocumentoReceptorLabel + "] es obligatorio.";
                return Pair.of(false,  mensaje);
            }
        } else {
            if (StringUtils.isNotBlank(numeroDocumentoReceptor) || StringUtils.isNotBlank(tipoDocumentoReceptor)) {
                if (StringUtils.isBlank(numeroDocumentoReceptor)) {
                    mensaje = "El campo [" + numeroDocumentoReceptorLabel + "] es obligatorio, si ingresa el campo [" + tipoDocumentoReceptorLabel + "]";
                    return Pair.of(false,  mensaje);
                }
                if (StringUtils.isBlank(tipoDocumentoReceptor)) {
                    mensaje = "El campo [" + tipoDocumentoReceptorLabel + "] es obligatorio, si ingresa el campo [" + numeroDocumentoReceptorLabel + "]";
                    return Pair.of(false,  mensaje);
                }
            }
        }
        return Pair.of(true,  mensaje);
    }

    private Pair<Boolean, String> validarDenominacionReceptor(String denominacionReceptor, Boolean datosReceptorObligatorio) {
        String mensaje = "";
        if(datosReceptorObligatorio) {
            if (StringUtils.isBlank(denominacionReceptor)) {
                mensaje = "El campo [" + denominacionReceptorLabel + "] es obligatorio.";
                return Pair.of(false,  mensaje);
            }
        }
        if (StringUtils.isNotBlank(denominacionReceptor) && StringUtils.length(denominacionReceptor) > 1500) {
            mensaje = "El campo [" + denominacionReceptorLabel + "] debe tener un maximo de 1500 caracteres.";
            return Pair.of(false,  mensaje);
        }
        return Pair.of(true,  mensaje);
    }

    private Pair<Boolean, String> validarDomicilioFiscalEmisor(String codigoDomicilioFiscal) {
        String mensaje = "";
        if(StringUtils.isNotBlank(codigoDomicilioFiscal)) {
            if (!StringUtils.isAlphanumeric(codigoDomicilioFiscal)) {
                mensaje = "El campo [" + codigoLocalAnexoEmisorLabel + "] debe ser alfanumerico.";
                return Pair.of(false,  mensaje);
            }
            if (StringUtils.length(codigoDomicilioFiscal) != 4) {
                mensaje = "El campo [" + codigoLocalAnexoEmisorLabel + "] debe tener 4 caracteres.";
                return Pair.of(false,  mensaje);
            }
        }
        return Pair.of(true,  mensaje);
    }

    private Pair<Boolean, String> validarTipoDocumentoRelacionado(String tipoDocumentoRelacionado) {
        String mensaje = "";
        if(StringUtils.isNotBlank(tipoDocumentoRelacionado)) {
            if (!StringUtils.isAlphanumeric(tipoDocumentoRelacionado)) {
                mensaje = "El campo [" + tipoDocumentoRelacionadoLabel + "] debe ser alfanumerico.";
                return Pair.of(false,  mensaje);
            }
            if (StringUtils.length(StringUtils.trim(tipoDocumentoRelacionado)) != 2) {
                mensaje = "El campo [" + tipoDocumentoRelacionadoLabel + "] debe tener 2 caracteres.";
                return Pair.of(false,  mensaje);
            }
        }
        return Pair.of(true,  mensaje);
    }

    private Pair<Boolean, String> validarNumeroDocumentoRelacionado(
            String numeroDocumentoRelacionado, String tipoDocumentoRelacionado
    ) {
        String mensaje = "";
        if(StringUtils.isNotBlank(numeroDocumentoRelacionado)) {
            if (StringUtils.isBlank(numeroDocumentoRelacionado)) {
                mensaje = "El campo [" + tipoDocumentoRelacionadoLabel + "] es obligatorio, en el caso de ingresar "
                        + "el campo [" + numeroDocumentoRelacionadoLabel + "]";
                return Pair.of(false,  mensaje);
            }
            if (StringUtils.length(numeroDocumentoRelacionado) > 30) {
                mensaje = "El campo [" + numeroDocumentoRelacionadoLabel + "] debe a lo mas 30 caracteres.";
                return Pair.of(false,  mensaje);
            }
        } else {
            if (StringUtils.isNotBlank(tipoDocumentoRelacionado)) {
                mensaje = "Se ha ingresado el campo [" + tipoDocumentoRelacionadoLabel + "] por lo cual tambien "
                        + "debe ingresar el campo [" + numeroDocumentoRelacionadoLabel + "]";
                return Pair.of(false,  mensaje);
            }
        }
        return Pair.of(true,  mensaje);
    }

    private Pair<Boolean, String> validarAnticipos(List<Anticipo> anticipos) {
        String mensaje = "";
        Pair<Boolean, String> resultado;
        if (anticipos!=null && !anticipos.isEmpty()) {
            for (Anticipo anticipo : anticipos) {
                resultado = validarAnticipoSerie(anticipo.getSerieAnticipo());
                if(!resultado.getLeft()) {
                    return resultado;
                }
                resultado = validarAnticpoNumero(anticipo.getNumeroAnticipo());
                if(!resultado.getLeft()) {
                    return resultado;
                }
                resultado = validarMontoAnticipado(anticipo.getMontoAnticipado());
                if(!resultado.getLeft()) {
                    return resultado;
                }
                resultado = validarTipoDocumentoAnticipo(anticipo.getTipoDocumentoAnticipo());
                if(!resultado.getLeft()) {
                    return resultado;
                }
            }
        }
        return Pair.of(true,  mensaje);
    }

    private Pair<Boolean, String> validarAnticipoSerie(String serie) {
        String mensaje = "";
        if (StringUtils.isBlank(serie)) {
            mensaje = "El campo [" + serieAnticipoLabel + "] es obligatorio.";
            return Pair.of(false, mensaje);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensaje = "El campo [" + serieAnticipoLabel + "] recibe caracteres alfabeticos y numericos.";
            return Pair.of(false, mensaje);
        }
        if (StringUtils.length(serie) != 4) {
            mensaje = "El campo [" + serieAnticipoLabel + "] debe ser alfanumerico de 4 caracteres.";
            return Pair.of(false, mensaje);
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarAnticpoNumero(Integer numero) {
        String mensaje = "";
        if (numero == null) {
            mensaje = "El campo [" + numeroAnticipoLabel + "] es obligatorio.";
            return Pair.of(false, mensaje);
        }
        if (numero < 1) {
            mensaje = "El campo [" + numeroAnticipoLabel + "] debe ser mayor que cero.";
            return Pair.of(false, mensaje);
        }
        if (StringUtils.length(numero.toString()) > 8) {
            mensaje = "El campo [" + numeroAnticipoLabel + "] debe tener como maximo 8 digitos.";
            return Pair.of(false, mensaje);
        }
        return Pair.of(true,  mensaje);
    }

    private Pair<Boolean, String> validarMontoAnticipado(BigDecimal montoAnticipado) {
        String mensaje = "";
        if (montoAnticipado == null) {
            mensaje = "El campo [" + montoAnticipadoLabel + "] es obligatorio.";
            return Pair.of(false, mensaje);
        }
        return Pair.of(true,  mensaje);
    }

    private Pair<Boolean, String> validarTipoDocumentoAnticipo(String tipoDocumentoAnticipo) {
        String mensaje = "";
        if (StringUtils.isBlank(tipoDocumentoAnticipo)) {
            mensaje = "El campo [" + tipoDocumentoAnticipoLabel + "] es obligatorio.";
            return Pair.of(false, mensaje);
        }
        return Pair.of(true, mensaje);
    }

    /*
    private Pair<Boolean, String> validarTotalOpe(ComprobanteRequest comprobanteRequest) {
        String mensaje = "";
        if ((comprobanteRequest.getTotalValorVentaGravada()==null||comprobanteRequest.getTotalValorVentaGravada().compareTo(BigDecimal.ZERO)==0)
                && comprobanteRequest.getTotalValorVentaExportacion()==null &&
                comprobanteRequest.getTotalValorVentaExonerada()==null&&comprobanteRequest.getTotalImpOperGratuita()==null&&
                comprobanteRequest.getTotalValorVentaInafecta()==null){
            for (ComprobanteItem line: comprobanteRequest.getItems() ) {
                switch (line.getCodigoTipoAfectacionIGV()){
                    case "20":
                        if(comprobanteRequest.getTotalValorVentaExonerada()==null) {
                            comprobanteRequest.setTotalValorVentaExonerada(BigDecimal.ZERO);
                        }
                        comprobanteRequest.getTotalValorVentaExonerada().add(line.getValorVenta());
                        comprobanteRequest.setTotalValorVentaExonerada(comprobanteRequest.getTotalValorVentaExonerada().add(line.getValorVenta()));
                        break;
                }
            }
        }
        return Pair.of(true, mensaje);
    }*/

    private Pair<Boolean, String> validarItems(List<ComprobanteItem> items, String tipoComprobante,
                                               String ublVersion, String ruc) {
        String mensaje = "";
        Pair<Boolean, String> resultado;
        if (items!=null && !items.isEmpty()) {
            for (ComprobanteItem item : items) {
                resultado = validarItemUnidadMedida(item.getCodigoUnidadMedida(), tipoComprobante);
                if(!resultado.getLeft()) {
                    return resultado;
                }
                resultado = validarItemCantidad(item.getCantidad(), tipoComprobante, item.getCodigoUnidadMedida());
                if(!resultado.getLeft()) {
                    return resultado;
                }
                resultado = validarItemCodigoProducto(item.getCodigoProducto());
                if(!resultado.getLeft()) {
                    return resultado;
                }
                resultado = validarItemCodigoProductoSunat(item.getCodigoProductoSunat());
                if(!resultado.getLeft()) {
                    return resultado;
                }
                resultado = validarItemDescripcion(item.getDescripcion(), tipoComprobante, ublVersion);
                if(!resultado.getLeft()) {
                    return resultado;
                }
                resultado = validarItemValorUnitario(item.getValorUnitario(), tipoComprobante);
                if(!resultado.getLeft()) {
                    return resultado;
                }
                resultado = validarItemValorVenta(item.getValorVenta(), tipoComprobante);
                if(!resultado.getLeft()) {
                    return resultado;
                }

                if(ublVersion.equals(ConstantesSunat.UBL_VERSION_2_1)) {
                    resultado = validarItemDescuento(item.getDescuento(), item.getCodigoDescuento());
                    if(!resultado.getLeft()) {
                        return resultado;
                    }
                    resultado = validarItemOperacionGravada(item.getMontoBaseIgv(), item.getIgv(), item.getPorcentajeIgv(), item.getCodigoTipoAfectacionIGV());
                } else {
                    resultado = validarItemAfectacionIgv(item.getCodigoTipoAfectacionIGV(), item.getIgv());
                }

                if(!resultado.getLeft()) {
                    return resultado;
                }
            }
        }
        return Pair.of(true,  mensaje);
    }

    private Pair<Boolean, String> validarItemUnidadMedida(String unidadMedida, String tipoComprobante) {
        String mensaje = "";
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
            if (StringUtils.isBlank(unidadMedida)) {
                mensaje = "El campo [" + codigoUnidadMedidaLabel + "] es obligatorio.";
                return Pair.of(false, mensaje);
            }
        }
        if (StringUtils.isNotBlank(unidadMedida)) {
            if (!StringUtils.isAlphanumeric(unidadMedida)) {
                mensaje = "El campo [" + codigoUnidadMedidaLabel + "] debe ser alfanumerico.";
                return Pair.of(false, mensaje);
            }
            if (StringUtils.length(unidadMedida) > 3) {
                mensaje = "El campo [" + codigoUnidadMedidaLabel + "] debe tener un maximo de 3 caracteres.";
                return Pair.of(false, mensaje);
            }
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarItemCantidad(BigDecimal cantidad, String tipoComprobante, String unidadMedida) {
        String mensaje = "";
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
            if (cantidad == null) {
                mensaje = "El campo [" + cantidadLabel + "] es obligatorio";
                return Pair.of(false, mensaje);
            }
        }
        if (cantidad != null) {
            if (cantidad.equals(BigDecimal.ZERO)) {
                mensaje = "El campo [" + mensaje + "] es debe ser diferente de cero.";
                return Pair.of(false, mensaje);
            }
            if (StringUtils.isBlank(unidadMedida)) {
                mensaje = "El campo [" + codigoUnidadMedidaLabel + "] es obligatorio, cuando "
                        + "ingresa un valor en el campo [" + mensaje + "]";
                return Pair.of(false, mensaje);
            }
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarItemCodigoProducto(String codigoProducto) {
        String mensaje = "";
        if (StringUtils.isNotBlank(codigoProducto)) {
            if (StringUtils.length(codigoProducto) > 30) {
                mensaje = "El campo [" + codigoProductoLabel + "] debe tener un maximo de 30 caracteres.";
                return Pair.of(false, mensaje);
            }
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarItemCodigoProductoSunat(String codigoProductoSunat) {
        String mensaje = "";
        if (StringUtils.isNotBlank(codigoProductoSunat)) {
            if (StringUtils.length(codigoProductoSunat) > 20) {
                mensaje = "El campo [" + codigoProductoLabel + "] debe tener un maximo de 30 caracteres.";
                return Pair.of(false, mensaje);
            }
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarItemDescripcion(String descripcion, String tipoComprobante, String ublVersion) {
        String mensaje = "";

        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
            if (StringUtils.isBlank(descripcion)) {
                mensaje = "El campo [" + descripcionLabel + "] es obligatorio.";
                return Pair.of(false, mensaje);
            }
        }
        if (StringUtils.isNotBlank(descripcion)) {
            if (ublVersion.equals(ConstantesSunat.UBL_VERSION_2_1)) {
                if (StringUtils.length(descripcion) > 500) {
                    mensaje = "El campo [" + descripcionLabel + "] debe tener un maximo de 500 caracteres.";
                    return Pair.of(false, mensaje);
                }
            } else {
                if (StringUtils.length(descripcion) > 250) {
                    mensaje = "El campo [" + descripcionLabel + "] debe tener un maximo de 250 caracteres.";
                    return Pair.of(false, mensaje);
                }
            }
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarItemValorUnitario(BigDecimal valorUnitario, String tipoComprobante) {
        String mensaje = "";
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
            if (valorUnitario == null) {
                mensaje = "El campo [" + valorUnitarioLabel + "] es obligatorio.";
                return Pair.of(false, mensaje);
            }
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarItemValorVenta(BigDecimal valorVenta, String tipoComprobante) {
        String mensaje = "";
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
            if (valorVenta == null) {
                mensaje = "El campo [" + valorVentaLabel + "] es obligatorio.";
                return Pair.of(false, mensaje);
            }
        }
        return Pair.of(true, mensaje);
    }

    /*
    private Pair<Boolean, String> validarValorReferencial(ComprobanteItem item) {
        String mensaje = "";
        if ((item.getCodigoTipoAfectacionIGV().equals(ConstantesSunat.TIPO_AFCETACION_IGV_EXONERADO))&&item.getValorReferencialUnitario()==null){
            item.setValorReferencialUnitario(item.getValorUnitario());
            item.setMontoBaseExportacion(null);
            item.setImpuestoVentaGratuita(BigDecimal.ZERO);
            if (item.getCodigoTipoAfectacionIGV().equals(ConstantesSunat.TIPO_AFCETACION_IGV_EXONERADO)){
                item.setMontoBaseExonerado(item.getValorVenta());
            }
        }
        return Pair.of(true, mensaje);
    }
    */

    private Pair<Boolean, String> validarItemDescuento(BigDecimal descuento, String tipoDescuento) {
        String mensaje = "";
        if (descuento != null && (descuento.compareTo(BigDecimal.ZERO) > 0)) {
            if (StringUtils.isBlank(tipoDescuento)) {
                mensaje = "El campo [" + codigoDescuentoLabel + "] es obligatorio, al ingresar un descuento.";
                return Pair.of(false, mensaje);
            }
            if (!StringUtils.isNumeric(tipoDescuento)) {
                mensaje = "El campo [" + codigoDescuentoLabel + "] debe contener caracteres numericos.";
                return Pair.of(false, mensaje);
            }
            if (StringUtils.length(tipoDescuento) != 2) {
                mensaje = "El campo [" + codigoDescuentoLabel + "] debe contener solo 2 caracteres numericos.";
                return Pair.of(false, mensaje);
            }
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarItemOperacionGravada(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje, String codigoAfectacion) {
        String mensaje = "";
        boolean existeOperacionGravada = false;

        Pair<Boolean, String> resultado = Pair.of(true, mensaje);

        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                mensaje = "El campo [" + montoBaseIgvLabel + "] es obligatorio, al ingresar: " +
                        igvLabel + " o " + porcentajeIgvLabel;
                return Pair.of(false, mensaje);
            }
            if (tributo == null) {
                mensaje = "El campo [" + igvLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIgvLabel + " o " + porcentajeIgvLabel;
                return Pair.of(false, mensaje);
            }
            if (porcentaje == null) {
                mensaje = "El campo [" + porcentajeIgvLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIgvLabel + " o " + igvLabel;
                return Pair.of(false, mensaje);
            }
            existeOperacionGravada = true;
        }
        if (existeOperacionGravada) {
            resultado = validarItemAfectacionIgv(codigoAfectacion, tributo);
        }
        return resultado;
    }

    private Pair<Boolean, String> validarItemAfectacionIgv(String tipoIGV, BigDecimal igv) {
        String mensaje = "";
        if (igv != null) {
            if (StringUtils.isBlank(tipoIGV)) {
                mensaje = "El campo [" + tipoAfectacionIGVLabel + "] es obligatorio.";
                return Pair.of(false, mensaje);
            }
            if (!StringUtils.isNumeric(tipoIGV)) {
                mensaje = "El campo [" + tipoAfectacionIGVLabel + "] debe contener caracteres numericos.";
                return Pair.of(false, mensaje);
            }
            if (StringUtils.length(tipoIGV) != 2) {
                mensaje = "El campo [" + tipoAfectacionIGVLabel + "] debe contener solo 2 caracteres numericos.";
                return Pair.of(false, mensaje);
            }
        }
        return Pair.of(true, mensaje);
    }

    private Pair<Boolean, String> validarTotalIsc(BigDecimal montoBaseIsc, BigDecimal montoIsc, List<ComprobanteItem> items) {
        String mensaje = "";
        boolean existeAlmenosUno = false;
        if (montoBaseIsc != null || montoIsc != null) {
            if (montoBaseIsc == null) {
                mensaje = "El campo [" + totalValorBaseIscLabel + "] es obligatorio, si ingresa valor en [" + totalIscLabel + "]";
                return Pair.of(false, mensaje);
            }
            if (montoIsc == null) {
                mensaje = "El campo [" + totalIscLabel + "] es obligatorio, si ingresa valor en [" + totalValorBaseIscLabel + "]";
                return Pair.of(false, mensaje);
            }
            for (ComprobanteItem line : items) {
                Triple<Boolean, Boolean, String> result = validarItemOperacionISC(line.getMontoBaseIsc(), line.getIsc(), line.getPorcentajeIsc(), line.getCodigoTipoCalculoISC());
                if(!result.getMiddle()) {
                    return Pair.of(result.getMiddle(), result.getRight());
                }
                existeAlmenosUno = result.getLeft();
                if (existeAlmenosUno) {
                    break;
                }
            }
            if (!existeAlmenosUno) {
                mensaje = "Debe ingresar al menos un item con datos del tributo ISC.";
                return Pair.of(false, mensaje);
            }
        }
        return Pair.of(true, mensaje);
    }

    private Triple<Boolean, Boolean, String> validarItemOperacionISC(BigDecimal montoBase, BigDecimal tributo, BigDecimal porcentaje, String tipoISC) {
        boolean existeOperacionIsc = false;
        MutableTriple<Boolean, Boolean, String> resultado = new MutableTriple<>(existeOperacionIsc, true, "");
        if (montoBase != null || tributo != null || porcentaje != null) {
            if (montoBase == null) {
                resultado.setLeft(existeOperacionIsc);
                resultado.setMiddle(false);
                resultado.setRight("El campo [" + montoBaseIscLabel + "] es obligatorio, al ingresar: " +
                        iscLabel + " o " + porcentajeIscLabel);
                return resultado;
            }
            if (tributo == null) {
                resultado.setLeft(existeOperacionIsc);
                resultado.setMiddle(false);
                resultado.setRight("El campo [" + iscLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIscLabel + " o " + porcentajeIscLabel);
                return resultado;
            }
            if (porcentaje == null) {
                resultado.setLeft(existeOperacionIsc);
                resultado.setMiddle(false);
                resultado.setRight("El campo [" + porcentajeIscLabel + "] es obligatorio, al ingresar: " +
                        montoBaseIscLabel + " o " + iscLabel);
                return resultado;
            }
            existeOperacionIsc = true;
            resultado.setLeft(existeOperacionIsc);
        }
        if (existeOperacionIsc) {
            Pair<Boolean, String> result = validarItemTipoISC(tipoISC);
            if(!result.getLeft()) {
                resultado.setLeft(existeOperacionIsc);
                resultado.setMiddle(false);
                resultado.setRight(result.getRight());
                return resultado;
            }
        }
        return resultado;
    }

    private Pair<Boolean, String> validarItemTipoISC(String tipoISC) {
        MutablePair<Boolean, String> resultado = MutablePair.of(true, "");
        if (StringUtils.isBlank(tipoISC)) {
            resultado.setLeft(false);
            resultado.setRight("El campo [" + tipoCalculoISCLabel + "] es obligatorio.");
            return resultado;
        }
        if (!StringUtils.isNumeric(tipoISC)) {
            resultado.setLeft(false);
            resultado.setRight("El campo [" + tipoCalculoISCLabel + "] debe contener caracteres numericos.");
            return resultado;
        }
        if (StringUtils.length(tipoISC) != 2) {
            resultado.setLeft(false);
            resultado.setRight("El campo [" + tipoCalculoISCLabel + "] debe contener solo 2 caracteres numericos.");
            return resultado;
        }
        return resultado;
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return null;
    }

}
