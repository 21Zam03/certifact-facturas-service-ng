package com.certicom.certifact_facturas_service_ng.validation;

import com.certicom.certifact_facturas_service_ng.exceptions.ExcepcionCamposValidacion;
import com.certicom.certifact_facturas_service_ng.feign.ComprobanteFeign;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParametro;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.apache.commons.lang3.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ComprobanteValidate extends CamposEntrada<Object> {

    private final ComprobanteFeign comprobanteFeign;

    private static final BigDecimal MONTO_MINIMO_BOLETA_TO_DATOS_CLIENTE = new BigDecimal("700");

    /*

    public void validarComprobante(ComprobanteDto comprobanteDto, Boolean isEdit) {
        boolean datosReceptorObligatorio;
        String identificadorDocumento;

        validarRucActivo(comprobanteDto.getRucEmisor());
        validarTipoComprobante(comprobanteDto.getTipoComprobante());

        validarSerie(paymentVoucher.getSerie(), paymentVoucher.getTipoComprobante(),
                paymentVoucher.getTipoComprobanteAfectado());

        validateNumero(paymentVoucher.getNumero());
        Integer proximo = comprobantesService.getSiguienteNumeroComprobante(paymentVoucher.getTipoComprobante(),
                paymentVoucher.getSerie(),paymentVoucher.getRucEmisor());
        if (proximo > 1){
            validateNumeracion(paymentVoucher.getNumero(),proximo);
        }

        identificadorDocumento = validateIdentificadorDocumento(paymentVoucher.getRucEmisor(), paymentVoucher.getTipoComprobante(),
                paymentVoucher.getSerie().toUpperCase(), paymentVoucher.getNumero(), isEdit);

        validateFechaEmision(paymentVoucher.getFechaEmision(), paymentVoucher.getTipoComprobante());
        validateHoraEmision(paymentVoucher.getHoraEmision(), paymentVoucher.getFechaEmision());
        validateTipoMoneda(paymentVoucher.getCodigoMoneda());
        validateImporteTotal(paymentVoucher.getImporteTotalVenta());

        datosReceptorObligatorio = validateObligatoriedadDatosCliente(paymentVoucher.getTipoComprobante(), paymentVoucher.getTipoComprobanteAfectado(), paymentVoucher.getImporteTotalVenta());

        validateTipoDocumentoReceptor(paymentVoucher.getTipoDocumentoReceptor(), datosReceptorObligatorio);
        validateNumeroDocumentoReceptor(paymentVoucher.getNumeroDocumentoReceptor(),
                paymentVoucher.getTipoDocumentoReceptor(), datosReceptorObligatorio);
        validateDenominacionReceptor(paymentVoucher.getDenominacionReceptor(), datosReceptorObligatorio);

        validateDomicilioFiscalEmisor(paymentVoucher.getCodigoLocalAnexoEmisor());

        validateTipoDocumentoRelacionado(paymentVoucher.getCodigoTipoOtroDocumentoRelacionado());
        validateNumeroDocumentoRelacionado(paymentVoucher.getSerieNumeroOtroDocumentoRelacionado(),
                paymentVoucher.getCodigoTipoOtroDocumentoRelacionado());
        validateAnticipos(paymentVoucher.getAnticipos());
        validateTotalOpe(paymentVoucher);
        validateItems(paymentVoucher.getItems(), paymentVoucher.getTipoComprobante(), paymentVoucher.getUblVersion(),paymentVoucher.getRucEmisor());


        if (paymentVoucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_1)) {
            validateTotalIsc(paymentVoucher.getTotalValorBaseIsc(), paymentVoucher.getTotalIsc(), paymentVoucher.getItems());
            validateTotalGratuita(paymentVoucher.getTotalValorVentaGratuita(), paymentVoucher.getTotalImpOperGratuita(), paymentVoucher.getItems());
            validateTotalGravada(paymentVoucher.getTotalValorVentaGravada(), paymentVoucher.getTotalIgv(), paymentVoucher.getItems());
            validateTotalOtrosTributos(paymentVoucher.getTotalValorBaseOtrosTributos(), paymentVoucher.getTotalOtrostributos(), paymentVoucher.getItems());
        }

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
        paymentVoucher.setIdentificadorDocumento(identificadorDocumento);
        switch (paymentVoucher.getTipoComprobante()) {

            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                validateTipoDocumentoReceptorFactura(paymentVoucher.getTipoDocumentoReceptor());
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:

                paymentVoucher.setSerieAfectado(null);
                paymentVoucher.setNumeroAfectado(null);
                paymentVoucher.setTipoComprobanteAfectado(null);
                paymentVoucher.setMotivoNota(null);
                paymentVoucher.setCodigoTipoNotaDebito(null);
                paymentVoucher.setCodigoTipoNotaCredito(null);

                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:

                if (paymentVoucher.getUblVersion().equals(ConstantesSunat.UBL_VERSION_2_0)) {
                    paymentVoucher.setTotalValorVentaGratuita(null);
                }
                paymentVoucher.setDescuentoGlobales(null);
                paymentVoucher.setSerieAfectado(paymentVoucher.getSerieAfectado().toUpperCase());
                //paymentVoucher.setAnticipos(null);

                if (paymentVoucher.getTipoComprobante().equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)) {
                    paymentVoucher.setCodigoTipoNotaCredito(null);
                    paymentVoucher.setTotalDescuento(null);
                } else {
                    paymentVoucher.setCodigoTipoNotaDebito(null);
                }
                break;
            default:
        }


        if (StringUtils.isBlank(paymentVoucher.getDenominacionReceptor())) {
            paymentVoucher.setDenominacionReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucher.getNumeroDocumentoReceptor())) {
            paymentVoucher.setNumeroDocumentoReceptor("-");
        }
        if (StringUtils.isBlank(paymentVoucher.getTipoDocumentoReceptor())) {
            paymentVoucher.setTipoDocumentoReceptor("-");
        }

        validateDetracciones(paymentVoucher);
    }

    */

    private void validarRucActivo(String rucEmisor) throws ExcepcionCamposValidacion {
        String mensajeValidacion = null;
        String estado = comprobanteFeign.obtenerEstadoEmpresaPorRuc(rucEmisor);

        if (!estado.equals(ConstantesParametro.REGISTRO_ACTIVO)) {
            mensajeValidacion = "El ruc emisor [" + rucEmisor + "] No se encuentra habilitado para "
                    + "ejecutar operaciones al API-REST.";
            throw new ExcepcionCamposValidacion(mensajeValidacion);
        }
    }

    private void validarTipoComprobante(String tipoComprobante) throws ExcepcionCamposValidacion {

        String mensajeValidacion = null;
        if (StringUtils.isBlank(tipoComprobante)) {
            mensajeValidacion = "El campo [" + tipoComprobanteLabel + "] es obligatorio.";
            throw new ExcepcionCamposValidacion(mensajeValidacion);
        }
        if (!(tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO)
                || tipoComprobante.equals(ConstantesSunat.TIPO_DOCUMENTO_GUIA_REMISION)
        )) {
            mensajeValidacion = "EL campo [" + tipoComprobanteLabel + "] contiene un valor No Valido. Valores permitidos 01: Factura, "
                    + "03: Boleta, 07: Nota Credito, 08: Nota Debito";
            throw new ExcepcionCamposValidacion(mensajeValidacion);
        }
    }

    /*

    private void validarSerie(String serie, String tipoComprobante, String tipoComprobanteAfectado) {
        String mensajeValidacion = null;
        if (StringUtils.isBlank(serie)) {
            mensajeValidacion = "El campo [" + serieLabel + "] es obligatorio.";
            throw new ExcepcionCamposValidacion(mensajeValidacion);
        }
        if (!StringUtils.isAlphanumeric(serie)) {
            mensajeValidacion = "El campo [" + serieLabel + "] recibe caracteres del alfabeto y números.";
            throw new ExcepcionCamposValidacion(mensajeValidacion);
        }
        if (StringUtils.length(serie) != 4) {
            mensajeValidacion = "El campo [" + serieLabel + "] debe ser alfanumerico de 4 caracteres.";
            throw new ExcepcionCamposValidacion(mensajeValidacion);
        }

        switch (tipoComprobante) {
            case ConstantesSunat.TIPO_DOCUMENTO_FACTURA:
                validateSerieFactura(serie);
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_BOLETA:
                validateSerieBoleta(serie);
                break;
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_CREDITO:
            case ConstantesSunat.TIPO_DOCUMENTO_NOTA_DEBITO:
                if (tipoComprobanteAfectado.equals(ConstantesSunat.TIPO_DOCUMENTO_FACTURA)) {
                    validateSerieFactura(serie);
                } else if (tipoComprobanteAfectado.equals(ConstantesSunat.TIPO_DOCUMENTO_BOLETA)) {
                    validateSerieBoleta(serie);
                }
        }
    }

     */
    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        // TODO Auto-generated method stub
        return null;
    }

}
