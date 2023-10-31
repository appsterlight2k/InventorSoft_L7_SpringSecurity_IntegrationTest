package co.inventorsoft.academy.spring.restfull.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CsrfController {

    @GetMapping("/form-page")
    public String showForm() {
        return "formPage";
    }

    @PostMapping("/csrf-check-endpoint")
    @ResponseBody
    public ResponseEntity<String> checkCsrf() {
        return ResponseEntity.ok("CSRF token is valid!");
    }
}
