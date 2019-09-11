package com.epam.rd.advphone.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.epam.rd.advphone.models.Contact;

@Database(entities = {Contact.class}, version = 1)
public abstract class ContactsDatabase extends RoomDatabase {
    private static ContactsDatabase instance;
    public abstract ContactsDao contactDao();

    public static ContactsDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context, ContactsDatabase.class, DatabaseStringsConstants.DB_NAME).build();
        }
        return instance;
    }
}
