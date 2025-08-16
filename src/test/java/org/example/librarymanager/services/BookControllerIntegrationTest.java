package org.example.librarymanager.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.librarymanager.dtos.BookCopyDto;
import org.example.librarymanager.dtos.BookCopyInputDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class BookControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldCreateNewBookAndFirstCopyCorrectly() throws Exception {
        // Arrange
        BookCopyInputDto inputDto = new BookCopyInputDto();
        inputDto.ISBN = "9781234567897";
        inputDto.title = "The New Test Book";
        inputDto.authorFirstName = "New";
        inputDto.authorLastName = "Author";
        inputDto.publisher = "New Publisher";
        inputDto.category = "FICTION";

        String requestJson = objectMapper.writeValueAsString(inputDto);

        // Act
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .contentType(APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        // Assert
        String createdBookCopyJson = result.getResponse().getContentAsString();

        BookCopyDto createdBookResponse = objectMapper.readValue(createdBookCopyJson, BookCopyDto.class);

        assertEquals("The New Test Book", createdBookResponse.bookTitle);
        assertEquals("New Author", createdBookResponse.bookAuthor);
        assertEquals(1L, createdBookResponse.followNumber);
        assertEquals("AVAILABLE", createdBookResponse.status.toString());
        assertThat(result.getResponse().getHeader("Location"), matchesPattern("^.*/books/copies/" + createdBookResponse.bookCopyId));
    }
}