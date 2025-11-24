package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EmailCompanyNotifyDto {

    private Long id;
    private String email;
    private boolean estado;
    private Long codCompany;

}
