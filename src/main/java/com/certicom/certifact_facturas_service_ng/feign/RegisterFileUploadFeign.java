package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.RegisterFileUpload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "registerFileUpload")
public interface RegisterFileUploadFeign {

    @GetMapping("/api/register-file-upload")
    public RegisterFileUpload findFirst1ByPaymentVoucherIdPaymentVoucherAndTipoArchivoAndEstadoArchivoOrderByOrdenDesc
            (@RequestParam Long idPayment, @RequestParam String tipoArchivo, @RequestParam String estadoArchivo);

    @PostMapping("/api/register-file-upload")
    public RegisterFileUpload saveRegisterFileUpload(@RequestBody RegisterFileUpload registerFileUploadDto);

    @GetMapping("/api/register-file-upload/id&uuid&tipo")
    public RegisterFileUpload findByIdPaymentVoucherAndUuidTipo(@RequestParam Long id, @RequestParam String uuid, @RequestParam String tipo);

}
