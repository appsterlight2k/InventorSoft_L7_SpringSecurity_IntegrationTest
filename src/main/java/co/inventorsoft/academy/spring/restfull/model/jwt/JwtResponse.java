package co.inventorsoft.academy.spring.restfull.model.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class JwtResponse implements Serializable {
    private final String jwtToken;
    private String message;

}
