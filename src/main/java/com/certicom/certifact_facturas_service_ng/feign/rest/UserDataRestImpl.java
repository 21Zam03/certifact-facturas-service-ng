package com.certicom.certifact_facturas_service_ng.feign.rest;

import com.certicom.certifact_facturas_service_ng.dto.UserDto;
import com.certicom.certifact_facturas_service_ng.exceptions.ServiceException;
import com.certicom.certifact_facturas_service_ng.feign.UserData;
import com.certicom.certifact_facturas_service_ng.util.LogHelper;
import com.certicom.certifact_facturas_service_ng.util.LogMessages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UserDataRestImpl implements UserData {

    private final RestTemplate restTemplate;

    @Value("${external.services.factura-service-sp.base-url}")
    private String baseUrl;

    @Value("${external.services.factura-service-sp.endpoints.api-user-endpoint}")
    private String apiUserEndpoint;

    public UserDataRestImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserDto findUserById(Long idUser) {
        String url = String.format("%s/%d", getUrlEndpoint(), idUser);
        try {
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, UserDto.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LogHelper.warnLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+e.getMessage());
            return null;
        } catch (HttpServerErrorException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+ e.getMessage());
            throw new ServiceException(LogMessages.ERROR_HTTP_SERVER, e);
        } catch (ResourceAccessException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error de conexión con el servicio externo", e);
            throw new ServiceException(LogMessages.ERROR_HTTP_RED, e);
        } catch (RestClientException ex) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error inesperado al comunicarse con el servicio externo", ex);
            throw new ServiceException(LogMessages.ERROR_HTTP, ex);
        }
    }

    @Override
    public UserDto findUserByUsername(String username) {
        String url = UriComponentsBuilder
                .fromHttpUrl(getUrlEndpoint() + "/byUsername")
                .queryParam("username", username)
                .toUriString();
        try {
            ResponseEntity<UserDto> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, UserDto.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LogHelper.warnLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+e.getMessage());
            return null;
        } catch (HttpServerErrorException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+ e.getMessage());
            throw new ServiceException(LogMessages.ERROR_HTTP_SERVER, e);
        } catch (ResourceAccessException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error de conexión con el servicio externo", e);
            throw new ServiceException(LogMessages.ERROR_HTTP_RED, e);
        } catch (RestClientException ex) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error inesperado al comunicarse con el servicio externo", ex);
            throw new ServiceException(LogMessages.ERROR_HTTP, ex);
        }
    }

    @Override
    public String getUsernameById(Long id) {
        String url = String.format("%s/%d/username", getUrlEndpoint(), id);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, String.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            LogHelper.warnLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+e.getMessage());
            return null;
        } catch (HttpServerErrorException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error "+e.getStatusCode()+" al comunicarse con el servicio externo, "+ e.getMessage());
            throw new ServiceException(LogMessages.ERROR_HTTP_SERVER, e);
        } catch (ResourceAccessException e) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error de conexión con el servicio externo", e);
            throw new ServiceException(LogMessages.ERROR_HTTP_RED, e);
        } catch (RestClientException ex) {
            LogHelper.errorLog(LogMessages.currentMethod(),
                    "Error inesperado al comunicarse con el servicio externo", ex);
            throw new ServiceException(LogMessages.ERROR_HTTP, ex);
        }
    }

    private String getUrlEndpoint() {
        return this.baseUrl+this.apiUserEndpoint;
    }

}
