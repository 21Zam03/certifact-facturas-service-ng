package com.certicom.certifact_facturas_service_ng.controller.web;

import com.certicom.certifact_facturas_service_ng.enums.TipoArchivoEnum;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.google.common.io.ByteStreams;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping(DescargaController.API_PATH)
@RequiredArgsConstructor
public class DescargaController {

    public static final String API_PATH = "/api";

    private final AmazonS3ClientService amazonS3ClientService;

    @GetMapping("/descargacdruuid/{id}/{uuid}/{nameDocument}")
    public ResponseEntity<?> downloadCDR(
            @PathVariable Long id, @PathVariable String uuid,
            @PathVariable String nameDocument, HttpServletRequest request) throws IOException {
        InputStream is = amazonS3ClientService.downloadFileInvoice(id, uuid, TipoArchivoEnum.CDR);
        byte[] targetArray = ByteStreams.toByteArray(is);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);
    }

    @GetMapping("/descargaxmluuid/{id}/{uuid}/{nameDocument}")
    public ResponseEntity<?> downsloadXml(@PathVariable Long id, @PathVariable String uuid, @PathVariable String nameDocument) throws IOException {
        InputStream is = amazonS3ClientService.downloadFileInvoice(id, uuid, TipoArchivoEnum.XML);

        byte[] targetArray = ByteStreams.toByteArray(is);

        ByteArrayResource resource = new ByteArrayResource(targetArray);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(resource);
    }


}
