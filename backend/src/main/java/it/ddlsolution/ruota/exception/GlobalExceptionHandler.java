package it.ddlsolution.ruota.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ExceptionMapperService exceptionMapperService;


    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseException> exceptionHandler(Exception ex) {
        BaseResponseException baseResponse = exceptionMapperService.mapToBaseResponse(ex);
        log.error("Errore: " + baseResponse.getId(), ex);
        int stato = exceptionMapperService.extractHttpStatus(ex);
        return ResponseEntity.status(stato).body(baseResponse);
    }

}
