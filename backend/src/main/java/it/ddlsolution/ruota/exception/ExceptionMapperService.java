package it.ddlsolution.ruota.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.ErrorResponse;

@Service
@Slf4j
public class ExceptionMapperService {

    /**
     * Mappa una generica Exception in un oggetto BaseResponseException per condividere la logica tra
     * GlobalExceptionHandler e LogDispositivaAspect
     */
    public BaseResponseException mapToBaseResponse(Exception ex) {
        BaseResponseException baseResponse = new BaseResponseException();
        baseResponse.setId(ex.hashCode());
        if (ex instanceof ManagedException) {
            baseResponse.setErrorCode(((ManagedException) ex).getInternalCode().name());
        } else {
            baseResponse.setErrorCode("ERR00");
        }
        baseResponse.setMessage(ObjectUtils.isEmpty(ex.getMessage()) ? ex.toString():ex.getMessage());
        baseResponse.setNameClassException(ex.getClass().getName());
        log.debug("Mappata eccezione {} con id {}", ex.getClass().getName(), baseResponse.getId());
        return baseResponse;
    }

    /**
     * Estrae lo status HTTP da un'eccezione
     */
    public int extractHttpStatus(Exception ex) {
        if (ex instanceof ErrorResponse) {
            return ((ErrorResponse) ex).getStatusCode().value();
        } else if (ex instanceof InsufficientAuthenticationException) {
            return HttpStatus.UNAUTHORIZED.value(); // 401
        } else if (ex instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN.value(); // 403
        } else if (ex instanceof ManagedException) {
            return 499;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }
}

