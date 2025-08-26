package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.dto.model.UserInterDto;
import com.certicom.certifact_facturas_service_ng.entity.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "user")
public interface UserFeign {

    @GetMapping("/api/invoice-sp/user/{idUsuario}")
    UserInterDto obtenerUsuario(@PathVariable Long idUsuario);

    @GetMapping("/api/invoice-sp/user/{username}")
    public UserEntity findByUserByUsername(@PathVariable String username);


}
