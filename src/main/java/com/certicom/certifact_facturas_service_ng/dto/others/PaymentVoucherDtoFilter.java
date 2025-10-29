package com.certicom.certifact_facturas_service_ng.dto.others;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PaymentVoucherDtoFilter {

    private String fechaEmision;
    private String tipoComprobante;
    private String serie;
    private Integer numero;
    private String tipoComprobanteAfectado;
    private String serieAfectado;
    private Integer numeroAfectado;
    private String numDocIdentReceptor;
    private String denominacionReceptor;
    private String codigoMoneda;
    private BigDecimal importeTotalVenta;
    private String estado;
    private LocalDate fechaRegistro;
    private String rucEmisor;
    private String estadoSunat;
    private String identificadorBaja;
    private String emailReceptor;
    private Long idPaymentVoucher;
    private String sernumguia; // t.serie || '-' || t.numero
    private String identificadorDocumento;
    private String uuid;
    private String mensajeRespuesta;
    private Boolean pagado;

}
