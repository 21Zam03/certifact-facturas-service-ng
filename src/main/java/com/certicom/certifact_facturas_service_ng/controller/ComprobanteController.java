package com.certicom.certifact_facturas_service_ng.controller;

import com.certicom.certifact_facturas_service_ng.dto.request.ComprobanteFiltroRequest;
import com.certicom.certifact_facturas_service_ng.service.ComprobanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
@Slf4j
public class ComprobanteController {

    private final ComprobanteService comprobanteService;

    @GetMapping
    public ResponseEntity<?> listarComprobantesConFiltros(
            @RequestParam(name = "filtroDesde", required = true) String filtroDesde,
            @RequestParam(name = "filtroHasta", required = true) String filtroHasta,
            @RequestParam(name = "filtroTipoComprobante", required = false) String filtroTipoComprobante,
            @RequestParam(name = "filtroRuc", required = false) String filtroRuc,
            @RequestParam(name = "filtroSerie", required = false) String filtroSerie,
            @RequestParam(name = "filtroNumero", required = false) Integer filtroNumero,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber,
            @RequestParam(name = "perPage", required = false) Integer perPage,
            @RequestParam(name = "estadoSunat", required = false) Integer estadoSunat,
            @RequestParam(name = "idUsuario", required = true) Long idUsuario
    ) {
        log.info("ComprobanteController - listarComprobantesConfiltros - [filtroDesde={}, filtroHasta={}, filtroTipoComprobante={}, fltroRuc={}, " +
                        "filtroSerie={}, filtroNumero={}, pageNumber={}, perPage={}, estadoSunat={}, idUsuario={}]",
                filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat, idUsuario);
        Map<String, Object> paginacionComprobantes = comprobanteService.obtenerComprobantesEstadoPorFiltro(
                filtroDesde, filtroHasta, filtroTipoComprobante, filtroRuc, filtroSerie, filtroNumero, pageNumber, perPage, estadoSunat, idUsuario);
        log.info("listarComprobantesConFiltros - paginacionComprobantes={}", paginacionComprobantes);
        return new ResponseEntity<>(paginacionComprobantes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> registrarComprobante() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> editarComprobante() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
