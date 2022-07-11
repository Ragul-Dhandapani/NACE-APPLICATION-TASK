package com.luxoft.naceapplication.exceptions;

import com.luxoft.naceapplication.dao.NaceErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutionException;

import static com.luxoft.naceapplication.constants.NaceApplicationConstants.*;


@ControllerAdvice
@ResponseBody
public class NaceExceptionResolver extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataNotFoundException.class)
    public final ResponseEntity<NaceErrorResponseDto> dataNotFound() {
        final String message = DATA_NOT_FOUND_ERR_MSG;
        NaceErrorResponseDto errorResponseDto = new NaceErrorResponseDto(FAILED , message , LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDto , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public final ResponseEntity<NaceErrorResponseDto> constraintViolation(ConstraintViolationException constraintException) {
        final String message = constraintException.getMessage();
        NaceErrorResponseDto errorResponseDto = new NaceErrorResponseDto(FAILED , message , LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDto , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class , IOException.class , ArithmeticException.class , FileNotFoundException.class ,
            NumberFormatException.class , IllegalStateException.class , ConcurrentModificationException.class ,
            ExecutionException.class , JpaSystemException.class , ExecutionException.class , InterruptedException.class})
    public final ResponseEntity<NaceErrorResponseDto> genericException(Exception exception) {
        final String message = (exception.getMessage() != null && !exception.getMessage().isEmpty()) ? exception.getMessage() : ISR_MSG;
        NaceErrorResponseDto errorResponseDto = new NaceErrorResponseDto(FAILED , message , LocalDateTime.now());
        return new ResponseEntity<>(errorResponseDto , HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
