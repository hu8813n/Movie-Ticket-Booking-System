package com.movie.ticket.booking.system.commons.handler;


import com.movie.ticket.booking.system.commons.constants.LoggerConstants;
import com.movie.ticket.booking.system.commons.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ErrorDTO methodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {

        log.info(LoggerConstants.ENTERED_CONTROLLER_HANDLER_MESSAGE.getValue(), "methodArgumentNotValidException"
                , this.getClass(), methodArgumentNotValidException.getAllErrors());

        return ErrorDTO.builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errorMessage(methodArgumentNotValidException.getAllErrors().stream()
                        //.map(objectError -> objectError.getDefaultMessage())
                        //can use
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList()))
                .build();
    }
}
