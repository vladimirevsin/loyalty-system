package com.template.dto;

import java.util.UUID;

public class Wallet {
    public Wallet(UUID walletId, Double value, int marker) {
        this.walletId = walletId;
        this.value = value;
        this.marker = marker;
    }

    private final UUID walletId;
    private final Double value;
    private final int marker;

    public UUID getWalletId() {
        return walletId;
    }

    public Double getValue() {
        return value;
    }

    public int getMarker() {
        return marker;
    }
}
