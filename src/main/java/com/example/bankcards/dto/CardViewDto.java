package com.example.bankcards.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardViewDto {
    private Long id;
    private String maskedCardNumber;
    private String owner;
    private LocalDate expiryDate;
    private String status;
    private BigDecimal balance;
}