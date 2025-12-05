package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EmailCompanyNotifyDto implements Serializable {

    private Long id;
    private String email;
    private boolean estado;
    private Long codCompany;

}
