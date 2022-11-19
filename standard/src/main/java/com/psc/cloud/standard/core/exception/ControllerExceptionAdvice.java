package com.psc.cloud.standard.core.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice("com.psc.cloud.standard.controller.web")
public class ControllerExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public String exception(ModelMap modelMap, Exception e) {
        modelMap.addAttribute("message", e.getMessage());
        return "error";
    }
}
