package com.epam.rd.advphone.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.epam.rd.advphone.database.ContactsDao;
import com.epam.rd.advphone.models.Contact;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactsViewModel extends ViewModel {
    private ContactsDao contactsDao;
    private LiveData<Integer> countOfFavourite;
    private LiveData<List<Contact>> contactsList;
    private MutableLiveData<String> contactsFilteringText = new MutableLiveData<>();
    private ExecutorService executorService;

    public ContactsViewModel(ContactsDao contactsDao) {
        this.contactsDao = contactsDao;
        initData();
    }

    private void initData() {
        executorService = Executors.newSingleThreadExecutor();
        if (contactsList == null) {
            executorService.execute(() -> {
                contactsList = contactsDao.getContacts();
                countOfFavourite = contactsDao.getCountOfFavourites();
            });
        }
    }

    public void setContactsFilteringText(String newText) {
        contactsFilteringText.setValue(newText);
    }

    public LiveData<List<Contact>> getContactsList() {
        return contactsList;
    }

    public LiveData<List<Contact>> getFoundContacts() {
        return Transformations.switchMap(contactsFilteringText, string ->
                contactsDao.getContactsByName(string));
    }

    public LiveData<Integer> getCountOfFavourite() {
        return countOfFavourite;
    }

    public void updateContact(Contact contact) {
        executorService.execute(() -> contactsDao.updateContact(contact));
    }

    public void deleteContact(int contactId) {
        executorService.execute(() -> contactsDao.deleteContact(contactId));
    }

    public void insertContact(Contact contact) {
        executorService.execute(() -> contactsDao.insertContact(contact));
    }
}
