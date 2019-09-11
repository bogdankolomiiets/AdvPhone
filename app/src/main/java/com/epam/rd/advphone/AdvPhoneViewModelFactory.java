package com.epam.rd.advphone;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.epam.rd.advphone.database.ContactsDao;
import com.epam.rd.advphone.viewmodels.CallsViewModel;
import com.epam.rd.advphone.viewmodels.ContactsViewModel;

public class AdvPhoneViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static volatile AdvPhoneViewModelFactory instance;
    private final ContactsDao contactsDao;


    public static AdvPhoneViewModelFactory getInstance(Application application) {

        if (instance == null) {
            synchronized (AdvPhoneViewModelFactory.class) {
                if (instance == null) {
                    instance = new AdvPhoneViewModelFactory(
                            ContactDaoInjection.provideContactsDao(application.getApplicationContext()));
                }
            }
        }
        return instance;
    }
//
//    public ContactsDao getContactsDao() {
//        return contactsDao;
//    }
//
//    public static void destroyInstance() {
//        instance = null;
//    }

    private AdvPhoneViewModelFactory(ContactsDao contactsDao) {
        this.contactsDao = contactsDao;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ContactsViewModel.class)) {
            return (T) new ContactsViewModel(contactsDao);
        }
        if (modelClass.isAssignableFrom(CallsViewModel.class)) {
            return (T) new CallsViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
