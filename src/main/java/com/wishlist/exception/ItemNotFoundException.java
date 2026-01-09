package com.wishlist.exception;

import com.wishlist.model.Item;

public class ItemNotFoundException extends Exception {
    private Item item;

    public ItemNotFoundException(String message){
        super(message);
    }

    public ItemNotFoundException(Long id) {
        super();
        Item item = new Item();
        item.setId(id);
        this.item = item;
    }

    @Override
    public String toString() {
        return "ItemNotFoundException{item with id = " + item.getId() + " not found}";
    }
}
