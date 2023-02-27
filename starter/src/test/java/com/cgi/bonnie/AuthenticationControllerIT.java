package com.cgi.bonnie;

import com.cgi.bonnie.authentication.controller.LoginData;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerIT extends BaseIT {

    private static final String PATH_LOGIN = "/login";

    @Test
    void login_invalidCredentials_fails() throws Exception {
        final String loginBody = objectMapper.writeValueAsString(new LoginData());
        final MvcResult loginResult = mockMvc.perform(post(PATH_LOGIN)
                        .contentType(APPLICATION_JSON)
                        .content(loginBody))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        assertNotNull(loginResult);
    }

    @Test
    void login_validCredentials_success() throws Exception {
        final String loginBody = objectMapper.writeValueAsString(getLoginData());
        final MvcResult loginResult = mockMvc.perform(post(PATH_LOGIN)
                        .contentType(APPLICATION_JSON)
                        .content(loginBody))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertNotNull(loginResult);
    }
}
