package com.epam.rd.advphone.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static com.epam.rd.advphone.database.DatabaseStringsConstants.SMS_DB_NAME;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.SMS_ID;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.SMS_IS_RECIPIENT;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.SMS_RECIPIENT_NAME;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.SMS_RECIPIENT_NUMBER;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.SMS_TEXT;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.SMS_TIME_STAMP;

@Entity(tableName = SMS_DB_NAME)
public class Sms {

    @ColumnInfo(name = SMS_ID)
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = SMS_TEXT)
    private String messageText;

    @ColumnInfo(name = SMS_RECIPIENT_NUMBER)
    private String recipientNumber;

    @ColumnInfo(name = SMS_RECIPIENT_NAME)
    private String recipientName;

    @ColumnInfo(name = SMS_TIME_STAMP)
    private long time;

    @ColumnInfo(name = SMS_IS_RECIPIENT)
    private boolean isRecipient;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(String recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isRecipient() {
        return isRecipient;
    }

    public void setRecipient(boolean recipient) {
        isRecipient = recipient;
    }

    public static class Builder {
        private Sms sms;

        public Builder() {
            sms = new Sms();
        }

        public Builder setMessageText(String messageText) {
            sms.messageText = messageText;
            return this;
        }

        public Builder setRecipientNumber(String recipientNumber) {
            sms.recipientNumber = recipientNumber;
            return this;
        }

        public Builder setRecipientName(String recipientName) {
            sms.recipientName = recipientName;
            return this;
        }

        public Builder setTime(long time) {
            sms.time = time;
            return this;
        }

        public Builder setRecipient(boolean recipient) {
            sms.isRecipient = recipient;
            return this;
        }

        public Sms build() {
            return sms;
        }
    }
}