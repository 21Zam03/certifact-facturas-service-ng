package com.certicom.certifact_facturas_service_ng.validation.validators;

import com.certicom.certifact_facturas_service_ng.dto.others.Anticipo;
import com.certicom.certifact_facturas_service_ng.dto.request.ComprobanteRequest;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.util.UtilFormat;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.certicom.certifact_facturas_service_ng.validation.anottations.ComprobanteValidation;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
        implements ConstraintValidator<ComprobanteValidation, ComprobanteRequest> {

    private final Boolean datosReceptorObligatorio = true;

    @Override
    public boolean isValid(ComprobanteRequest comprobanteRequest, ConstraintValidatorContext context) {
        Pair<Boolean, String> resultado;
        resultado = validarTipoComprobante(comprobanteRequest.getTipoComprobante());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarSerie(comprobanteRequest.getSerie());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarNumero(comprobanteRequest.getNumero());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarFechaEmision(comprobanteRequest.getFechaEmision());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarHoraEmision(comprobanteRequest.getHoraEmision(), comprobanteRequest.getFechaEmision());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarTipoMoneda(comprobanteRequest.getCodigoMoneda());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarImportaTotal(comprobanteRequest.getImporteTotalVenta());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarTipoDocumentoReceptor(comprobanteRequest.getTipoDocumentoReceptor(), datosReceptorObligatorio);
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarNumeroDocumentoReceptor(
                comprobanteRequest.getNumeroDocumentoReceptor(), comprobanteRequest.getTipoDocumentoReceptor(),
                datosReceptorObligatorio);
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarDenominacionReceptor(comprobanteRequest.getDenominacionReceptor(), datosReceptorObligatorio);
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }

        resultado = validarDomicilioFiscalEmisor(comprobanteRequest.getCodigoLocalAnexoEmisor());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }

        resultado = validarTipoDocumentoRelacionado(comprobanteRequest.getCodigoTipoOtroDocumentoRelacionado());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }

        resultado = validarNumeroDocumentoRelacionado(
                comprobanteRequest.getSerieNumeroOtroDocumentoRelacionado(), comprobanteRequest.getCodigoTipoOtroDocumentoRelacionado());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        resultado = validarAnticipos(comprobanteRequest.getAnticipos());
        if(!resultado.getLeft()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(resultado.getRight()).addConstraintViolation();
            return resultado.getLeft();
        }
        return true;
    }

    private Pair<Boolean, String> validarTipoComprobante(String tipoComprobante) {
        String mensaje = "";
        if(tipoComprobante == null || tipoComprobante.isEmpty()) {
            mensaje = "El campo ["+tipoComprobanteLabel+"] es obligatorio";
            return Pair.of(false, mensaje);
        }
        if(!tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA) &&
                !tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) &&
        !tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
            mensaje = "El campo ["+tipoComprobanteLabel+"] contiene un valor no valido o no es de tipo factura, nota de credito, nota de debito - ["+tipoComprobante+"]";
            return Pair.of(false, mensaje);
        }
        return Pair.of(true, mensaje);
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

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return null;
    }

}
