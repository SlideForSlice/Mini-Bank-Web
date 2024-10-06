package com.java.bank.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {
    @GetMapping("/swagger-ui/")
    public String swaggerUi() {
        return "forward:/static/swagger-ui/index.html";
    }
}
