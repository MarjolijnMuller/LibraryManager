package org.example.librarymanager.services;

import org.junit.jupiter.api.Test;
import org.example.librarymanager.dtos.LoanDto;
import org.example.librarymanager.dtos.LoanInputDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class LoanControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldCreateNewLoanCorrectly() throws Exception {
        LoanInputDto inputDto = new LoanInputDto();
        inputDto.bookCopyId = 1L;
        inputDto.userId = 1L;

        inputDto.loanDate = LocalDate.now();
        inputDto.returnDate = LocalDate.now().plusDays(14);

        String requestJson = objectMapper.writeValueAsString(inputDto);

        MvcResult result = this.mockMvc
                .perform(MockMvcRequestBuilders.post("/loans")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String createdLoanJson = result.getResponse().getContentAsString();
        LoanDto createdLoan = objectMapper.readValue(createdLoanJson, LoanDto.class);

        assertThat(result.getResponse().getHeader("Location"), matchesPattern("^.*/loans/" + createdLoan.loanId));
    }

    @Test
    void shouldReturnBookCorrectly() throws Exception {
        Long loanIdToReturn = 2L;

        this.mockMvc
                .perform(MockMvcRequestBuilders.patch("/loans/{loanId}/return", loanIdToReturn))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanId").value(loanIdToReturn))
                .andExpect(jsonPath("$.isReturned").value(true));
    }
}