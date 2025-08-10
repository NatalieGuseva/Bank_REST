package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardViewDto;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @MockBean
    private com.example.bankcards.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private com.example.bankcards.security.JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private CardViewDto cardViewDto;

    @BeforeEach
    void setUp() {
        cardViewDto = new CardViewDto();
        cardViewDto.setId(1L);
        cardViewDto.setMaskedCardNumber("**** **** **** 1234");
        cardViewDto.setOwner("user1");
        cardViewDto.setExpiryDate(LocalDate.now().plusYears(2));
        cardViewDto.setStatus("ACTIVE");
        cardViewDto.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    @WithMockUser(username = "user1")
    void createCard() throws Exception {
        CardCreateDto createDto = new CardCreateDto();
        createDto.setCardNumber("1234567890123456");
        createDto.setOwner("user1");
        createDto.setExpiryDate(LocalDate.now().plusYears(2));
        createDto.setBalance(BigDecimal.valueOf(1000));

        Mockito.when(cardService.createCard(any(CardCreateDto.class), eq("user1"))).thenReturn(cardViewDto);

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.maskedCardNumber").value("**** **** **** 1234"));
    }

    @Test
    @WithMockUser(username = "user1")
    void getUserCards() throws Exception {
        Mockito.when(cardService.getUserCards(eq("user1"), anyInt(), anyInt(), any()))
                .thenReturn(new PageImpl<>(List.of(cardViewDto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "user1")
    void getCard() throws Exception {
        Mockito.when(cardService.getCard(eq(1L), eq("user1"))).thenReturn(cardViewDto);

        mockMvc.perform(get("/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "user1")
    void blockCard() throws Exception {
        Mockito.when(cardService.blockCard(eq(1L), eq("user1"))).thenReturn(cardViewDto);

        mockMvc.perform(post("/cards/1/block"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "user1")
    void deleteCard() throws Exception {
        Mockito.doNothing().when(cardService).deleteCard(eq(1L), eq("user1"));

        mockMvc.perform(delete("/cards/1"))
                .andExpect(status().isNoContent());
    }
}