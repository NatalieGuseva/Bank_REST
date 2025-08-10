package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardViewDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.ApplicationException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.EncryptionUtils;
import com.example.bankcards.util.MaskingUtils;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private SecretKey aesKey; // Изменено с final для возможности установки в тестах

    public CardService(CardRepository cardRepository, UserRepository userRepository, SecretKey aesKey) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.aesKey = aesKey;
    }

    // Сеттер для aesKey для тестирования
    public void setAesKey(SecretKey aesKey) {
        this.aesKey = aesKey;
    }

    @Transactional
    public CardViewDto createCard(CardCreateDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found"));

        // Проверка уникальности номера карты (после дешифровки)
        boolean cardExists = cardRepository.findAllByUser(user).stream()
                .anyMatch(card -> {
                    try {
                        String decryptedNumber = EncryptionUtils.decrypt(card.getEncryptedCardNumber(), aesKey);
                        return decryptedNumber.equals(dto.getCardNumber());
                    } catch (Exception e) {
                        throw new ApplicationException("Decryption error during card validation");
                    }
                });

        if (cardExists) {
            throw new ApplicationException("Card with this number already exists");
        }

        Card card = new Card();
        try {
            card.setEncryptedCardNumber(EncryptionUtils.encrypt(dto.getCardNumber(), aesKey));
        } catch (Exception e) {
            throw new ApplicationException("Card number encryption failed", e);
        }

        card.setUser(user);
        card.setExpiryDate(dto.getExpiryDate());
        card.setStatus(Card.Status.ACTIVE);
        card.setBalance(dto.getBalance());

        cardRepository.save(card);
        return toViewDto(card);
    }

    public Page<CardViewDto> getUserCards(String username, int page, int size, String status) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        List<Card> userCards = cardRepository.findAllByUser(user);

        // Фильтрация по статусу если нужно
        List<Card> filteredCards = status == null
                ? userCards
                : userCards.stream()
                .filter(card -> card.getStatus().name().equalsIgnoreCase(status))
                .collect(Collectors.toList());

        // Пагинация
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredCards.size());
        List<CardViewDto> content = filteredCards.subList(start, end)
                .stream()
                .map(this::toViewDto)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, filteredCards.size());
    }

    public CardViewDto getCard(Long id, String username) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Card not found"));

        if (!card.getUser().getUsername().equals(username)) {
            throw new ApplicationException("Access denied");
        }

        return toViewDto(card);
    }

    @Transactional
    public CardViewDto blockCard(Long id, String username) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Card not found"));

        if (!card.getUser().getUsername().equals(username)) {
            throw new ApplicationException("Access denied");
        }

        if (card.isExpired()) {
            throw new ApplicationException("Cannot block expired card");
        }

        card.setStatus(Card.Status.BLOCKED);
        cardRepository.save(card);
        return toViewDto(card);
    }

    @Transactional
    public void deleteCard(long id, String username) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new ApplicationException("Card not found"));

        if (!card.getUser().getUsername().equals(username)) {
            throw new ApplicationException("Access denied");
        }

        cardRepository.delete(card);
    }

    private CardViewDto toViewDto(Card card) {
        return CardViewDto.builder()
                .id(card.getId())
                .maskedCardNumber(getMaskedCardNumber(card))
                .owner(card.getUser().getUsername())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus().name())
                .balance(card.getBalance())
                .build();
    }

    private String getMaskedCardNumber(Card card) {
        try {
            String decrypted = EncryptionUtils.decrypt(card.getEncryptedCardNumber(), aesKey);
            return MaskingUtils.maskCard(decrypted);
        } catch (Exception e) {
            throw new ApplicationException("Failed to mask card number", e);
        }
    }

    public void setSecretKey(SecretKey testKey) {
        this.aesKey = testKey;
    }
}