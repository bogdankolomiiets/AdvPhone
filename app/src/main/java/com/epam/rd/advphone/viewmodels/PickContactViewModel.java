package com.epam.rd.advphone.viewmodels;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.epam.rd.advphone.ContactDaoInjection;
import com.epam.rd.advphone.SmsDaoInjection;
import com.epam.rd.advphone.database.SmsDao;
import com.epam.rd.advphone.models.Contact;
import com.epam.rd.advphone.models.Sms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class PickContactViewModel extends AndroidViewModel {
    private SmsDao smsDao;
    private LiveData<List<Contact>> contactsLive;
    private List<Contact> contactsList;
    private List<Contact> tempContactsList;
    private MutableLiveData<String> pattern;

    public PickContactViewModel(@NonNull Application application) {
        super(application);
        smsDao = SmsDaoInjection.provideSmsDao(application);
        contactsList = new ArrayList<>();
        tempContactsList = new ArrayList<>();
        contactsLive = ContactDaoInjection.provideContactsDao(application).getAllContacts();
        pattern = new MutableLiveData<>();
        pattern.setValue("\\S*\\S*");
    }

    public List<Contact> getContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return contactsList.stream()
                    .filter(contact -> contact.getName().toLowerCase().matches(Objects.requireNonNull(pattern.getValue())))
                    .collect(Collectors.toList());
        } else {
            tempContactsList.clear();
            for (Contact contact : contactsList) {
                if (contact.getName().toLowerCase().matches(Objects.requireNonNull(pattern.getValue()))) {
                    tempContactsList.add(contact);
                }
            }
            return tempContactsList;
        }
    }

    public void setPattern(String pattern) {
        this.pattern.postValue("\\S*" + pattern.toLowerCase() + "\\S*");
    }

    public MutableLiveData<String> getPattern() {
        return pattern;
    }

    public LiveData<List<Contact>> getContactsLive() {
        return contactsLive;
    }

    public void setContactsList(List<Contact> contactsList) {
        this.contactsList.clear();
        this.contactsList.addAll(contactsList);
    }

    public void insertNewSms(Sms sms) {
        Executors.newSingleThreadExecutor().execute(() -> smsDao.insertSms(sms));
    }

    public void deleteSms(int smsId) {
//        Executors.newSingleThreadExecutor().execute(() -> smsDao.deleteSms(smsId));
    }
}
