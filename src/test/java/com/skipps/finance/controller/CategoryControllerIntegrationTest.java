package com.skipps.finance.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.skipps.finance.model.CategoryModel;
import com.skipps.finance.model.Role;
import com.skipps.finance.model.UserModel;
import com.skipps.finance.repository.CategoryRepository;
import com.skipps.finance.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String adminJwt;

    private String regularJwt;

    @BeforeEach
    void setUp() throws Exception
    {
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        adminJwt = registerAdminUser();
        regularJwt = registerRegularUser();
    }

    @Test
    void adminCanCreateCategory() throws Exception
    {
        mockMvc.perform(post("/api/category")
                .header("Authorization", "Bearer " + adminJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("Food"))
            .andExpect(status().isCreated());
    }

    @Test
    void adminCanUpdateCategory() throws Exception
    {
        CategoryModel category = categoryRepository.save(new CategoryModel("Clash Royale"));

        mockMvc.perform(put("/api/category/{categoryId}", category.getId())
                .header("Authorization", "Bearer " + adminJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("Fortnite Battle Pass"))
            .andExpect(status().isOk())
            .andExpect(content().string("Fortnite Battle Pass"));
    }

    @Test
    void adminCanDeleteCategory() throws Exception
    {
        CategoryModel category = categoryRepository.save(new CategoryModel("Rent"));

        mockMvc.perform(delete("/api/category/{categoryId}", category.getId())
                .header("Authorization", "Bearer " + adminJwt))
            .andExpect(status().isNoContent());

        assertFalse(categoryRepository.existsById(category.getId()));
    }

    @Test
    void regularUserCanGetCategories() throws Exception
    {
        CategoryModel category = categoryRepository.save(new CategoryModel("Groceries"));

        mockMvc.perform(get("/api/category")
                .header("Authorization", "Bearer " + regularJwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(category.getId()))
            .andExpect(jsonPath("$[0].name").value(category.getName()));

    }

    @Test
    void regularUserCantCreateCategory() throws Exception
    {
        mockMvc.perform(post("/api/category")
                .header("Authorization", "Bearer " + regularJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("Food"))
            .andExpect(status().isForbidden());
    }

    String registerAdminUser() throws Exception
    {
        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "skipps",
                        "email": "skipps@example.com",
                        "password": "password"
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        UserModel admin = userRepository.findByUsername("skipps");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        return objectMapper.readTree(response).get("jwt").asString();
    }

    String registerRegularUser() throws Exception
    {
        String response = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "username": "skipps1",
                        "email": "skipps1@example.com",
                        "password": "password"
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

            return objectMapper.readTree(response).get("jwt").asString();
    }
}
