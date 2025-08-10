package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardViewDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CardService cardService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Создаём тестовый ключ AES
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        SecretKey testKey = keyGen.generateKey();

        // ИСПРАВЛЕНО: Устанавливаем тестовый ключ в сервис
        cardService.setSecretKey(testKey);
    }

    @Test
    void createCard_success() {
        // Создаём полноценного пользователя
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");

        CardCreateDto dto = new CardCreateDto();
        dto.setCardNumber("1234567890123456");
        dto.setOwner("user1");
        dto.setExpiryDate(LocalDate.now().plusYears(2));
        dto.setBalance(BigDecimal.valueOf(1000));

        // Мокируем репозитории
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(cardRepository.findAllByUser(user)).thenReturn(Collections.emptyList());

        // Мокируем save() чтобы он устанавливал ID для сохраненной карты
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card cardToSave = invocation.getArgument(0);
            cardToSave.setId(1L); // Устанавливаем ID как делает реальная БД
            return cardToSave;
        });

        // Тест
        CardViewDto result = assertDoesNotThrow(() -> cardService.createCard(dto, "user1"));

        // Проверки
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("user1", result.getOwner());
        assertEquals(Card.Status.ACTIVE.name(), result.getStatus());
        assertEquals(BigDecimal.valueOf(1000), result.getBalance());

        verify(cardRepository).save(any(Card.class));
        verify(userRepository).findByUsername("user1");
    }
}