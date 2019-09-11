package com.epam.rd.advphone.models;

public class Call {
    private int id;
    private String callImage;
    private String name;
    private String phone;
    private String type;
    private Long date;

    public Call() {
    }

    public Call(int id, String callImage, String name, String phone, String type, Long date) {
        this.id = id;
        this.callImage = callImage;
        this.name = name;
        this.phone = phone;
        this.type = type;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCallImage() {
        return callImage;
    }

    public void setCallImage(String callImage) {
        this.callImage = callImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
