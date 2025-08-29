package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.dto.others.UserDto;
import com.certicom.certifact_facturas_service_ng.entity.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "facturas-service-sp", url = "http://localhost:8090", contextId = "user")
public interface UserFeign {

    @GetMapping("/api/user/{idUsuario}")
    UserDto obtenerUsuario(@PathVariable Long idUsuario);

    @GetMapping("/api/user/username")
    public UserEntity findByUserByUsername(@RequestParam String username);


}
