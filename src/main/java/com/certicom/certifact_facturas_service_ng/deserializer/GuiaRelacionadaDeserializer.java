package com.certicom.certifact_facturas_service_ng.deserializer;

import com.certicom.certifact_facturas_service_ng.dto.others.GuiaRelacionada;
import com.certicom.certifact_facturas_service_ng.exceptions.DeserializadorException;
import com.certicom.certifact_facturas_service_ng.util.CamposEntrada;
import com.certicom.certifact_facturas_service_ng.util.ConstantesParametro;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GuiaRelacionadaDeserializer extends CamposEntrada<GuiaRelacionada> {

    @Override
    public GuiaRelacionada deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        GuiaRelacionada objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        String codigoTipoGuia = null;
        String serieNumeroGuia = null;
        Long idguiaremision = null;

        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(codigoTipoGuiaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParametro.MSG_RESP_ERROR_DESERIALIZACION_STRING + "24[" + codigoTipoGuiaLabel + "]";
                throw new DeserializadorException(mensajeError);
            } else {
                codigoTipoGuia = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(serieNumeroGuiaLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParametro.MSG_RESP_ERROR_DESERIALIZACION_STRING + "25[" + serieNumeroGuiaLabel + "]";
                throw new DeserializadorException(mensajeError);
            } else {
                serieNumeroGuia = campoTrama.textValue();
            }
        }
        campoTrama = trama.get(idguiaremisionLabel);
        if (campoTrama != null && !campoTrama.isNull()) {
            if (campoTrama.isNumber()) {
                idguiaremision = campoTrama.longValue();
            } else {
                mensajeError = ConstantesParametro.MSG_RESP_ERROR_DESERIALIZACION_STRING + "26[" + idguiaremisionLabel + "]";
                throw new DeserializadorException(mensajeError);
            }
        }

        objectResult = new GuiaRelacionada();
        objectResult.setCodigoTipoGuia(codigoTipoGuia);
        objectResult.setSerieNumeroGuia(serieNumeroGuia);
        objectResult.setIdguiaremision(idguiaremision);


        return objectResult;
    }
}
