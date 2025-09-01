package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.model.User;
import com.certicom.certifact_facturas_service_ng.entity.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "user")
public interface UserFeign {

    @GetMapping("/api/user/idUser")
    User findUserById(@RequestParam Long idUser);

    @GetMapping("/api/user/username")
    public UserEntity findUserByUsername(@RequestParam String username);

}
