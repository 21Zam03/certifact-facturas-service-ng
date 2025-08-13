package com.certicom.certifact_facturas_service_ng.dto.request;

import com.certicom.certifact_facturas_service_ng.validation.RucActivo;
import com.certicom.certifact_facturas_service_ng.validation.TipoComprobanteFactura;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ComprobanteRequest {

    @NotBlank(message = "El campo [documento_tipo_comprobante] es obligatorio")
    @TipoComprobanteFactura
    private String tipoComprobante;

    @NotBlank(message = "El campo [serie] es obligatorio")
    private String serie;

    @NotNull(message = "El campo [numero] es obligatorio")
    private Integer numero;

    private String fechaEmision;
    private String horaEmision;
    private String fechaVencimiento;
    private String codigoMoneda;
    private String codigoTipoOperacion;
    private String codigoTipoOperacionCatalogo51;

    @RucActivo
    private String rucEmisor;

    private String direccionOficinaEmisor;
    private String codigoLocalAnexoEmisor;
    private String tipoDocumentoReceptor;
    private String numeroDocumentoReceptor;
    private String denominacionReceptor;
    private String direccionReceptor;
    private String emailReceptor;

}
