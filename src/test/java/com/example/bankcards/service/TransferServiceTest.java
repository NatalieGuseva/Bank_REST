package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ApplicationException;
import com.example.bankcards.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceTest {
    @Mock
    private CardRepository cardRepository;
    @InjectMocks
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void transfer_success() {
        User user = new User();
        user.setUsername("user1");

        Card from = new Card();
        from.setId(1L);
        from.setUser(user);
        from.setStatus(Card.Status.ACTIVE);
        from.setBalance(BigDecimal.valueOf(100));

        Card to = new Card();
        to.setId(2L);
        to.setUser(user);
        to.setStatus(Card.Status.ACTIVE);
        to.setBalance(BigDecimal.valueOf(50));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertDoesNotThrow(() -> transferService.transfer(1L, 2L, BigDecimal.valueOf(10), "user1"));
        assertEquals(BigDecimal.valueOf(90), from.getBalance());
        assertEquals(BigDecimal.valueOf(60), to.getBalance());
    }

    @Test
    void transfer_insufficientFunds() {
        User user = new User();
        user.setUsername("user1");

        Card from = new Card();
        from.setId(1L);
        from.setUser(user);
        from.setStatus(Card.Status.ACTIVE);
        from.setBalance(BigDecimal.valueOf(5));

        Card to = new Card();
        to.setId(2L);
        to.setUser(user);
        to.setStatus(Card.Status.ACTIVE);
        to.setBalance(BigDecimal.valueOf(50));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(from));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(to));

        assertThrows(ApplicationException.class,
                () -> transferService.transfer(1L, 2L, BigDecimal.valueOf(10), "user1"));
    }
}