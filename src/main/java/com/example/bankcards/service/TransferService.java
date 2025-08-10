package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.ApplicationException;
import com.example.bankcards.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferService {
    @Autowired
    private CardRepository cardRepository;

    @Transactional
    public void transfer(Long fromCardId, Long toCardId, BigDecimal amount, String username) {
        // Проверка суммы
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationException("Amount must be positive");
        }

        // Получение карт
        Card from = cardRepository.findById(fromCardId)
                .orElseThrow(() -> new ApplicationException("Source card not found"));
        Card to = cardRepository.findById(toCardId)
                .orElseThrow(() -> new ApplicationException("Target card not found"));

        // Проверка владельца (используем getUser().getUsername())
        if (!from.getUser().getUsername().equals(username) || !to.getUser().getUsername().equals(username)) {
            throw new ApplicationException("Access denied");
        }

        // Проверка статуса карт
        if (from.getStatus() != Card.Status.ACTIVE || to.getStatus() != Card.Status.ACTIVE) {
            throw new ApplicationException("Both cards must be active");
        }

        // Проверка баланса
        if (from.getBalance().compareTo(amount) < 0) {
            throw new ApplicationException("Insufficient funds");
        }

        // Выполнение перевода
        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        // Сохранение изменений
        cardRepository.save(from);
        cardRepository.save(to);
    }
}
