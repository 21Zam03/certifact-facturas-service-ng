package com.certicom.certifact_facturas_service_ng.deserializer;

import com.certicom.certifact_facturas_service_ng.dto.others.*;
import com.certicom.certifact_facturas_service_ng.request.PaymentVoucherRequest;
import com.certicom.certifact_facturas_service_ng.exceptions.DeserializerException;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ComprobanteDeserializer extends CamposEntrada<PaymentVoucherRequest> {

    private final AnticipoDeserializer anticipoDeserializer;
    private final CampoAdicionalDeserializer campoAdicionalDeserializer;
    private final GuiaRelacionadaDeserializer guiaRelacionadaDeserializer;
    private final CuotaDeserializer cuotaDeserializer;
    private final ComprobanteItemDeserializer  comprobanteItemDeserializer;

    @Override
    public PaymentVoucherRequest deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {

        Iterator<JsonNode> iteratorItems;
        Iterator<JsonNode> iteratorAnticipos;
        Iterator<JsonNode> iteratorCamposAdicionales;
        Iterator<JsonNode> iteratorCuotas;
        Iterator<JsonNode> iteratorGuiasRelacionadas;

        List<ComprobanteItem> items;
        List<Anticipo> anticipos;
        List<CampoAdicional> camposAdicionales;
        List<ComprobanteCuota> cuotas;
        List<GuiaRelacionada> guiaRelacionadas;

        PaymentVoucherRequest objectResult;
        ComprobanteItem item;
        Anticipo anticipo;
        CampoAdicional campoAdicional;
        ComprobanteCuota cuota;
        GuiaRelacionada guiaRelacionada;
        JsonNode campoTrama;
        JsonNode itemJson;
        JsonNode anticipoJson;
        JsonNode campoAdicionalJson;
        JsonNode cuotaJson;
        JsonNode guiaRelacionadaJson;
        JsonNode trama;

        String tipoComprobante = null;
        String serieDocumento = null;
        Integer numeroDocumento = null;
        String fechaEmision;
        String horaEmision;
        String fechaVencimiento;
        String codigoMoneda;

        BigDecimal tipoTransaccion = null;
        BigDecimal montoPendiente = null;
        BigDecimal cantidadCuotas = null;
        BigDecimal pagoCuenta = null;

        /*Integer nCuota=0;
        BigDecimal montoCuota = null;
        BigDecimal montoPendiente = null;
        String numeroCuota;
        String idPaymentVoucherReference=null;*/
        String codigoTipoOperacion;
        String rucEmisor;
        String codigoLocalAnexoEmisor;
        String tipoDocumentoReceptor;
        String numeroDocumentoReceptor;
        String denominacionReceptor;
        String direccionReceptor;
        String emailReceptor;

        String detraccion;

        String serieNumeroOtroDocumentoRelacionado;
        String codigoTipoOtroDocumentoRelacionado;
        BigDecimal totalValorVentaExportacion = null;
        BigDecimal totalValorVentaGravada = null;
        BigDecimal totalValorVentaInafecta = null;
        BigDecimal totalValorVentaExonerada = null;
        BigDecimal totalValorVentaGratuita = null;
        BigDecimal totalValorBaseIsc = null;
        BigDecimal totalValorBaseOtrosTributos = null;
        BigDecimal totalValorVentaGravadaIVAP = null;
        BigDecimal totalImpOperacionGratuita = null;
        BigDecimal totalDescuento = null;
        BigDecimal totalIgv = null;
        BigDecimal totalIsc = null;
        BigDecimal totalOtrostributos = null;
        BigDecimal descuentoGlobales = null;
        BigDecimal sumatoriaOtrosCargos = null;
        BigDecimal importeTotalVenta = null;
        String serieAfectado;
        Integer numeroAfectado = null;
        String tipoComprobanteAfectado;
        String codigoTipoNotaCredito;
        String codigoTipoNotaDebito;
        String motivo;
        String ordenCompra;
        String codigoMedioPago = null;
        String cuentaFinancieraBeneficiario = null;
        String codigoBienDetraccion = null;
        String idpay;
        Integer retencion = null;
        BigDecimal porcentajeDetraccion = null;
        BigDecimal porcentajeRetencion = null;
        BigDecimal montoDetraccion = null;
        BigDecimal montoRetencion = null;
        String hidroCantidad = null;
        String hidroDescripcionTipo = null;
        String hidroEmbarcacion = null;
        String hidroFechaDescarga = null;
        String hidroLugarDescarga = null;
        String hidroMatricula = null;

        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(tipoComprobanteLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "1 [" + tipoComprobanteLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                tipoComprobante = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(serieLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + " 2[" + serieLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                serieDocumento = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(numeroLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numeroDocumento = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + numeroLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(fechaEmisionLabel);
        fechaEmision = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(horaEmisionLabel);
        horaEmision = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(fechaVencimientoLabel);
        fechaVencimiento = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(codigoMonedaLabel);
        codigoMoneda = (campoTrama != null) ? campoTrama.textValue() : null;


        campoTrama = trama.get(tipoTransaccionLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                tipoTransaccion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + tipoTransaccionLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoPendienteLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoPendiente = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + montoPendienteLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(cantidadCuotasLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                cantidadCuotas = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + cantidadCuotasLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(pagoCuentaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                pagoCuenta = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + pagoCuentaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        /*
        campoTrama = trama.get(montoCuotaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoCuota = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoCuotaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(montoPendienteLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoPendiente = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoPendienteLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(nCuotaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                nCuota = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + nCuotaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(numeroCuotaLabel);
        numeroCuota = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(idPaymentVoucherReferenceLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + " [" + idPaymentVoucherReferenceLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                idPaymentVoucherReference = campoTrama.textValue();
            }
        }else{
            idPaymentVoucherReference = null;
        }
        */

        campoTrama = trama.get(tipoOperacionLabel);
        codigoTipoOperacion = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(rucEmisorLabel);
        rucEmisor = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(codigoLocalAnexoEmisorLabel);
        codigoLocalAnexoEmisor = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(tipoDocumentoReceptorLabel);
        tipoDocumentoReceptor = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(numeroDocumentoReceptorLabel);
        numeroDocumentoReceptor = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(denominacionReceptorLabel);
        denominacionReceptor = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(direccionReceptorLabel);
        direccionReceptor = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(emailReceptorLabel);
        emailReceptor = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(idpayLabel);
        idpay = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(porcentajeDetraccionLabel);

        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeDetraccion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeDetraccionLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }


        campoTrama = trama.get(numeroDocumentoRelacionadoLabel);
        serieNumeroOtroDocumentoRelacionado = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(tipoDocumentoRelacionadoLabel);
        codigoTipoOtroDocumentoRelacionado = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(totalValorVentaExportacionlabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaExportacion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaExportacionlabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaGravadaLabel);

        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {

                totalValorVentaGravada = campoTrama.decimalValue();
            } else {

                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaGravadaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorBaseOtrosTributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorBaseOtrosTributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorBaseOtrosTributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorBaseIscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorBaseIsc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorBaseIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalImpOperGratuitaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalImpOperacionGratuita = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalImpOperGratuitaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaInafectaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaInafecta = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaInafectaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaExoneradaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaExonerada = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaExoneradaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaGratuitaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaGratuita = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaGratuitaLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalValorVentaGravadaIVAPLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalValorVentaGravadaIVAP = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalValorVentaGravadaIVAPLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalDescuentoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalDescuento = campoTrama.decimalValue();
            } else {
                System.out.println(totalDescuento+ "Total descuento no es un numero y quizas este vacio");
                //mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalDescuentoLabel + "]";
                //throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(totalIgvLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalIgv = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalIgvLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalIscLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalIsc = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalIscLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalOtrostributosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                totalOtrostributos = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + totalOtrostributosLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(descuentoGlobalesLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                descuentoGlobales = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + descuentoGlobalesLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(totalOtrosCargosLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if(campoTrama.toString().length() > 0){
                if (campoTrama.isNumber()) {
                    if(campoTrama.decimalValue().compareTo(BigDecimal.ZERO) > 0){
                        sumatoriaOtrosCargos = campoTrama.decimalValue();
                    }

                } else {
                    mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER +" "+ campoTrama +"[" + totalOtrosCargosLabel + "]";
                    throw new DeserializerException(mensajeError);
                }
            }

        }
        campoTrama = trama.get(importeTotalLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                importeTotalVenta = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + importeTotalLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(serieAfectadoLabel);
        serieAfectado = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(numeroAfectadoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numeroAfectado = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + numeroAfectadoLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }


        campoTrama = trama.get(ordenCompraLabel);
        ordenCompra = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(tipoComprobanteAfectadoLabel);
        tipoComprobanteAfectado = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(tipoNotaCreditoLabel);
        codigoTipoNotaCredito = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(tipoNotaDebitoLabel);
        codigoTipoNotaDebito = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(motivoNotaLabel);
        motivo = (campoTrama != null) ? campoTrama.textValue() : null;

        anticipos = null;
        if (trama.get(anticiposLabel) != null) {

            anticipos = new ArrayList<Anticipo>();
            iteratorAnticipos = trama.get(anticiposLabel).elements();
            while (iteratorAnticipos.hasNext()) {

                anticipoJson = iteratorAnticipos.next();
                anticipo = anticipoDeserializer.deserialize(anticipoJson.traverse(jsonParser.getCodec()), context);
                anticipos.add(anticipo);
            }
        }

        camposAdicionales = null;
        if (trama.get(camposAdicionalesLabel) != null) {

            camposAdicionales = new ArrayList<CampoAdicional>();
            iteratorCamposAdicionales = trama.get(camposAdicionalesLabel).elements();
            while (iteratorCamposAdicionales.hasNext()) {

                campoAdicionalJson = iteratorCamposAdicionales.next();
                campoAdicional = campoAdicionalDeserializer.deserialize(campoAdicionalJson.traverse(jsonParser.getCodec()), context);
                camposAdicionales.add(campoAdicional);
            }
        }

        cuotas = null;
        if (trama.get(cuotasLabel) != null) {

            cuotas = new ArrayList<ComprobanteCuota>();
            iteratorCuotas = trama.get(cuotasLabel).elements();
            while (iteratorCuotas.hasNext()) {

                cuotaJson = iteratorCuotas.next();
                cuota = cuotaDeserializer.deserialize(cuotaJson.traverse(jsonParser.getCodec()), context);
                cuotas.add(cuota);
            }
        }

        guiaRelacionadas = null;
        if (trama.get(guiasRelacionadasLabel) != null) {

            guiaRelacionadas = new ArrayList<GuiaRelacionada>();
            iteratorGuiasRelacionadas = trama.get(guiasRelacionadasLabel).elements();
            while (iteratorGuiasRelacionadas.hasNext()) {

                guiaRelacionadaJson = iteratorGuiasRelacionadas.next();
                guiaRelacionada = guiaRelacionadaDeserializer.deserialize(guiaRelacionadaJson.traverse(jsonParser.getCodec()), context);
                guiaRelacionadas.add(guiaRelacionada);
            }
        }


        items = null;
        if (trama.get(itemsLabel) != null) {

            items = new ArrayList<ComprobanteItem>();
            iteratorItems = trama.get(itemsLabel).elements();
            while (iteratorItems.hasNext()) {

                itemJson = iteratorItems.next();
                item = comprobanteItemDeserializer.deserialize(itemJson.traverse(jsonParser.getCodec()), context);
                items.add(item);
            }
        }

        campoTrama = trama.get(codigoMedioPagoLabel);
        codigoMedioPago = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(cuentaFinancieraBeneficiarioLabel);
        cuentaFinancieraBeneficiario = (campoTrama != null) ? campoTrama.textValue() : null;


        campoTrama = trama.get(codigoBienDetraccionLabel);
        codigoBienDetraccion = (campoTrama != null) ? campoTrama.textValue() : null;

       /* campoTrama = trama.get(hidroCantidadLabel);
        hidroCantidad = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(hidroDescripcionTipoLabel);
        hidroDescripcionTipo = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(hidroEmbarcacionLabel);
        hidroEmbarcacion = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(hidroFechaDescargaLabel);
        hidroFechaDescarga = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(hidroLugarDescargaLabel);
        hidroLugarDescarga = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(hidroMatriculaLabel);
        hidroMatricula = (campoTrama != null) ? campoTrama.textValue() : null;*/

        campoTrama = trama.get(porcentajeDetraccionLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeDetraccion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeDetraccionLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(porcentajeRetencionLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                porcentajeRetencion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + porcentajeRetencionLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(montoDetraccionLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoDetraccion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoDetraccionLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }

        campoTrama = trama.get(montoRetencionLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoRetencion = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoRetencionLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }


        campoTrama = trama.get(detraccionLabel);
        detraccion = (campoTrama != null) ? campoTrama.textValue() : null;

        campoTrama = trama.get(retencionLabel);
        retencion = (campoTrama != null) ? campoTrama.intValue() : null;

        objectResult = new PaymentVoucherRequest();
        objectResult.setTipoComprobante(tipoComprobante);
        objectResult.setSerie(serieDocumento);
        objectResult.setNumero(numeroDocumento);
        objectResult.setFechaEmision(fechaEmision);
        objectResult.setHoraEmision(horaEmision);
        objectResult.setFechaVencimiento(fechaVencimiento);
        objectResult.setCodigoMoneda(codigoMoneda);


        objectResult.setTipoTransaccion(tipoTransaccion);
        objectResult.setMontoPendiente(montoPendiente);
        objectResult.setCantidadCuotas(cantidadCuotas);
        objectResult.setPagoCuenta(pagoCuenta);

        /*
        objectResult.setMontoCuota(montoCuota);
        objectResult.setNCuota(nCuota);
        objectResult.setNumeroCuota(numeroCuota);
        objectResult.setIdPaymentVoucherReference(idPaymentVoucherReference);*/
        objectResult.setCodigoTipoOperacion(codigoTipoOperacion);
        objectResult.setRucEmisor(rucEmisor);
        objectResult.setCodigoLocalAnexoEmisor(codigoLocalAnexoEmisor);
        objectResult.setTipoDocumentoReceptor(tipoDocumentoReceptor);
        objectResult.setNumeroDocumentoReceptor(numeroDocumentoReceptor);
        objectResult.setDenominacionReceptor(denominacionReceptor);
        objectResult.setDireccionReceptor(direccionReceptor);
        objectResult.setEmailReceptor(emailReceptor);
        objectResult.setSerieNumeroOtroDocumentoRelacionado(serieNumeroOtroDocumentoRelacionado);
        objectResult.setCodigoTipoOtroDocumentoRelacionado(codigoTipoOtroDocumentoRelacionado);
        objectResult.setTotalValorVentaExportacion(totalValorVentaExportacion);
        objectResult.setTotalValorVentaGravada(totalValorVentaGravada);
        objectResult.setTotalValorVentaInafecta(totalValorVentaInafecta);
        objectResult.setTotalValorVentaExonerada(totalValorVentaExonerada);
        objectResult.setTotalValorVentaGratuita(totalValorVentaGratuita);
        objectResult.setTotalValorBaseIsc(totalValorBaseIsc);
        objectResult.setTotalValorBaseOtrosTributos(totalValorBaseOtrosTributos);
        objectResult.setTotalValorVentaGravadaIVAP(totalValorVentaGravadaIVAP);
        objectResult.setTotalDescuento(totalDescuento);
        objectResult.setTotalImpOperGratuita(totalImpOperacionGratuita);
        objectResult.setTotalIgv(totalIgv);
        objectResult.setTotalIsc(totalIsc);
        objectResult.setTotalOtrostributos(totalOtrostributos);
        objectResult.setDescuentoGlobales(descuentoGlobales);
        objectResult.setSumatoriaOtrosCargos(sumatoriaOtrosCargos);
        objectResult.setImporteTotalVenta(importeTotalVenta);
        objectResult.setSerieAfectado(serieAfectado);
        objectResult.setNumeroAfectado(numeroAfectado);
        objectResult.setTipoComprobanteAfectado(tipoComprobanteAfectado);
        objectResult.setCodigoTipoNotaCredito(codigoTipoNotaCredito);
        objectResult.setCodigoTipoNotaDebito(codigoTipoNotaDebito);
        objectResult.setMotivoNota(motivo);
        objectResult.setAnticipos(anticipos);
        objectResult.setCuotas(cuotas);
        objectResult.setCamposAdicionales(camposAdicionales);
        objectResult.setGuiasRelacionadas(guiaRelacionadas);
        objectResult.setItems(items);
        objectResult.setOrdenCompra(ordenCompra);
        objectResult.setCodigoMedioPago(codigoMedioPago);
        objectResult.setCuentaFinancieraBeneficiario(cuentaFinancieraBeneficiario);
        objectResult.setCodigoBienDetraccion(codigoBienDetraccion);
        objectResult.setPorcentajeDetraccion(porcentajeDetraccion);
        objectResult.setPorcentajeRetencion(porcentajeRetencion);
        objectResult.setMontoDetraccion(montoDetraccion);
        objectResult.setMontoRetencion(montoRetencion);
        objectResult.setDetraccion(detraccion);
        objectResult.setRetencion(retencion);
        objectResult.setIdpay(idpay);

        return objectResult;
    }
}
