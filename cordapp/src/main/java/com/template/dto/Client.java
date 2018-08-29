package com.template.dto;

import java.util.UUID;

public class Client {
    public String getFIO() {
        return FIO;
    }

    public String getPhone() {
        return Phone;
    }

    public UUID getClientID() {
        return clientID;
    }

    private String FIO;
    private String Phone;
    private UUID clientID;



    public Client(String fio, String phone, UUID clientID) {
        FIO = fio;
        Phone = phone;
        this.clientID = clientID;
    }
}
