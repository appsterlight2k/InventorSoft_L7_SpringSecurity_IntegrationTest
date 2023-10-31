package co.inventorsoft.academy.spring.restfull.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class UserDto {
    private Long id;
    @NotBlank
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    @Size(max = 300)
    private String password;

}
