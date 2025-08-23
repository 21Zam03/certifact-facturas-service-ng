package com.certicom.certifact_facturas_service_ng.deserializer;

import com.certicom.certifact_facturas_service_ng.dto.others.Anticipo;
import com.certicom.certifact_facturas_service_ng.exceptions.DeserializadorException;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParameter;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
public class AnticipoDeserializer extends CamposEntrada<Anticipo> {

    @Override
    public Anticipo deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        Anticipo objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        String serieAnticipo = null;
        Integer numeroAnticipo = null;
        String tipoDocumentoAnticipo = null;
        BigDecimal montoAnticipado = null;

        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(serieAnticipoLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "3[" + serieAnticipoLabel + "]";
                throw new DeserializadorException(mensajeError);
            } else {
                serieAnticipo = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(numeroAnticipoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.canConvertToInt()) {
                numeroAnticipo = campoTrama.intValue();
            } else {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_INTEGER + "[" + numeroAnticipoLabel + "]";
                throw new DeserializadorException(mensajeError);
            }
        }
        campoTrama = trama.get(tipoDocumentoAnticipoLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "4[" + tipoDocumentoAnticipoLabel + "]";
                throw new DeserializadorException(mensajeError);
            } else {
                tipoDocumentoAnticipo = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(montoAnticipadoLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                montoAnticipado = campoTrama.decimalValue();
            } else {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "[" + montoAnticipadoLabel + "]";
                throw new DeserializadorException(mensajeError);
            }
        }

        objectResult = new Anticipo();
        objectResult.setMontoAnticipado(montoAnticipado);
        objectResult.setNumeroAnticipo(numeroAnticipo);
        objectResult.setSerieAnticipo(serieAnticipo);
        objectResult.setTipoDocumentoAnticipo(tipoDocumentoAnticipo);

        return objectResult;
    }

}
