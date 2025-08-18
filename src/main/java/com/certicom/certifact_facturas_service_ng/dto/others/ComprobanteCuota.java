package com.certicom.certifact_facturas_service_ng.dto.others;

import com.certicom.certifact_facturas_service_ng.entity.CuotaComprobanteEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComprobanteCuota {

    private Integer numero;
    private BigDecimal monto;
    private String fecha;

}
