package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(final ValidationException e) {
        log.info("400 {}",e.getMessage(), e);
        return new ApiError(Collections.emptyList(),"Ошибка валидации данных",
                e.getMessage(),HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        log.info("404 {}",e.getMessage(), e);
        return new ApiError(Collections.emptyList(),"Объект не найден",
                e.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleNotFound(final ClientErrorException e) {
        log.info("409 {}",e.getMessage(), e);
        return new ApiError(Collections.emptyList(),"Конфликт при обработке запроса",
                e.getMessage(),HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(final RuntimeException e) {
        log.info("500 {}",e.getMessage(), e);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return new ApiError(Collections.singletonList(sw.toString()),"Error .... ",
                e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
