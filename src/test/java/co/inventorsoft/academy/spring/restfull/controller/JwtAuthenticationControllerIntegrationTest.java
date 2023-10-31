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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;

import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

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
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtAuthenticationControllerIntegrationTest(WebApplicationContext webApplicationContext, MockMvc mockMvc,
                                                      ObjectMapper objectMapper, UserService userService, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.webApplicationContext = webApplicationContext;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    private static String adminToken;
    private static String userToken;

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
    void givenWac_whenServletContext_thenItProvidesJwtAuthenticationController(){
        ServletContext servletContext = webApplicationContext.getServletContext();

        Assertions.assertNotNull(servletContext);
        Assertions.assertTrue(servletContext instanceof MockServletContext);
        Assertions.assertNotNull(webApplicationContext.getBean("jwtAuthenticationController"));
    }

    @ParameterizedTest
    @MethodSource("signInData")
    void givenSignInURI_whenMockMVC_thenVerifyResponse(String username, String password, HttpStatus expectedStatus,
                                                       boolean isTokenExists, String expectedMessage) throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername(username);
        request.setPassword(password);

        ResultActions resultActions = mockMvc.perform(post("/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().is(expectedStatus.value()));

        if (isTokenExists) {
            resultActions.andExpect(jsonPath("$.jwtToken").exists());
        } else {
            resultActions.andExpect(jsonPath("$.jwtToken").doesNotExist());
        }

        resultActions.andExpect(jsonPath("$.message").value(expectedMessage));
        MvcResult mvcResult = resultActions.andReturn();

        assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());
    }

    static Stream<Arguments> signInData() {
        return Stream.of(
                Arguments.of("test_admin", "Test", HttpStatus.OK, true, "You were authenticated successfully!"),
                Arguments.of("test_admin", "BadPassword", HttpStatus.UNAUTHORIZED, false, "Invalid credentials"),
                Arguments.of("test_user", "Test", HttpStatus.OK, true, "You were authenticated successfully!"),
                Arguments.of("test_user", "BadPassword", HttpStatus.UNAUTHORIZED, false, "Invalid credentials"),
                Arguments.of(null, null, HttpStatus.UNAUTHORIZED, false, "Invalid credentials")
        );
    }

    @ParameterizedTest
    @MethodSource("randomUserDtoProvider")
    void givenSignUpURI_withValidUserData_whenMockMvc_thenVerifyResponse(String userName, String firstName,
                       String lastName, String email, String phone, String password, HttpStatus expectedStatus, String expectedMessage) throws Exception {
        UserDto userDto = new UserDto();
        userDto.setUsername(userName);
        userDto.setFirstname(firstName);
        userDto.setLastname(lastName);
        userDto.setEmail(email);
        userDto.setPhone(phone);
        userDto.setPassword(password);

        ResultActions resultActions = mockMvc.perform(post("/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().is(expectedStatus.value()));

        resultActions.andExpect(content().string(expectedMessage));
        MvcResult mvcResult = resultActions.andReturn();
        assertEquals("application/json;charset=UTF-8", mvcResult.getResponse().getContentType());

        //clean-up DB
        Optional<User> user = userService.findByUsername(userName);
        user.ifPresent(value -> userService.delete(value));
    }

    static Stream<Arguments> randomUserDtoProvider() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000);

        return Stream.of(
                Arguments.of("test_user" + randomNumber, "Test", "Test", "test@gmail.com",
                        "+0000000000", "TestPassword", HttpStatus.OK, "User " + "test_user" + randomNumber + " was registered successfully!"),
                Arguments.of("", "", "", "", "", "", HttpStatus.BAD_REQUEST, ""),
                Arguments.of(null, null, null, null, null, null, HttpStatus.BAD_REQUEST, "")
        );
    }

    @ParameterizedTest
    @MethodSource("tokenProvider")
    void givenGetAllUsersURI_whenMockMVC_thenVerifyResponse(String username, String password, HttpStatus expectedStatus) throws Exception {
        String token = (username != null && password != null) ? getUserToken(username, password) : null;

        mockMvc.perform(get("/all-users")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().is(expectedStatus.value()));
    }

    static Stream<Arguments> tokenProvider() {
        return Stream.of(
                Arguments.of("test_admin", "Test", HttpStatus.OK),
                Arguments.of("test_user", "Test", HttpStatus.FORBIDDEN),
                Arguments.of(null, null, HttpStatus.UNAUTHORIZED)
        );
    }

}
