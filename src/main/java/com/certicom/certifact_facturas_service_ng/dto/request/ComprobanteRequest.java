package com.certicom.certifact_facturas_service_ng.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ComprobanteRequest {

    @NotBlank(message = "El campo [documento_tipo_comprobante] es obligatorio")
    private String tipoComprobante;

    @NotBlank(message = "El campo [serie] es obligatorio")
    private String serie;

    @NotBlank(message = "El campo [numero] es obligatorio")
    private Integer numero;

    private String fechaEmision;
    private String horaEmision;
    private String fechaVencimiento;
    private String codigoMoneda;
    private String codigoTipoOperacion;
    private String codigoTipoOperacionCatalogo51;
    private String rucEmisor;
    private String direccionOficinaEmisor;
    private String codigoLocalAnexoEmisor;
    private String tipoDocumentoReceptor;
    private String numeroDocumentoReceptor;
    private String denominacionReceptor;
    private String direccionReceptor;
    private String emailReceptor;

}
