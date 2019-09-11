package com.epam.rd.advphone.util;

public interface ContactCommunicator {
    void call(String contactNumber);
    void sendSms(String contactNumber);
}
