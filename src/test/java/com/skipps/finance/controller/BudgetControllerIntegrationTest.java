package com.skipps.finance.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.skipps.finance.model.CategoryModel;
import com.skipps.finance.repository.BudgetRepository;
import com.skipps.finance.repository.CategoryRepository;
import com.skipps.finance.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BudgetControllerIntegrationTest
{
    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private String jwt;

    @BeforeEach
    void cleanDatabaseAndRegisterUser() throws Exception
    {
        budgetRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        jwt = registerAndGetJwt();
    }

    @Test
    void createBudgetReturnsBudgetDTO() throws Exception
    {
        CategoryModel category = categoryRepository.save(new CategoryModel("Medicine"));
        String requestBody = """
            {
                "categoryId": %d,
                "amountLimit": %f
            }
        """.formatted(category.getId(), 2.4);

        mockMvc.perform(post("/api/budget")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.category").value("Medicine"))
            .andExpect(jsonPath("$.amountLimit").value(2.4));
    }

    @Test
    void getBudgetsReturnsBudgetDTO() throws Exception
    {
        CategoryModel category = categoryRepository.save(new CategoryModel("Food"));
        CategoryModel category2 = categoryRepository.save(new CategoryModel("Royal Pass"));
        createBudgetForTest(category.getId(), new BigDecimal(2.4));
        createBudgetForTest(category2.getId(), new BigDecimal(4.2));

        mockMvc.perform(get("/api/budget")
                .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].category").value("Food"))
            .andExpect(jsonPath("$[0].amountLimit").value(2.4))
            .andExpect(jsonPath("$[1].category").value("Royal Pass"))
            .andExpect(jsonPath("$[1].amountLimit").value(4.2));

    }

    @Test
    void updateBudgetReturnsBudgetDTO() throws Exception
    {
        CategoryModel category = categoryRepository.save(new CategoryModel("Food"));
        CategoryModel category2 = categoryRepository.save(new CategoryModel("Not Food"));
        long budgetId = createBudgetForTest(category.getId(), new BigDecimal(9.2));

        String requestBody = """
            {
                "categoryId": %d,
                "amountLimit": %f
            }
            """.formatted(category2.getId(), 4.3);

        mockMvc.perform(put("/api/budget/{budgetId}", budgetId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.category").value("Not Food"))
            .andExpect(jsonPath("$.amountLimit").value(4.3));
    }

    @Test
    void deleteBudgetReturnsNothing() throws Exception
    {
        long budgetId = createBudgetForTest(null, new BigDecimal(2.1));

        mockMvc.perform(delete("/api/budget/{budgetId}", budgetId)
                .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isNoContent());

        assertFalse(budgetRepository.existsById(budgetId));
    }

    private Long createBudgetForTest(
            Long categoryId,
            BigDecimal amountLimit) throws Exception
    {
        String requestBody = """
            {
                "categoryId": %d,
                "amountLimit": %f
            }
        """.formatted(categoryId, amountLimit);

        String response = mockMvc.perform(post("/api/budget")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private String registerAndGetJwt() throws Exception
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

        return  objectMapper.readTree(response).get("jwt").asString();
    }
}
