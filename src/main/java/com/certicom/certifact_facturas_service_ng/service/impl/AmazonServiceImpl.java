package com.certicom.certifact_facturas_service_ng.service.impl;

import com.certicom.certifact_facturas_service_ng.model.CompanyModel;
import com.certicom.certifact_facturas_service_ng.model.RegisterFileUploadModel;
import com.certicom.certifact_facturas_service_ng.enums.TipoArchivoEnum;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.PaymentVoucherData;
import com.certicom.certifact_facturas_service_ng.feign.RegisterFileUploadData;
import com.certicom.certifact_facturas_service_ng.service.AmazonS3ClientService;
import com.certicom.certifact_facturas_service_ng.util.LogHelper;
import com.certicom.certifact_facturas_service_ng.util.LogMessages;
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
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonServiceImpl implements AmazonS3ClientService {

    private final PaymentVoucherData paymentVoucherData;
    private final RegisterFileUploadData registerFileUploadData;

    private final S3Client s3client;

    @Value("${apifact.aws.s3.bucket}")
    private String bucketName;

    @Value("${apifact.aws.s3.baseUrl}")
    private String baseUrl;

    @Override
    public RegisterFileUploadModel subirArchivoAlStorage(InputStream inputStream, String nameFile, String folder, CompanyModel companyModel) {
        String periodo = UtilDate.dateNowToString("MMyyyy");

        String bucket = this.bucketName;

        String fileNameKey = String.format("archivos/%s/%s/%s/%s-%s",
                companyModel.getRuc(),
                folder,
                periodo,
                UUID.randomUUID(),
                nameFile);
        try {
            StopWatch watch = new StopWatch();
            watch.start();

            byte[] contentBytes = IOUtils.toByteArray(inputStream);

            byte[] md5Bytes = DigestUtils.md5(contentBytes);
            String md5Base64 = Base64.encodeBase64String(md5Bytes);

            Map<String, String> metadata = new HashMap<>();
            metadata.put("original-filename", nameFile);
            metadata.put("uploaded-by", companyModel.getRuc());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileNameKey)
                    .contentMD5(md5Base64)
                    .metadata(metadata)
                    .build();

            s3client.putObject(putObjectRequest, RequestBody.fromBytes(contentBytes));

            RegisterFileUploadModel resp = registerFileUploadData.saveRegisterFileUpload(RegisterFileUploadModel.builder()
                    .estado("A")
                    .bucket(bucket)
                    .nombreGenerado(fileNameKey)
                    .nombreOriginal(nameFile)
                    .codCompany(companyModel.getId())
                    .fechaUpload(new Timestamp(System.currentTimeMillis()))
                    .build());

            watch.stop();
            LogHelper.infoLog(LogMessages.currentMethod(),
                    "El archivo se ha subido exitosamente en " + watch.getTime() + "ms");
            return resp;

        } catch (Exception ex) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Ocurrio un error al subir archivo", ex);
            throw new ServiceException("Ocurrió un error al subir el archivo: " + ex.getMessage());
        }
    }

    @Override
    public String downloadFileStorageInB64(RegisterFileUploadModel fileStorage) {
        return UtilArchivo.binToB64(downloadFileStorageDto(fileStorage));
    }

    @Override
    public ByteArrayInputStream downloadFileStorageDto(RegisterFileUploadModel fileStorage) {
        String bucket, name;
        if (fileStorage == null ) {
            return new ByteArrayInputStream(new byte[0]);
        }
        if (fileStorage.getIsOld() == null || !fileStorage.getIsOld()) {
            bucket = fileStorage.getBucket();
            name = fileStorage.getNombreGenerado();
        } else {
            bucket = String.format("%s/archivos_old/%s", this.bucketName, fileStorage.getRucCompany());
            name = String.format("%s.%s", fileStorage.getUuid(), fileStorage.getExtension());
        }
        ByteArrayInputStream ba = null;
        try{
            ba = new ByteArrayInputStream(getFile(bucket, name).toByteArray());
        }catch (Exception e){
            LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error al descargar archivo", e);
            throw new ServiceException("Ocurrio un error al descargar archivo", e.getMessage());
        }
        return ba;
    }

    @Override
    public RegisterFileUploadModel uploadFileStorage(InputStream inputStream, String nameFile, String folder, CompanyModel companyModel) {
        String periodo = UtilDate.dateNowToString("MMyyyy");

        // Bucket fijo (sin '/')
        String bucket = this.bucketName;

        // key (ruta dentro del bucket)
        String fileNameKey = String.format("archivos/%s/%s/%s/%s-%s",
                companyModel.getRuc(),
                folder,
                periodo,
                UUID.randomUUID(),
                nameFile);
        try {
            StopWatch watch = new StopWatch();
            watch.start();

            byte[] contentBytes = IOUtils.toByteArray(inputStream);

            byte[] md5Bytes = DigestUtils.md5(contentBytes);
            String md5Base64 = Base64.encodeBase64String(md5Bytes);

            Map<String, String> metadata = new HashMap<>();
            metadata.put("original-filename", nameFile);
            metadata.put("uploaded-by", companyModel.getRuc());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileNameKey)
                    .contentMD5(md5Base64)
                    .metadata(metadata)
                    .build();

            s3client.putObject(putObjectRequest, RequestBody.fromBytes(contentBytes));

            RegisterFileUploadModel resp = registerFileUploadData.saveRegisterFileUpload(RegisterFileUploadModel.builder()
                    .estado("A")
                    .bucket(bucket)
                    .nombreGenerado(fileNameKey)
                    .nombreOriginal(nameFile)
                    .codCompany(companyModel.getId())
                    .fechaUpload(new Timestamp(System.currentTimeMillis()))
                    .build());

            watch.stop();
            LogHelper.infoLog(LogMessages.currentMethod(),
                    "El archivo se ha subido exitosamente en " + watch.getTime() + "ms");

            return resp;

        } catch (Exception ex) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Ocurrio un error al subir archivo", ex);
            throw new ServiceException("Ocurrió un error al subir el archivo: " + ex.getMessage());
        }
    }

    @Override
    public ByteArrayInputStream downloadFileInvoice(Long id, String uuid, TipoArchivoEnum tipoArchivoEnum) {
        String tipo = tipoArchivoEnum.name();
        RegisterFileUploadModel registerFileUploadModelInterDto = registerFileUploadData.findByIdPaymentVoucherAndUuidTipo(id, uuid, tipo);
        return downloadFileStorageInter(registerFileUploadModelInterDto);
    }

    @Override
    public ByteArrayInputStream downloadFileStorageInter(RegisterFileUploadModel fileStorage) {
        String bucket, name;
        if (fileStorage == null ) {
            return new ByteArrayInputStream(new byte[0]);
        }
        if (fileStorage.getIsOld() == null || !fileStorage.getIsOld()) {
            bucket = fileStorage.getBucket();
            name = fileStorage.getNombreGenerado();
        } else {
            bucket = String.format("%s/archivos_old/%s", this.bucketName, fileStorage.getRucCompany());
            name = String.format("%s.%s", fileStorage.getUuid(), fileStorage.getExtension());
        }
        ByteArrayInputStream ba = null;
        try{
            ba = new ByteArrayInputStream(getFile(bucket, name).toByteArray());
        }catch (Exception e){
            LogHelper.errorLog(LogMessages.currentMethod(), "Ocurrio un error al descargar archivo", e);
            throw new ServiceException("Ocurrio un error al descargar archivo", e.getMessage());
        }
        return ba;
    }

    public ByteArrayOutputStream getFile(String bucketName, String keyName) {
        try {
            StopWatch watch = new StopWatch();
            watch.start();

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            ResponseInputStream<GetObjectResponse> s3Object = s3client.getObject(getObjectRequest);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len;
            try (InputStream is = s3Object) {
                while ((len = is.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
            }
            watch.stop();
            return baos;

        } catch (Exception ex) {
            log.info("NO SE ENCONTRO ARCHIVO EN S3, BUCKET: {} NAME: {}", bucketName, keyName);
            log.error("Exception SERVICE : {}", ex.getMessage());
            throw new ServiceException("El servicio de storage está fuera de servicio, comuníquese con el administrador.", ex);
        }
    }

}
