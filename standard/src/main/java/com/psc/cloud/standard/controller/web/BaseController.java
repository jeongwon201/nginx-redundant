package com.psc.cloud.standard.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BaseController {

    @GetMapping
    public String getIndex(String data, ModelMap modelMap) {
        modelMap.addAttribute("data", "GET index [" + data + "]");
        return "index";
    }

    @PostMapping
    public String postIndex(String data, ModelMap modelMap) {
        modelMap.addAttribute("data", "POST" + data);
        return "index";
    }
}
