package com.epam.rd.advphone.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Call {

    private static SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy \nHH:mm");

    private int id;
    private String name;
    private String phone;
    private String type;
    private Long date;
    private String photo;

    private Call(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.phone = builder.phone;
        this.type = builder.type;
        this.date = builder.date;
        this.photo = builder.photo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public String getDate() {
        return formatter.format(new Date(date));
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public static class Builder {
        private int id;
        private String name;
        private String phone;
        private String type;
        private Long date;
        private String photo;

        public Builder() {
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setDate(Long date) {
            this.date = date;
            return this;
        }

        public Builder setPhoto(String photo) {
            this.photo = photo;
            return this;
        }

        public Call build() {
            return new Call(this);
        }
    }
}
