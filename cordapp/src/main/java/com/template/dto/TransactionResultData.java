package com.template.dto;

import java.util.List;

public class TransactionResultData {
    public TransactionResultData(List<Wallet> wallets, int code, String message, String transaction) {
        this.wallets = wallets;
        this.code = code;
        this.message = message;
        this.transaction = transaction;
    }

    private final List<Wallet> wallets;
    private final int code;
    private final String message;
    private final String transaction;

    public List<Wallet> getWallets() {
        return wallets;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getTransaction() {
        return transaction;
    }
}
