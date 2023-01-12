package com.cgi.bonnie;

import com.cgi.bonnie.bonnierest.model.UserRequest;
import com.cgi.bonnie.businessrules.user.User;
import com.cgi.bonnie.businessrules.user.UserCredentials;
import com.cgi.bonnie.businessrules.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIT extends BaseIT {

    private static final String PATH_USER_ROOT = "/api/user";
    private static final String PATH_USER_ID = PATH_USER_ROOT + "/{id}";
    private static final String PATH_CURRENT_USER = PATH_USER_ROOT + "/current";
    private static final String PATH_USER_CREATE = PATH_USER_ROOT + "/add";
    private static final long UNKNOWN_USER_ID = 9999L;

    @Autowired
    private UserService userService;

    @Test
    void getUser_noUserPresent_badRequest() throws Exception {
        final MvcResult result = mockMvc.perform(get(PATH_USER_ID, UNKNOWN_USER_ID)
                        .with(securityContext(getSecurityContext())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();

        assertNotNull(result);
    }

    @Test
    void getCurrentUser_authenticated_returnsUser() throws Exception {
        final MvcResult result = mockMvc.perform(get(PATH_CURRENT_USER)
                        .with(securityContext(getSecurityContext())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(result.getResponse());
        final User user = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        assertEquals(userData, user);
    }

    @Test
    void createUser_validInputData_userCreated() throws Exception {
        final UserRequest userRequest = new UserRequest(TEST_USER_NAME, TEST_USER_PASSWORD, TEST_USER_ROLE.name(), TEST_USER_EMAIL);
        final String body = objectMapper.writeValueAsString(userRequest);

        final MvcResult result = mockMvc.perform(post(PATH_USER_CREATE)
                        .content(body)
                        .contentType(APPLICATION_JSON)
                        .with(securityContext(getSecurityContext())))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertNotNull(result.getResponse().getContentAsString());

        final long userId = Long.parseLong(result.getResponse().getContentAsString());
        final User user = userService.loadUser(userId);
        final UserCredentials userCredentials = userCredentialStorage.findByEmail(TEST_USER_EMAIL);

        assertEquals(userRequest.getName(), user.getName());
        assertEquals(userRequest.getEmail(), user.getEmail());
        assertEquals(userRequest.getRole(), user.getRole().name());
        assertEquals(userRequest.getName(), userCredentials.getName());
        assertEquals(userRequest.getEmail(), userCredentials.getEmail());
        assertEquals(userRequest.getPassword(), userCredentials.getPassword());
    }
}
