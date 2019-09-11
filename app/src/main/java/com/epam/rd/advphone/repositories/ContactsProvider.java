package com.epam.rd.advphone.repositories;

import com.epam.rd.advphone.models.Contact;

import java.util.List;

public interface ContactsProvider {
    List<Contact> getContacts();
    void insertContact(Contact contact);
    void updateContact(Contact contact);
    void deleteContact(int contactId);
}
