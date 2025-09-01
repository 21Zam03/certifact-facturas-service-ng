package com.certicom.certifact_facturas_service_ng.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BranchOffices {

    private Integer id;
    private Long codCompany;

}
