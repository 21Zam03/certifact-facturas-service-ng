package com.certicom.certifact_facturas_service_ng.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String dni;
    private String typeUser;
    private Boolean enabled;
    private Boolean changePass;
    private Date lastPasswordResetDate;
    private Boolean estado;
    private Boolean hidecomprobante;
    private String passwordTemp;
    private Boolean pdfUnico;
    private Boolean viewCompra;
    private Integer oficinaId;

}
