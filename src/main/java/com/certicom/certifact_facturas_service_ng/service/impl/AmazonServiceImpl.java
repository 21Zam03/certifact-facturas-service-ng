package com.certicom.certifact_facturas_service_ng.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.certicom.certifact_facturas_service_ng.dto.model.EmpresaDto;
import com.certicom.certifact_facturas_service_ng.dto.model.RegisterFileUploadDto;
import com.certicom.certifact_facturas_service_ng.entity.SubidaRegistroArchivoEntity;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.FacturaComprobanteFeign;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.util.UtilArchivo;
import com.certicom.certifact_facturas_service_ng.util.UtilDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonServiceImpl implements AmazonS3ClientService {

    private final FacturaComprobanteFeign facturaComprobanteFeign;

    private final AmazonS3 s3client;

    @Value("${apifact.aws.s3.bucket}")
    private String bucketName;

    @Value("${apifact.aws.s3.baseUrl}")
    private String baseUrl;

    @Override
    public SubidaRegistroArchivoEntity subirArchivoAlStorage(InputStream inputStream, String nameFile, String folder, EmpresaDto company) {
        System.out.println("NOMBRE DEL ARCHIVO "+nameFile);
        String periodo = UtilDate.dateNowToString("MMyyyy ");

        String fileNameKey = String.format("%s-%s", UUID.randomUUID(), nameFile);
        String bucket = String.format("%s/archivos/%s/%s/%s", this.bucketName, company.getRuc(), folder, periodo);

        try {

            StopWatch watch = new StopWatch();
            watch.start();

            ObjectMetadata metadata = new ObjectMetadata();
            byte[] resultByte = DigestUtils.md5(inputStream);
            inputStream.reset();
            byte[] contentBytes = IOUtils.toByteArray(inputStream);
            String streamMD5 = new String(Base64.encodeBase64(resultByte));
            Long contentLength = Long.valueOf(contentBytes.length);
            metadata.setContentMD5(streamMD5);
            metadata.setContentLength(contentLength);

            inputStream.reset();

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileNameKey, inputStream, metadata);

            this.s3client.putObject(putObjectRequest);

            SubidaRegistroArchivoEntity resp = facturaComprobanteFeign.regitrarSubidaArchivo(RegisterFileUploadDto.builder()
                    .bucket(bucket)
                    .nombreGenerado(fileNameKey)
                    .nombreOriginal(nameFile)
                    .codCompany(company.getId())
                    .build());
            watch.stop();
            log.info(String.format("%s %s %s", "Tiempo de Subida de archivo:", nameFile, watch.getTime()));
            return resp;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("error [" + ex.getMessage() + "] occurred while uploading [" + nameFile + "] ");
            throw new ServiceException("Ocurrio un error al subir el archivo: " + ex.getMessage());
        }
    }

    @Override
    public String downloadFileStorageInB64(RegisterFileUploadDto fileStorage) {
        return UtilArchivo.binToB64(downloadFileStorageDto(fileStorage));
    }

    @Override
    public ByteArrayInputStream downloadFileStorageDto(RegisterFileUploadDto fileStorage) {
        String bucket, name;
        if (fileStorage == null ) {
            return new ByteArrayInputStream(new byte[0]);
        }
        if (fileStorage.getIsOld() == null || !fileStorage.getIsOld()) {
            bucket = fileStorage.getBucket();
            name = fileStorage.getNombreGenerado();
        } else {
            bucket = String.format("%s/archivos_old/%s", this.bucketName, fileStorage.getRucCompany());
            name = String.format("%s.%s", fileStorage.getUuid(), fileStorage.getExtensiones());
        }
        System.out.println("bucket: "+bucket);
        System.out.println("name: "+name);
        ByteArrayInputStream ba = null;
        try{
            ba = new ByteArrayInputStream(getFile(bucket, name).toByteArray());
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return ba;
    }

    public ByteArrayOutputStream getFile(String bucketName, String keyName) {

        try {

            StopWatch watch = new StopWatch();
            watch.start();

            S3Object s3object = this.s3client.getObject(new GetObjectRequest(bucketName, keyName));

            InputStream is = s3object.getObjectContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }

            watch.stop();


            return baos;
        } catch (Exception ioe) {
            log.info("NO SE ENCONTRO ARCHIVO EN S3 , BUCKET: "+bucketName + " NAME: "+keyName);
            log.error("Exception SERVICE : " + ioe.getMessage());
            throw new ServiceException("El servicio de storage está fuera de servicio, comuniquese con el administrador.");
        }
    }

}
