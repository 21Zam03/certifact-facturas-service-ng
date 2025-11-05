package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.entity.TmpVoucherSendBillEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "tmpVoucher")
public interface TmpVoucherData {

    @GetMapping("/api/tmp-voucher/{id}")
    public TmpVoucherSendBillEntity findTmpVoucherByIdPaymentVoucher(@PathVariable Long id);

    @PostMapping("/api/tmp-voucher")
    public int saveTmpVoucher(@RequestBody TmpVoucherSendBillEntity tmpVoucherSendBillEntity);

    @PutMapping("/api/tmp-voucher/status")
    public int updateStatusVoucherTmp(@RequestParam Long identificador, @RequestParam String estado);

    @DeleteMapping("/api/tmp-voucher/{id}")
    public int deleteTmpVoucherById(@PathVariable("id") Long tmpVoucherId);


}
