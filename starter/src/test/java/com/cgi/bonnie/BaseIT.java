package com.cgi.bonnie;

import com.cgi.bonnie.authentication.controller.LoginData;
import com.cgi.bonnie.businessrules.Role;
import com.cgi.bonnie.businessrules.user.User;
import com.cgi.bonnie.businessrules.user.UserCredentialStorage;
import com.cgi.bonnie.businessrules.user.UserCredentials;
import com.cgi.bonnie.businessrules.user.UserStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Import(TestConfig.class)
@Transactional
@AutoConfigureMockMvc
@TestInstance(PER_CLASS)
@AutoConfigureEmbeddedDatabase(provider = ZONKY)
@EmbeddedKafka(brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@SpringBootTest(webEnvironment = RANDOM_PORT)
class BaseIT {

    static final String TEST_EMAIL = "integration.test@admin.local";
    static final String TEST_USER_NAME = "test user";
    static final String TEST_USER_EMAIL = "testuser@mail.local";
    static final String TEST_USER_PASSWORD = "testpassword";
    static final Role TEST_USER_ROLE = Role.ADMIN;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserStorage userStorage;

    @Autowired
    UserCredentialStorage userCredentialStorage;

    @Autowired
    MockMvc mockMvc;

    UserCredentials userCredentials;

    User userData;

    @BeforeAll
    void setup() {
        userCredentials = userCredentialStorage.findByEmail(TEST_EMAIL);
        userData = userStorage.findByEmail(TEST_EMAIL);
    }

    LoginData getLoginData() {
        final LoginData loginData = new LoginData();
        loginData.setUsername(userCredentials.getEmail());
        loginData.setPassword(userCredentials.getPassword());
        return loginData;
    }

    SecurityContext getSecurityContext() {
        final LoginData loginData = getLoginData();
        final SecurityContext securityContext = SecurityContextHolder.getContext();

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginData.getUsername(), loginData.getPassword());
        final Authentication authentication = authenticationManager.authenticate(authenticationToken);

        securityContext.setAuthentication(authentication);
        return securityContext;
    }
}
