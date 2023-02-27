package com.cgi.bonnie;

import com.cgi.bonnie.businessrules.user.User;
import com.cgi.bonnie.businessrules.user.UserCredentials;
import com.cgi.bonnie.businessrules.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static com.cgi.bonnie.businessrules.Role.ASSEMBLER;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserRegisterControllerIT extends BaseIT {

    private static final String PATH_USER_REGISTER = "/user/register";

    @Autowired
    private UserService userService;

    @Test
    void createUser_validInputData_userCreated() throws Exception {
        final MvcResult result = mockMvc.perform(post(PATH_USER_REGISTER)
                        .param("name", TEST_USER_NAME)
                        .param("email", TEST_USER_EMAIL)
                        .param("password", TEST_USER_PASSWORD)
                        .param("role", EMPTY)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .with(securityContext(getSecurityContext()))) // Currently the path is secured
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertNotNull(result.getResponse().getContentAsString());

        final long userId = Long.parseLong(result.getResponse().getContentAsString());
        final User user = userService.loadUser(userId);
        final UserCredentials userCredentials = userCredentialStorage.findByEmail(TEST_USER_EMAIL);

        assertEquals(TEST_USER_NAME, user.getName());
        assertEquals(TEST_USER_EMAIL, user.getEmail());
        assertEquals(ASSEMBLER, user.getRole()); // Currently, every registration results in ASSEMBLER role
        assertEquals(TEST_USER_NAME, userCredentials.getName());
        assertEquals(TEST_USER_EMAIL, userCredentials.getEmail());
        assertEquals(TEST_USER_PASSWORD, userCredentials.getPassword());
    }
}
