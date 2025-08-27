package com.certicom.certifact_facturas_service_ng.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(FileController.API_PATH)
public class FileController {


    public static final String API_PATH = "/api/file";

    public ResponseEntity<?> downloadPdf() {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    public ResponseEntity<?> downloadCDR() {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    public ResponseEntity<?> downsloadXml(){
        return new ResponseEntity<>("", HttpStatus.OK);
    }

}
