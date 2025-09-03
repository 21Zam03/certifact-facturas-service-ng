package com.certicom.certifact_facturas_service_ng.deserializer;

import com.certicom.certifact_facturas_service_ng.dto.others.CampoAdicional;
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
public class CampoAdicionalDeserializer extends CamposEntrada<CampoAdicional> {


    @Override
    public CampoAdicional deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        CampoAdicional objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        String nombreCampo = null;
        String valorCampo = null;

        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);

        campoTrama = trama.get(nombreCampoAdicionalLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "5[" + nombreCampoAdicionalLabel + "]";
                throw new DeserializerException(mensajeError);
            } else {
                nombreCampo = campoTrama.textValue();
            }
        }

        campoTrama = trama.get(valorCampoAdicionalLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                //mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "6[" + valorCampoAdicionalLabel + "]";
                //throw new DeserializerException(mensajeError);
                valorCampo = "-";
            } else {
                valorCampo = campoTrama.textValue().length()>1800?
                        campoTrama.textValue().substring(0,1800):campoTrama.textValue();
            }
        }

        objectResult = new CampoAdicional();
        objectResult.setNombreCampo(nombreCampo);
        objectResult.setValorCampo(valorCampo);

        return objectResult;
    }
}
