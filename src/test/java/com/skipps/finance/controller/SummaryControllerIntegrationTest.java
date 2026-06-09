package com.skipps.finance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.skipps.finance.repository.BudgetRepository;
import com.skipps.finance.repository.CategoryRepository;
import com.skipps.finance.repository.TransactionRepository;
import com.skipps.finance.repository.UserRepository;
import com.skipps.finance.model.CategoryModel;
import com.skipps.finance.model.TransactionType;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SummaryControllerIntegrationTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwt;

    @BeforeEach
    void setUp() throws Exception
    {
        budgetRepository.deleteAll();
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        registerUserAndGetJwt();
    }

    @Test
    void getIncomeReturnsBigDecimal() throws Exception
    {
        createTransactionForTest(TransactionType.INCOME, new BigDecimal(4.2));
        createTransactionForTest(TransactionType.EXPENSE, new BigDecimal(3.5));
        createTransactionForTest(TransactionType.INCOME, new BigDecimal(1.0));

        mockMvc.perform(get("/api/summary/income")
                .header("Authorization", "Bearer " + jwt)
                .param("year", "2026")
                .param("month", "6"))
            .andExpect(status().isOk())
            .andExpect(content().string("5.20"));

    }

    @Test
    void getExpensesReturnsBigDecimal() throws Exception
    {
        createTransactionForTest(TransactionType.INCOME, new BigDecimal(4.2));
        createTransactionForTest(TransactionType.EXPENSE, new BigDecimal(3.5));
        createTransactionForTest(TransactionType.EXPENSE, new BigDecimal(1.0));

        mockMvc.perform(get("/api/summary/expense")
                .header("Authorization", "Bearer " + jwt)
                .param("year", "2026")
                .param("month", "6"))
            .andExpect(status().isOk())
            .andExpect(content().string("4.50"));
    }

    @Test
    void getMonthlySummaryReturnsDTO() throws Exception
    {
        createTransactionForTest(TransactionType.INCOME, new BigDecimal(6.7));
        createTransactionForTest(TransactionType.INCOME, new BigDecimal(4.2));
        createTransactionForTest(TransactionType.EXPENSE, new BigDecimal(3.5));
        createTransactionForTest(TransactionType.EXPENSE, new BigDecimal(1.0));

        mockMvc.perform(get("/api/summary/monthly")
                .header("Authorization", "Bearer " + jwt)
                .param("year", "2026")
                .param("month", "6"))
            .andExpect(status().isOk())
        .andExpect(jsonPath("$.year").value(2026))
        .andExpect(jsonPath("$.month").value(6))
        .andExpect(jsonPath("$.totalIncome").value(10.9))
        .andExpect(jsonPath("$.totalExpenses").value(4.5))
        .andExpect(jsonPath("$.balance").value(6.4));
    }

    @Test
    void getExpensesByCategoryReturnsDTO() throws Exception
    {
        CategoryModel food = categoryRepository.save(new CategoryModel("Food"));
        CategoryModel education = categoryRepository.save(new CategoryModel("Education"));

        createTransactionForTestWithCategoryx(TransactionType.EXPENSE, new BigDecimal(4.5), food.getId());
        createTransactionForTestWithCategoryx(TransactionType.EXPENSE, new BigDecimal(2.3), food.getId());
        createTransactionForTestWithCategoryx(TransactionType.EXPENSE, new BigDecimal(7.0), education.getId());

        mockMvc.perform(get("/api/summary/expenses/by-category")
                .header("Authorization", "Bearer " + jwt)
                .param("year", "2026")
                .param("month", "6"))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[*].categoryName", containsInAnyOrder("Food", "Education")))
            .andExpect(jsonPath("$[*].totalSpent", containsInAnyOrder(6.8, 7.0)));
    }

    private void createTransactionForTestWithCategoryx(
            TransactionType type,
            BigDecimal amount,
            long categoryId) throws Exception
    {
        String requestBody = """
            {
                "type": "%s",
                "amount": %f,
                "categoryId": %d,
                "description": "transaction for test"
            }
            """.formatted(type, amount, categoryId);

        mockMvc.perform(post("/api/transaction")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
    }

    private long createTransactionForTest(
            TransactionType type,
            BigDecimal amount) throws Exception
    {
        String requestBody = """
            {
                "type": "%s",
                "amount": %f,
                "categoryId": null,
                "description": "transaction for test"
            }
            """.formatted(type, amount);

        String response = mockMvc.perform(post("/api/transaction")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private void registerUserAndGetJwt() throws Exception
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

        jwt = objectMapper.readTree(response).get("jwt").asString();
    }
}
