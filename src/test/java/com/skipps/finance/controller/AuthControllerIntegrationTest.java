package com.skipps.finance.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;

import com.skipps.finance.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase()
    {
        userRepository.deleteAll();
    }

    @Test
    void registerUserReturnsJwtAndUserDTO() throws Exception
    {
        String requestBody = """
            {
                "username": "skipps",
                "email": "skipps@example.com",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.jwt", not(blankOrNullString())))
            .andExpect(jsonPath("$.response.username").value("skipps"))
            .andExpect(jsonPath("$.response.email").value("skipps@example.com"))
            .andExpect(jsonPath("$.response.passwordHash").doesNotExist());
    }

    @Test
    void registerWithExistingUsernameReturnsConflict() throws Exception
    {
        String requestBody = """
            {
                "username": "skipps",
                "email": "skipps@example.com",
                "password": "password"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());

        requestBody = """
            {
                "username": "skipps",
                "email": "other@example.com",
                "password": "password"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Username is already taken"));
    }


    @Test
    void registerWithExistingEmailReturnsConflict() throws Exception
    {
        String requestBody = """
            {
                "username": "skipps",
                "email": "skipps@example.com",
                "password": "password"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());

        requestBody = """
            {
                "username": "other",
                "email": "skipps@example.com",
                "password": "password"
            }
        """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Email is already taken"));
    }

    @Test
    void loginUserReturnsJwtAndUserDTO() throws Exception
    {
        String requestBody = """
            {
                "username": "skipps",
                "email": "skipps@example.com",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());


        requestBody = """
            {
                "username": "skipps",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jwt", not(blankOrNullString())))
            .andExpect(jsonPath("$.response.username").value("skipps"))
            .andExpect(jsonPath("$.response.email").value("skipps@example.com"));

    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception
    {
        String requestBody = """
            {
                "username": "skipps",
                "email": "skipps@example.com",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());


        requestBody = """
            {
                "username": "skipps",
                "password": "pass"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Wrong username or password"));
    }

    @Test
    void loginWithWrongUsernameReturnsUnauthorized() throws Exception
    {
        String requestBody = """
            {
                "username": "skipps",
                "email": "skipps@example.com",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated());


        requestBody = """
            {
                "username": "skipps1",
                "password": "password"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Wrong username or password"));
    }
}
