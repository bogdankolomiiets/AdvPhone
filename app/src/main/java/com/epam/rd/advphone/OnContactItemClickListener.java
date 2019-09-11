package com.epam.rd.advphone;

import com.epam.rd.advphone.models.Contact;

public interface OnContactItemClickListener {
    void onFavouriteClick(int position, Contact contact);
    void onCallClick(String contactNumber);
    void onSmsClick(String contactNumber);
    void onEditClick(Contact contact);
}
