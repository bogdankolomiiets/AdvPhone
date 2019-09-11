package com.epam.rd.advphone.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.epam.rd.advphone.database.ContactsDao;
import com.epam.rd.advphone.models.Contact;

import java.util.List;
import java.util.concurrent.Executors;

public class ContactsViewModel extends ViewModel {
    private ContactsDao contactsDao;
    private LiveData<Integer> countOfFavourite;
    private LiveData<List<Contact>> contactsList;

    public ContactsViewModel(ContactsDao contactsDao) {
        this.contactsList = new MutableLiveData<>();
        this.countOfFavourite = new MutableLiveData<>();
        this.contactsDao = contactsDao;
        loadData();
    }

    private void loadData() {
        Executors.newSingleThreadExecutor().submit(() -> {
            //getting first favourite contacts, and after all contacts
//            List<Contact> tempContacts = contactsDao.getFavouriteContacts();
//            countOfFavourite.postValue(tempContacts.size());
//            tempContacts.addAll(contactsDao.getContacts());
            contactsList = contactsDao.getContacts();
            countOfFavourite = contactsDao.getCountOfFavourites();
        });
    }

    public LiveData<List<Contact>> getContactsList() {
        return contactsList;
    }

    public void updateContact(Contact contact) {
        Executors.newSingleThreadExecutor().submit(() -> {
            contactsDao.updateContact(contact);
            loadData();
        });
    }

    public LiveData<Integer> getCountOfFavourite() {
        return countOfFavourite;
    }
}
