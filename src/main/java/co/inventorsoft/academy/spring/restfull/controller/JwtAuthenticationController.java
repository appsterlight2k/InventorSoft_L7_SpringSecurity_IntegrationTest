package co.inventorsoft.academy.spring.restfull.controller;

import co.inventorsoft.academy.spring.restfull.dto.UserDto;
import co.inventorsoft.academy.spring.restfull.model.User;
import co.inventorsoft.academy.spring.restfull.model.jwt.JwtRequest;
import co.inventorsoft.academy.spring.restfull.model.jwt.JwtResponse;
import co.inventorsoft.academy.spring.restfull.service.JwtUserDetailsService;
import co.inventorsoft.academy.spring.restfull.service.UserService;
import co.inventorsoft.academy.spring.restfull.util.JwtTokenUtil;
import co.inventorsoft.academy.spring.restfull.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static co.inventorsoft.academy.spring.restfull.controller.constants.Error.*;

@Slf4j
@RestController
@CrossOrigin
public class JwtAuthenticationController {
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;
    private JwtUserDetailsService userDetailsService;
    private UserService userService;
    private MapperUtil mapperUtil;

    public JwtAuthenticationController(AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, JwtUserDetailsService userDetailsService, UserService userService, MapperUtil mapperUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.mapperUtil = mapperUtil;
    }

    //http://localhost:8080/sign-in
    @PostMapping("/sign-in")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest request) throws Exception {
        Authentication authentication = authenticate(request.getUsername(), request.getPassword());
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String token = jwtTokenUtil.generateToken(userDetails);
        String successMessage = "You were authenticated successfully!";
        log.info(String.format("user %s was authenticated successfully!", userDetails.getUsername()));

        return ResponseEntity.ok(new JwtResponse(token, successMessage));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserDto userDto) {
        userDetailsService.save(userDto);

        return ResponseEntity.ok(String.format("User %s was registered successfully!", userDto.getUsername()));
    }

    @GetMapping("/all-users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();

        return ResponseEntity.ok(allUsers);
    }

    private Authentication authenticate(String username, String password) {
        try {
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            log.info(String.format(DISABLED_USER_MESSAGE, username));
            throw new DisabledException("ACCOUNT_DISABLED", e);
        } catch (BadCredentialsException e) {
            log.info(String.format(BAD_CREDENTIALS_MESSAGE, username));
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        } catch (UsernameNotFoundException e) {
            log.info(String.format(USERNAME_NOT_FOUND_MESSAGE, username));
            throw new UsernameNotFoundException("USERNAME_NOT_FOUND", e);
        } catch (InsufficientAuthenticationException e) {
            log.info(String.format(INSUFFICIENT_AUTH_MESSAGE, username));
            throw new InsufficientAuthenticationException("INSUFFICIENT_AUTHENTICATION", e);
        }

    }

}
