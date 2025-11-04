package com.certicom.certifact_facturas_service_ng.feign;

import com.certicom.certifact_facturas_service_ng.dto.UserDto;

public interface UserData {

    UserDto findUserById(Long id);
    public UserDto findUserByUsername(String username);
    public String getUsernameById(Long id);

}
