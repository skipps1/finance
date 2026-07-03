package com.skipps.finance.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.skipps.finance.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerIntegrationTest
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private String jwt;

	@BeforeEach
	void cleanDatabaseAndRegisterUser() throws Exception
	{
        userRepository.deleteAll();
        jwt = registerAndGetJwt("skipps", "skipps@example.com");
	}

	@Test
	void updateUsernameReturnsJwt() throws Exception
	{
	    String requestBody = """
			{
			    "username": "skipps1"
			}
			""";

		mockMvc.perform(put("/api/user/username")
		        .header("Authorization", "Bearer " + jwt)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(requestBody))
		    .andExpect(status().isOk())
			.andExpect(content().string(not(blankOrNullString())));
	}

	@Test
	void updateUsernameToUsedOneReturnsConflict() throws Exception
	{
        registerAndGetJwt("skipps1", "example@gmail.com");

        String requestBody = """
            {
                "username": "skipps1"
            }
            """;

        mockMvc.perform(put("/api/user/username")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Username is already taken"));

	}

	@Test
	void updateEmailReturnsUserDTO() throws Exception
	{
	    String requestBody = """
			{
			    "email": "skipps1@example.com"
			}
			""";

		mockMvc.perform(put("/api/user/email")
		        .header("Authorization", "Bearer " + jwt)
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
		    .andExpect(status().isOk())
			.andExpect(jsonPath("$.username").value("skipps"))
			.andExpect(jsonPath("$.email").value("skipps1@example.com"));

	}

	@Test
	void updateEmailToUsedOneReturnsConflict() throws Exception
	{
	    String requestBody = """
			{
			    "email": "example@gmail.com"
			}
			""";

		registerAndGetJwt("skiiiips", "example@gmail.com");

		mockMvc.perform(put("/api/user/email")
		        .header("Authorization", "Bearer " + jwt)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(requestBody))
		    .andExpect(status().isConflict())
			.andExpect(jsonPath("$.message").value("User with such an email already exists"));
	}

	@Test
	void updatePasswordReturnsNothing() throws Exception
	{
	    String requestBody = """
			{
			    "currentPassword": "password",
				"newPassword": "password123"
			}
			""";

		mockMvc.perform(put("/api/user/password")
		        .header("Authorization", "Bearer " + jwt)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(requestBody))
		    .andExpect(status().isNoContent())
			.andExpect(content().string(blankOrNullString()));
	}

	private String registerAndGetJwt(String username, String email) throws Exception
	{
	    String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "%s",
                        "email": "%s",
                        "password": "password"
                    }
                    """.formatted(username, email)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return objectMapper.readTree(response).get("jwt").asString();
	}
}
