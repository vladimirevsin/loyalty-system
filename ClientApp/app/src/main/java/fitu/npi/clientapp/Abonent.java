package fitu.npi.clientapp;

public class Abonent {
    private String FIO;
    private String phoneNumber;
    private String password;

    public String getFIO() {
        return FIO;
    }

    public void setFIO(String FIO) {
        this.FIO = FIO;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Abonent(String fio, String phoneNumber, String password) {
        FIO = fio;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
