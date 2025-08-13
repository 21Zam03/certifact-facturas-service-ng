package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FirmaResp implements Serializable {

    private Boolean status;
    private ByteArrayOutputStream signatureFile;
    private String digestValue;

}
