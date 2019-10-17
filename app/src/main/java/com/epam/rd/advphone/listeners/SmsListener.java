package com.epam.rd.advphone.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

import com.epam.rd.advphone.services.SmsReceiveService;

public class SmsListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            intent.setClass(context, SmsReceiveService.class);
            context.startService(intent);
            abortBroadcast();
        }
    }
}
