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
import com.skipps.finance.model.TransactionType;
import com.skipps.finance.repository.CategoryRepository;
import com.skipps.finance.repository.TransactionRepository;
import com.skipps.finance.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionControllerIntegrationTest
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	private String jwt;

	@BeforeEach
	void setUp() throws Exception
	{
        transactionRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        jwt = registerAndGetJwt();
	}

	String registerAndGetJwt() throws Exception
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

		return objectMapper.readTree(response).get("jwt").asString();
	}

	@Test
	void makeTransactionReturnsTransactionDTO() throws Exception
	{
        CategoryModel category = categoryRepository.save(new CategoryModel("Food"));
	    String requestBody = """
			{
			    "type": "INCOME",
				"amount": 2.5,
				"categoryId": %d,
				"description": "borrowed from my parents"
			}
			""".formatted(category.getId());

		mockMvc.perform(post("/api/transaction")
		        .header("Authorization", "Bearer " + jwt)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(requestBody))
		    .andExpect(status().isCreated())
			.andExpect(jsonPath("$.type").value("INCOME"))
			.andExpect(jsonPath("$.amount").value(2.5))
			.andExpect(jsonPath("$.category").value("Food"))
			.andExpect(jsonPath("$.description").value("borrowed from my parents"));
	}

	@Test
	void getTransactionsReturnsTransactionDTO() throws Exception
	{
        CategoryModel category = categoryRepository.save(
            new CategoryModel("Food"));
        createTransactionForTest(TransactionType.INCOME, new BigDecimal(2.4), category.getId());
        createTransactionForTest(TransactionType.EXPENSE, new BigDecimal(2.9), category.getId());

        mockMvc.perform(get("/api/transaction")
                .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("INCOME"))
            .andExpect(jsonPath("$[0].amount").value(2.4))
            .andExpect(jsonPath("$[0].category").value("Food"))
            .andExpect(jsonPath("$[0].description").value("transaction for test"))
            .andExpect(jsonPath("$[1].type").value("EXPENSE"))
            .andExpect(jsonPath("$[1].amount").value(2.9))
            .andExpect(jsonPath("$[1].category").value("Food"))
            .andExpect(jsonPath("$[1].description").value("transaction for test"));
	}

	@Test
	void deleteTransactionReturnsNothing() throws Exception
	{
        CategoryModel category = categoryRepository.save(
            new CategoryModel("Food"));
        Long transactionId = createTransactionForTest(TransactionType.EXPENSE, new BigDecimal(6.7), category.getId());

        mockMvc.perform(delete("/api/transaction/{transactionId}", transactionId)
                .header("Authorization", "Bearer " + jwt))
            .andExpect(status().isNoContent());

        assertFalse(transactionRepository.existsById(transactionId));
	}

	@Test
	void updateTransactionReturnsTransactionDTO() throws Exception
	{
        CategoryModel category = categoryRepository.save(
            new CategoryModel("CS-GO skins"));

        Long transactionId = createTransactionForTest(TransactionType.INCOME, new BigDecimal(5.2), category.getId());

        String request = """
            {
                "type": "EXPENSE",
                "amount": 2.0,
                "categoryId": %d,
                "description": "not transaction for test",
                "timestamp": "2026-06-04T14:30:00.123"
            }
            """.formatted(category.getId());

        mockMvc.perform(put("/api/transaction/{transactionId}", transactionId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(transactionId))
            .andExpect(jsonPath("$.type").value("EXPENSE"))
            .andExpect(jsonPath("$.amount").value(2.0))
            .andExpect(jsonPath("$.timestamp").value("2026-06-04T14:30:00.123"))
            .andExpect(jsonPath("$.category").value("CS-GO skins"))
            .andExpect(jsonPath("$.description").value("not transaction for test"));
	}

	Long createTransactionForTest(
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

		String response = mockMvc.perform(post("/api/transaction")
		        .header("Authorization", "Bearer " + jwt)
		        .contentType(MediaType.APPLICATION_JSON)
		        .content(requestBody))
		    .andExpect(status().isCreated())
			.andReturn()
			.getResponse()
			.getContentAsString();

		Long transactionId = objectMapper.readTree(response).get("id").asLong();
		return transactionId;
	}
}
