package com.certicom.certifact_facturas_service_ng.validation.business;

import com.certicom.certifact_facturas_service_ng.dto.request.VoucherAnnularRequest;
import com.certicom.certifact_facturas_service_ng.enums.EstadoComprobanteEnum;
import com.certicom.certifact_facturas_service_ng.exceptions.ValidationException;
import com.certicom.certifact_facturas_service_ng.feign.CompanyFeign;
import com.certicom.certifact_facturas_service_ng.feign.ParameterFeign;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherFeign;
import com.certicom.certifact_facturas_service_ng.model.ParameterModel;
import com.certicom.certifact_facturas_service_ng.model.PaymentVoucherModel;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.certicom.certifact_facturas_service_ng.validation.ConstantesSunat;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VoucherAnnularValidator extends CamposEntrada<Object> {

    private final ParameterFeign parameterFeign;
    private final CompanyFeign companyFeign;
    private final PaymentVoucherFeign paymentVoucherFeign;

    public void validateVoucherAnnular(List<VoucherAnnularRequest> documentos, String rucEmisor) {
        ParameterModel parametroEntity = parameterFeign.findByName(ConstantesParameter.RANGO_DIAS_BAJA_DOCUMENTOS);
        Integer rangoFechaAceptable = Integer.parseInt(parametroEntity.getValue());

        validateRucActivo(rucEmisor);
        for (VoucherAnnularRequest documento : documentos) {
            boolean isDocumentFacturaOrNotaAsociada = false;
            String tipoComprobanteRelacionado;
            validateTipoComprobante(documento.getTipoComprobante());
            validateSerie(documento.getSerie());
            validateNumero(documento.getNumero());
            validateTipoComprobanteRelacionado(documento.getTipoComprobante(), documento.getTipoComprobanteRelacionado());
            validateMotivoAnulacion(documento.getMotivoAnulacion());

            String identificadorDocumento = rucEmisor + "-" + documento.getTipoComprobante() + "-" +
                    documento.getSerie().toUpperCase() + "-" + documento.getNumero();

            if (documento.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
                isDocumentFacturaOrNotaAsociada = true;
            } else {
                if (documento.getTipoComprobanteRelacionado() != null &&
                        documento.getTipoComprobanteRelacionado().equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
                    isDocumentFacturaOrNotaAsociada = true;
                }
            }
            validaFechaBajaByDocumento(
                    identificadorDocumento,
                    rangoFechaAceptable,
                    isDocumentFacturaOrNotaAsociada,
                    documento.getTipoComprobante());
        }
    }

    private void validaFechaBajaByDocumento(String identificadorDocumento, Integer rangoFechaAceptable, boolean isDocumentFacturaOrNotaAsociada, String tipoDocumento) {
        String mensajeValidacion = null;
        boolean noExiste = false;
        EstadoComprobanteEnum estadoComprobante = null;
        System.out.println("tipo de documento: "+tipoDocumento.getClass().getSimpleName());

        PaymentVoucherModel entity = paymentVoucherFeign.getIdentificadorDocument(identificadorDocumento);
        if (entity==null){
            noExiste=true;
        }else {
            estadoComprobante = EstadoComprobanteEnum.getEstadoComprobante(entity.getEstado());
        }
        if (noExiste) {
            throw new ValidationException("El comprobante no ha sido anulado, "
                    + "Por este medio solo se podrá anular los documentos que previamente han "
                    + "sido registrados desde el API-REST.");
        }
        switch (estadoComprobante) {
            case REGISTRADO:
                if (isDocumentFacturaOrNotaAsociada) {
                    mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                            + "debido a que para anular una factura o nota asociada a una factura,"
                            + " este debe estar previamente acpetada por la Sunat.";
                }
                break;
            case ACEPTADO:
            case ACEPTADO_ADVERTENCIA:
                break;
            case ACEPTADO_POR_VERIFICAR:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido a que dicho comprobante necesita verificar su resultado por la Sunat.";
                break;
            case PENDIENTE_ANULACION:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido a que dicho comprobante se en encuentra en pendiente de anulacion.";
                break;
            /*case ANULADO:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido que dicho comprobante ya se encuentra anulado por la Sunat.";
                break;*/
            case PROCESO_ENVIO:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido que dicho comprobante esta en proceso por la Sunat.";
                break;
            case RECHAZADO:
            case ERROR:
                mensajeValidacion = "El comprobante no ha sido anulado, " + identificadorDocumento + ", "
                        + "debido que dicho comprobante ha sido rechazado o tiene errores reportado" + " por la Sunat.";
                break;
        }
        if (mensajeValidacion != null) {
            throw new ValidationException(mensajeValidacion);
        }
    }

    private void validateMotivoAnulacion(String motivoAnulacion) {
        if (StringUtils.isBlank(motivoAnulacion)) {
            throw new ValidationException("El campo [" + motivoToAnularLabel + "] es obligatorio.");
        }
        if (StringUtils.length(motivoAnulacion) > 100) {
            throw new ValidationException("El campo [" + motivoToAnularLabel + "] debe tener como longitud maxima 100 caracteres.");
        }
    }

    private void validateTipoComprobanteRelacionado(String tipoComprobante, String tipoComprobanteRelacionado) {
        if (tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO) ||
                tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
            if (StringUtils.isBlank(tipoComprobanteRelacionado)) {
                throw new ValidationException("El campo [" + tipoComprobanteRelacionadoToAnularLabel + "] es obligatorio, cuando "
                        + tipoComprobanteToAnularLabel + " es 07: Nota Credito, 08: Nota Debito");
            }
            if (!(tipoComprobanteRelacionado.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA))) {
                throw new ValidationException("EL campo [" + tipoComprobanteRelacionadoToAnularLabel + "] contiene un valor No Valido. "
                        + "Valores permitidos 01: Factura");
            }
        }
    }

    private void validateNumero(Integer numero) {
        if (numero == null) {
            throw new ValidationException("El campo [" + numeroToAnularLabel + "] es obligatorio.");
        }
        if (numero < 1) {
            throw new ValidationException("El campo [" + numeroToAnularLabel + "] no es valido.");
        }
        if (StringUtils.length(numero.toString()) > 8) {
            throw new ValidationException("El campo [" + numeroToAnularLabel + "] debe tener como maximo 8 digitos.");
        }
    }

    private void validateSerie(String serie) {
        if (StringUtils.isBlank(serie)) {
            throw new ValidationException("El campo [" + serieToAnularLabel + "] es obligatorio.");
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            throw new ValidationException("El campo [" + serieToAnularLabel + "] recibe caracteres alfabeticos y numericos.");
        }
        if (StringUtils.length(serie) != 4) {
            throw new ValidationException("El campo [" + serieToAnularLabel + "] Debe ser alfanumerico de 4 caracteres.");
        }
    }

    private void validateTipoComprobante(String tipoComprobante) {
        if (StringUtils.isBlank(tipoComprobante)) {
            throw new ValidationException("El campo [" + tipoComprobanteToAnularLabel + "] es obligatorio.");
        }
        if (!(tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)
        )) {
            throw new ValidationException("El campo [" + tipoComprobanteToAnularLabel + "] contiene un valor no valido. "
                    + "Valores permitidos 01: Factura, 07: Nota Credito, 08: Nota Debito");
        }
    }

    private void validateRucActivo(String rucEmisor) {
        String estado = companyFeign.getStateFromCompanyByRuc(rucEmisor);
        if (!estado.equals(ConstantesParameter.REGISTRO_ACTIVO)) {
            throw new ValidationException("El ruc emisor [" + rucEmisor + "] No se encuentra habilitado para "
                    + "ejecutar operaciones al API-REST.");
        }
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return null;
    }


}
