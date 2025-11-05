package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.PaymentVoucherFileModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "paymentVoucherFile")
public interface PaymentVoucherFileData {

    @PostMapping("/api/payment-voucher-file")
    public int save(@RequestBody PaymentVoucherFileModel paymentVoucherFileModel);

}
