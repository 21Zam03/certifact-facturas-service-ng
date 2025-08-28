package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Signature implements Serializable {

    private String id;
    private String denominacionEmisor;
    private String rucEmisor;
    private String uri;

}
