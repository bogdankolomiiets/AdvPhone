package com.epam.rd.advphone.models;

import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.epam.rd.advphone.database.DatabaseStringsConstants;

import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_FAVOURITE;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_ID;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_IMAGE;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_NAME;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_PHONE;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_TYPE;

@Entity(tableName = DatabaseStringsConstants.DB_NAME)
public class Contact {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = CONTACT_ID)
    private int id;

    @ColumnInfo(name = CONTACT_IMAGE)
    private String contactImage;

    @ColumnInfo(name = CONTACT_NAME)
    private String name;

    @ColumnInfo(name = CONTACT_FAVOURITE)
    private boolean favourite;

    @ColumnInfo(name = CONTACT_PHONE)
    private String phone;

    @ColumnInfo(name = CONTACT_TYPE)
    private String type;

    public Contact() {
    }

    public Contact(int id, String contactImage, String name, String phone, String type) {
        this.id = id;
        this.contactImage = contactImage;
        this.name = name;
        this.phone = phone;
        this.type = type;
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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContactImage() {
        return contactImage;
    }

    public void setContactImage(String contactImage) {
        this.contactImage = contactImage;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }
}
