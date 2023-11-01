package co.inventorsoft.academy.spring.restfull.controller;

import co.inventorsoft.academy.spring.restfull.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
class CorsControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    private static String adminToken;
    private static String userToken;
    private static String invalidUserToken = "InvalidUserToken123345234abcdefghijklmnopqrstuvwxyz";


    @BeforeEach
    public void givenValidAdminAndUserCredentials_whenAuthenticateUsers_thenSetTokens() throws Exception {
        adminToken = getUserToken("test_admin", "Test");
        userToken = getUserToken("test_user", "Test");
    }

    private String getUserToken(String username, String password) throws Exception {
        Authentication authentication =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));;
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return jwtTokenUtil.generateToken(userDetails);
    }


    @Test
    void givenLocalhostURIFromAllowedList_withValidToken_whenMockMvc_thenVerifyResponse() throws Exception {
        mockMvc.perform(get("/endpoint-for-localhost")
                        .header(HttpHeaders.ORIGIN, "http://localhost:8080"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:8080"))
                .andExpect(content().string("Data for localhost"));
    }

    @Test
    void givenSomeURIFromAllowedList_withValidToken_whenMockMvc_thenVerifyResponse() throws Exception {
        mockMvc.perform(get("/endpoint-for-any")
                        .header(HttpHeaders.ORIGIN, "http://somedomain1.com")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://somedomain1.com"))
                .andExpect(content().string("Data for any domain"));
    }

    @Test
    void givenSomeURINotFromAllowedList_withValidToken_whenMockMvc_thenVerifyResponse() throws Exception {
        mockMvc.perform(get("/endpoint-for-any")
                        .header(HttpHeaders.ORIGIN, "http://domain-not-from-allowed-list.com")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenSomeURINotFromAllowedList_withInvalidToken_whenMockMvc_thenVerifyResponse() throws Exception {
        mockMvc.perform(get("/endpoint-for-any")
                        .header(HttpHeaders.ORIGIN, "http://domain-not-from-allowed-list.com")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidUserToken))
                .andExpect(status().isForbidden());
    }

}

