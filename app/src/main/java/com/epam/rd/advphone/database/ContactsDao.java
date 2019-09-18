package com.epam.rd.advphone.database;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.epam.rd.advphone.models.Contact;

import java.util.ArrayList;
import java.util.List;

import static com.epam.rd.advphone.database.DatabaseStringsConstants.*;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_FAVOURITE;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_ID;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.DB_NAME;

@Dao
public interface ContactsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertContact(Contact contact);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertContacts(List<Contact> contacts);

    @Update
    void updateContact(Contact contact);

    @Query("DELETE FROM " + DB_NAME + " WHERE " + CONTACT_ID + " = :id")
    void deleteContact(int id);

    @Query("SELECT a.* FROM (SELECT * FROM " + DB_NAME + " WHERE " + CONTACT_FAVOURITE + " = 1) a " +
            "UNION ALL SELECT b.* FROM (SELECT * FROM " + DB_NAME + " ORDER BY " + CONTACT_NAME + ") b")
//    @Query("SELECT * FROM " + DB_NAME + " ORDER BY " + CONTACT_NAME)
    LiveData<List<Contact>> getContacts();

    @Query("SELECT * FROM " + DB_NAME + " WHERE " + CONTACT_FAVOURITE + " = 1 ORDER BY " + CONTACT_NAME)
    List<Contact> getFavouriteContacts();

    @Query("SELECT * FROM " + DB_NAME + " WHERE " + CONTACT_NAME + " LIKE :name ORDER BY " + CONTACT_NAME)
    LiveData<List<Contact>> getContactsByName(String name);

    @Query("SELECT COUNT() FROM " + DB_NAME + " WHERE " + CONTACT_FAVOURITE + " = 1 ORDER BY " + CONTACT_NAME)
    LiveData<Integer> getCountOfFavourites();
}
