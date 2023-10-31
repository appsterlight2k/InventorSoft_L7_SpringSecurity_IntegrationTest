package co.inventorsoft.academy.spring.restfull.controller;

import co.inventorsoft.academy.spring.restfull.dto.UserDto;
import co.inventorsoft.academy.spring.restfull.model.User;
import co.inventorsoft.academy.spring.restfull.model.jwt.JwtRequest;
import co.inventorsoft.academy.spring.restfull.model.jwt.JwtResponse;
import co.inventorsoft.academy.spring.restfull.service.UserService;
import co.inventorsoft.academy.spring.restfull.util.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class JwtAuthenticationControllerIntegrationTest {

    private WebApplicationContext webApplicationContext;

    private AuthenticationManager authenticationManager;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private JwtTokenUtil jwtTokenUtil;
    private UserService userService;

    @Autowired
    public JwtAuthenticationControllerIntegrationTest(WebApplicationContext webApplicationContext, AuthenticationManager authenticationManager, MockMvc mockMvc, ObjectMapper objectMapper, JwtTokenUtil jwtTokenUtil, UserService userService) {
        this.webApplicationContext = webApplicationContext;
        this.authenticationManager = authenticationManager;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    private static String adminToken;
    private static String userToken;

    @BeforeEach
    public void givenValidAdminAndUserCredentials_whenAuthenticateUsers_thenSetTokens() throws Exception {
        //get token for admin
        JwtRequest adminRequest = new JwtRequest();
        adminRequest.setUsername("test_admin");
        adminRequest.setPassword("Test");

        MvcResult adminResult = mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String jwtAdminResponse = adminResult.getResponse().getContentAsString();
        JwtResponse adminJwtResponse = objectMapper.readValue(jwtAdminResponse, JwtResponse.class);
        adminToken = adminJwtResponse.getJwtToken();

        //get token for user
        JwtRequest userRequest = new JwtRequest();
        userRequest.setUsername("test_user");
        userRequest.setPassword("Test");

        MvcResult userResult = mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String jwtUserResponse = userResult.getResponse().getContentAsString();
        JwtResponse userJwtResponse = objectMapper.readValue(jwtUserResponse, JwtResponse.class);
        userToken = userJwtResponse.getJwtToken();
    }

    @Test
    void givenWac_whenServletContext_thenItProvidesJwtAuthenticationController(){
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertNotNull(servletContext);
        Assertions.assertTrue(servletContext instanceof MockServletContext);
        Assertions.assertNotNull(webApplicationContext.getBean("jwtAuthenticationController"));

    }

    @Test
    void givenSignInURI_whenMockMVC_withValidAdminCredentials_thenVerifyResponse() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("test_admin");
        request.setPassword("Test");

        MvcResult mvcResult = mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").exists())
                .andExpect(jsonPath("$.message").value("You were authenticated successfully!"))
                .andReturn();

        assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
    }

    @Test
    void givenSignInURI_whenMockMVC_withInvalidAdminCredentials_thenVerifyResponse() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("test_admin");
        request.setPassword("BadPassword");

        MvcResult mvcResult = mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.jwtToken").doesNotExist())
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andReturn();

        assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
    }

    @Test
    void givenSignInURI_whenMockMVC_withValidUserCredentials_thenVerifyResponse() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("test_user");
        request.setPassword("Test");

        MvcResult mvcResult = mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").exists())
                .andExpect(jsonPath("$.message").value("You were authenticated successfully!"))
                .andReturn();

        assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
    }

    @Test
    void givenSignInURI_whenMockMVC_withInvalidUserCredentials_thenVerifyResponse() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("test_user");
        request.setPassword("BadPassword");

        MvcResult mvcResult = mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.jwtToken").doesNotExist())
                .andExpect(jsonPath("$.message").value("Invalid credentials"))
                .andReturn();

        assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
    }

    @Test
    void givenSignUpURI_whenMockMvc_thenVerifyResponse() throws Exception {
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        String userName = "test_user" + randomNumber;

        UserDto userDto = new UserDto();
        userDto.setUsername(userName);
        userDto.setFirstname("Test");
        userDto.setLastname("Test");
        userDto.setEmail("test@gmail.com");
        userDto.setPhone("+0000000000");
        userDto.setPassword("TestPassword");

        MvcResult mvcResult = mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("User " + userName + " was registered successfully!"))
                .andReturn();

        assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());

        //clean-up DB
        Optional<User> user = userService.findByUsername(userName);
        userService.delete(user.get());
    }

    @Test
    void givenGetAllUsersURI_whenMockMVC_withValidAdminToken_thenVerifyResponse() throws Exception {

        mockMvc.perform(get("/all-users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void givenGetAllUsersURI_whenMockMVC_withValidUserToken_thenVerifyResponse() throws Exception {

        mockMvc.perform(get("/all-users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }



}
