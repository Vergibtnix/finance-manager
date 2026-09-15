package at.vergibtnix.finance_manager;

import at.vergibtnix.finance_manager.entity.AppUser;
import at.vergibtnix.finance_manager.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
    }

    @Test
    void publicAuthPagesAreAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedPageRedirectsToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/transactions"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void registrationCreatesEncodedUserAndRedirectsToLogin() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("username", "alice")
                        .param("password", "Secret123!")
                        .param("confirmPassword", "Secret123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        AppUser appUser = appUserRepository.findByUsernameIgnoreCase("alice")
                .orElseThrow(() -> new AssertionError("Benutzer wurde nicht gespeichert."));

        assertTrue(passwordEncoder.matches("Secret123!", appUser.getPassword()));
    }

    @Test
    void loginWorksWithStoredCredentials() throws Exception {
        AppUser appUser = new AppUser();
        appUser.setUsername("alice");
        appUser.setPassword(passwordEncoder.encode("Secret123!"));
        appUser.setRole("USER");
        appUserRepository.save(appUser);

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "alice")
                        .param("password", "Secret123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("alice"));
    }
}

