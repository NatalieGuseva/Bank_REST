package com.example.bankcards.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 255) // Увеличена длина для зашифрованного значения
    private String encryptedCardNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Future
    @Column(nullable = false)
    private LocalDate expiryDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Digits(integer = 10, fraction = 2) // Контроль точности
    @Column(nullable = false)
    private BigDecimal balance;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEncryptedCardNumber() { return encryptedCardNumber; }
    public void setEncryptedCardNumber(String encryptedCardNumber) { this.encryptedCardNumber = encryptedCardNumber; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    // Метод для маскированного отображения номера карты
    public String getMaskedCardNumber() {
        if (encryptedCardNumber == null || encryptedCardNumber.length() < 4) {
            return null;
        }
        return "**** **** **** " + encryptedCardNumber.substring(encryptedCardNumber.length() - 4);
    }

    // Метод для проверки истечения срока действия
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    // Автоматическое обновление статуса при запросе
    @PreUpdate
    @PrePersist
    public void updateStatus() {
        if (isExpired() && status != Status.BLOCKED) {
            status = Status.EXPIRED;
        }
    }

    public enum Status {
        ACTIVE, BLOCKED, EXPIRED
    }
}