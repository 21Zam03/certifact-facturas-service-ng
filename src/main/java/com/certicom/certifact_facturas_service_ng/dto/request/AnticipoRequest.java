package com.certicom.certifact_facturas_service_ng.dto.request;

import com.certicom.certifact_facturas_service_ng.deserializer.AnticipoDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize(using = AnticipoDeserializer.class)
public class AnticipoRequest implements Serializable {

    private String identificadorPago;
    private String serieAnticipo;
    private Integer numeroAnticipo;
    private String tipoDocumentoAnticipo;
    private BigDecimal montoAnticipado;

}
