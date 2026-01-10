package com.wishlist.service;

import com.wishlist.model.Item;
import com.wishlist.model.User;
import com.wishlist.model.Wishlist;
import com.wishlist.model.dto.ItemRequestDto;
import com.wishlist.model.dto.ItemResponseDto;
import com.wishlist.model.enums.ItemStatus;
import com.wishlist.repository.ItemRepository;
import com.wishlist.repository.WishlistRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final CloudImageService cloudImageService;

    public ItemService(ItemRepository itemRepository, CloudImageService cloudImageService, WishlistRepository wishlistRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.cloudImageService = cloudImageService;
        this.wishlistRepository = wishlistRepository;
        this.userService = userService;
    }

    public ItemResponseDto convertToDto(Item item) {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setName(item.getName());
        itemResponseDto.setDescription(item.getDescription());
        itemResponseDto.setShopLink(item.getShopLink());
        itemResponseDto.setPrice(item.getPrice());
        itemResponseDto.setCurrency(item.getCurrency());
        itemResponseDto.setImageUrl(item.getImageUrl());
        itemResponseDto.setStatus(item.getStatus());
        return itemResponseDto;
    }

    public CloudImageService.CloudImageUploadResult getImageInfo(ItemRequestDto itemRequestDto, MultipartFile file) throws IOException {
        if (itemRequestDto.getImageLink() != null && !itemRequestDto.getImageLink().isBlank()) {
            return new CloudImageService.CloudImageUploadResult(itemRequestDto.getImageLink(),null);

        } else if (file != null && !file.isEmpty()) {
            CloudImageService.CloudImageUploadResult responseDto = cloudImageService.uploadImage(file, "items");
            return new CloudImageService.CloudImageUploadResult(responseDto.imageUrl(), responseDto.publicId());
        }
        return new CloudImageService.CloudImageUploadResult(null, null);
    }

    private boolean isCloudinaryImage(String imageUrl) {
        return imageUrl != null && imageUrl.contains("res.cloudinary.com");
    }

    @Transactional
    public ItemResponseDto createItem(ItemRequestDto itemRequestDto, MultipartFile file , Wishlist wishlist) throws IOException {

        CloudImageService.CloudImageUploadResult cloudImageUploadResult = getImageInfo(itemRequestDto, file);
        String imageUrl = cloudImageUploadResult.imageUrl();
        String publicId = cloudImageUploadResult.publicId();

        Item item = new Item();
        item.setName(itemRequestDto.getName());
        item.setDescription(itemRequestDto.getDescription());
        item.setShopLink(itemRequestDto.getShopLink());
        item.setPrice(itemRequestDto.getPrice());
        item.setCurrency(itemRequestDto.getCurrency());
        item.setImageUrl(imageUrl);
        item.setImagePublicId(publicId);
        item.setStatus(ItemStatus.AVAILABLE);
        item.setWishlist(wishlist);
        item.setUser(null);

        int newCount = wishlist.getCount() + 1;
        wishlist.setCount(newCount);

        wishlistRepository.save(wishlist);
        itemRepository.save(item);

        return convertToDto(item);
    }

    @Transactional
    public ItemResponseDto updateItem(Long id, ItemRequestDto itemRequestDto, MultipartFile file) throws IOException {
        Item item = getItemById(id);

        String oldImageUrl = item.getImageUrl();

        CloudImageService.CloudImageUploadResult cloudImageUploadResult = getImageInfo(itemRequestDto, file);
        String newImageUrl = cloudImageUploadResult.imageUrl();

        if (newImageUrl != null && !oldImageUrl.equals(newImageUrl)) {
            if (isCloudinaryImage(oldImageUrl)) {
                cloudImageService.deleteImage(item.getImagePublicId());
            }
            item.setImageUrl(newImageUrl);
        }

        item.setName(itemRequestDto.getName());
        item.setDescription(itemRequestDto.getDescription());
        item.setShopLink(itemRequestDto.getShopLink());
        item.setPrice(itemRequestDto.getPrice());
        item.setCurrency(itemRequestDto.getCurrency());

        itemRepository.save(item);

        return convertToDto(item);
    }

    @Transactional
    public void deleteItem(Long id, Wishlist wishlist) throws IOException {
        Item item = getItemById(id);
        if (item.getImageUrl() != null && !item.getImageUrl().isBlank()) {
            cloudImageService.deleteImage(item.getImagePublicId());
        }

        itemRepository.delete(item);
        int newCount = wishlist.getCount() - 1;

        wishlist.setCount(newCount);
        wishlistRepository.save(wishlist);
    }

    public List<ItemResponseDto> getAllWishlistItems(Wishlist wishlist) {
        List<Item> itemsFromDB = itemRepository.findByWishlistId(wishlist.getId());
        List<ItemResponseDto> resultList = new ArrayList<>();
        for (Item item : itemsFromDB) {
            ItemResponseDto responseDto = convertToDto(item);
            resultList.add(responseDto);
        }
        return resultList;
    }

    public Item getItemById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        return item;
    }

    public List<ItemResponseDto> getAllReservedItemsForUser() {
        User user = userService.getCurrentUser();

        List<Item> itemsFromDB = itemRepository.findAllReservedByUserId(user.getId());
        List<ItemResponseDto> resultList = new ArrayList<>();

        for (Item item : itemsFromDB) {
            ItemResponseDto responseDto = convertToDto(item);
            resultList.add(responseDto);
        }
        return resultList;
    }
}
