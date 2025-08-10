package com.example.bankcards.controller;

import com.example.bankcards.service.TransferService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/transfers")
public class TransferController {
    @Autowired
    private TransferService transferService;

    public static class TransferRequest {
        @NotNull
        public Long fromCardId;
        @NotNull
        public Long toCardId;
        @NotNull
        @Min(1)
        public BigDecimal amount;
    }

    @PostMapping
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request, @AuthenticationPrincipal UserDetails user) {
        transferService.transfer(request.fromCardId, request.toCardId, request.amount, user.getUsername());
        return ResponseEntity.ok("Transfer successful");
    }
}
