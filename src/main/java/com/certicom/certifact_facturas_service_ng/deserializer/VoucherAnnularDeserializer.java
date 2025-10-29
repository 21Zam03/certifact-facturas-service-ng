package com.certicom.certifact_facturas_service_ng.deserializer;

import com.certicom.certifact_facturas_service_ng.request.VoucherAnnularRequest;
import com.certicom.certifact_facturas_service_ng.exceptions.DeserializerException;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class VoucherAnnularDeserializer extends CamposEntrada<VoucherAnnularRequest> {

    @Override
    public VoucherAnnularRequest deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JacksonException {
        VoucherAnnularRequest objectResult;
        JsonNode campoTrama;
        JsonNode trama;
        String mensajeError;

        String tipoComprobanteRelacionado = null;
        String tipoComprobante;
        String serieDocumento = null;
        String motivoAnulacion = null;
        Integer numeroDocumento = null;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(tipoComprobanteToAnularLabel);
        tipoComprobante = (campoTrama != null) ? campoTrama.textValue() : null;
        campoTrama = trama.get(serieToAnularLabel);
        if (campoTrama != null) {
            if (campoTrama.isTextual()) {
                serieDocumento = campoTrama.textValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "71[" + serieToAnularLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(numeroToAnularLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numeroDocumento = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + numeroToAnularLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(motivoToAnularLabel);
        if (campoTrama != null) {
            if (campoTrama.isTextual()) {
                motivoAnulacion = campoTrama.textValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "72[" + motivoToAnularLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        campoTrama = trama.get(tipoComprobanteRelacionadoToAnularLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isTextual()) {
                tipoComprobanteRelacionado = campoTrama.textValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "73[" + tipoComprobanteRelacionadoToAnularLabel + "]";
                throw new DeserializerException(mensajeError);
            }
        }
        objectResult = new VoucherAnnularRequest();
        objectResult.setNumero(numeroDocumento);
        objectResult.setSerie(serieDocumento);
        objectResult.setTipoComprobante(tipoComprobante);
        objectResult.setTipoComprobanteRelacionado(tipoComprobanteRelacionado);
        objectResult.setMotivoAnulacion(motivoAnulacion);

        return objectResult;
    }
}
