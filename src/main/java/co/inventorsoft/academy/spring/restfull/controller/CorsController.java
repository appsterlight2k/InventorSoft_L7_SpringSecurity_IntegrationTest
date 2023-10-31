package co.inventorsoft.academy.spring.restfull.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CorsController {

    @GetMapping("/endpoint-for-localhost")
    @CrossOrigin(origins = "http://localhost:8080")
    public ResponseEntity<String> localhostEndpoint() {
        return ResponseEntity.ok("Data for localhost");
    }

    @GetMapping("/endpoint-for-any")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> anyEndpoint() {
        return ResponseEntity.ok("Data for any domain");
    }
}
