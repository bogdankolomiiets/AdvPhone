package com.epam.rd.advphone.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.epam.rd.advphone.models.Contact;

import java.util.List;

import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_FAVOURITE;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_ID;
import static com.epam.rd.advphone.database.DatabaseStringsConstants.CONTACT_NAME;
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
    LiveData<List<Contact>> getFavAndAllContacts();

    @Query("SELECT * FROM " + DB_NAME + " ORDER BY " + CONTACT_NAME)
    LiveData<List<Contact>> getAllContacts();

    @Query("SELECT * FROM " + DB_NAME + " WHERE " + CONTACT_NAME + " LIKE :name ORDER BY " + CONTACT_NAME)
    LiveData<List<Contact>> getContactsByName(String name);

    @Query("SELECT COUNT() FROM " + DB_NAME + " WHERE " + CONTACT_FAVOURITE + " = 1 ORDER BY " + CONTACT_NAME)
    LiveData<Integer> getCountOfFavourites();
}
