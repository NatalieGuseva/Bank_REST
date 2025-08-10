package com.example.bankcards.controller;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.dto.CardViewDto;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardViewDto> create(@Valid @RequestBody CardCreateDto dto, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cardService.createCard(dto, user.getUsername()));
    }

    @GetMapping
    public ResponseEntity<Page<CardViewDto>> getUserCards(@AuthenticationPrincipal UserDetails user,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(required = false) String status) {
        return ResponseEntity.ok(cardService.getUserCards(user.getUsername(), page, size, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardViewDto> getCard(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cardService.getCard(id, user.getUsername()));
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<CardViewDto> blockCard(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cardService.blockCard(id, user.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id, @AuthenticationPrincipal UserDetails user) {
        cardService.deleteCard(id, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}