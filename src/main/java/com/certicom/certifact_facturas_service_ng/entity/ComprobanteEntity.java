package com.certicom.certifact_facturas_service_ng.entity;

import lombok.Data;

import java.util.List;

@Data
public class ComprobanteEntity {

    private Long idPaymentVoucher;
    private List<ComprobanteArchivoEntity> comprobanteArchivoEntityList;



}
