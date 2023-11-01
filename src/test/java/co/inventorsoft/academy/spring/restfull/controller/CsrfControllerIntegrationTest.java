package co.inventorsoft.academy.spring.restfull.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CsrfControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenCsrfCheckEndpoint_withValidCsrfToken_whenMockMvc_thenVerifyResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/csrf-check-endpoint")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("CSRF token is valid!"));
    }

    @Test
    void givenCsrfCheckEndpoint_withInvalidCsrfToken_whenMockMvc_thenVerifyResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/csrf-check-endpoint")
                        .with(csrf().useInvalidToken()))
                .andExpect(status().isForbidden());
    }

}