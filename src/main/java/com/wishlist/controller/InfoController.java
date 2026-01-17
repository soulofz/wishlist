package com.wishlist.controller;

import com.wishlist.model.dto.ItemResponseDto;
import com.wishlist.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/info")
public class InfoController {

    private final ItemService itemService;

    public InfoController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/reserved")
    public ResponseEntity<List<ItemResponseDto>> getReservedItems() {
        List<ItemResponseDto> result = itemService.getAllReservedItemsForUser();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recommendation")
    public ResponseEntity<List<ItemResponseDto>> getRecommendationItems() {
        List<ItemResponseDto> result = itemService.getRandomItems();
        return ResponseEntity.ok(result);
    }
}
