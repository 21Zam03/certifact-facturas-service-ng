package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.dto.model.EmpresaDto;
import com.certicom.certifact_facturas_service_ng.entity.SubidaRegistroArchivoEntity;
import com.certicom.certifact_facturas_service_ng.service.AmazonService;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class AmazonServiceImpl implements AmazonService {

    @Override
    public SubidaRegistroArchivoEntity uploadFileStorage(InputStream inputStream, String nameFile, String folder, EmpresaDto company) {
        return null;
    }
}
