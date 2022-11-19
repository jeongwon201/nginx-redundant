package com.psc.cloud.standard.core.exception;

import com.psc.cloud.standard.core.dto.ShareDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestControllerAdvice("com.psc.cloud.standard.controller.api")
public class RestControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ShareDto exception(HttpServletResponse response, Exception e) {
        return new ShareDto(false, e.getMessage());
    }
}
