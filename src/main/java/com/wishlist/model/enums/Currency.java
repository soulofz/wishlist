package com.wishlist.model.enums;

public enum Currency {
    USD("$"), EUR("€"), GBP("£"), RUB("₽"), BYN("Br"), KZT("₸");

    private String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
