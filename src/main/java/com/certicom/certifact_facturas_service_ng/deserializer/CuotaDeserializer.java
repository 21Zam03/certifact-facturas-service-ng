package com.certicom.certifact_facturas_service_ng.deserializer;

import com.certicom.certifact_facturas_service_ng.dto.others.ComprobanteCuota;
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
public class CuotaDeserializer extends CamposEntrada<ComprobanteCuota> {

    @Override
    public ComprobanteCuota deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        ComprobanteCuota objectResult;
        JsonNode trama;
        JsonNode campoTrama;

        Integer numero = null;
        BigDecimal monto = null;
        String fecha = null;

        String mensajeError;

        trama = jsonParser.getCodec().readTree(jsonParser);
        System.out.println("TRAMA");
        System.out.println(trama);
        campoTrama = trama.get(numeroCuotaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isNumber()) {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "5[" + numeroCuotaLabel + "]";
                throw new DeserializadorException(mensajeError);
            } else {
                numero = campoTrama.intValue();
            }
        }

        campoTrama = trama.get(montoCuotaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isNumber()) {
                mensajeError = ConstantesParameter.MSG_ERROR_DESERIALIZACION_NUMBER + "6[" + montoCuotaLabel + "]";
                throw new DeserializadorException(mensajeError);
            } else {
                monto = campoTrama.decimalValue();
            }
        }
        campoTrama = trama.get(fechaCuotaLabel);
        if (campoTrama != null) {
            if (!campoTrama.isTextual()) {
                mensajeError = ConstantesParameter.MSG_RESP_ERROR_DESERIALIZACION_STRING + "6[" + fechaCuotaLabel + "]";
                throw new DeserializadorException(mensajeError);
            } else {
                fecha = campoTrama.textValue();
            }
        }

        objectResult = new ComprobanteCuota();
        objectResult.setNumero(numero);
        objectResult.setMonto(monto);
        objectResult.setFecha(fecha);

        return objectResult;
    }
}
