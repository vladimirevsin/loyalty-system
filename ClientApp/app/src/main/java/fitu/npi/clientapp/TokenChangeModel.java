package fitu.npi.clientapp;

public class TokenChangeModel {
    public TokenChangeModel(String phoneNumber, Double value, int marker) {
        this.phoneNumber = phoneNumber;
        this.value = value;
        this.marker = marker;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public int getMarker() {
        return marker;
    }

    public void setMarker(int marker) {
        this.marker = marker;
    }

    private String phoneNumber;
    private Double value;
    private int marker;
}
