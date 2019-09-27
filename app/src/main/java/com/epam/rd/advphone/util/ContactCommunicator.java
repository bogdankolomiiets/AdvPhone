package com.epam.rd.advphone.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.epam.rd.advphone.Constants;
import com.epam.rd.advphone.RequestCodes;
import com.epam.rd.advphone.views.SmsActivity;

public interface ContactCommunicator {
    default void call(View view, String contactNumber){
        Context context = view.getContext();

        String permission = Manifest.permission.CALL_PHONE;
        if (PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, RequestCodes.PERMISSION_CALL_PHONE);
        } else {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + Uri.encode(contactNumber)));
            context.startActivity(intent);
        }
    }

    default void sendSms(View view, String contactNumber) {
        Context context = view.getContext();

        String permission = Manifest.permission.SEND_SMS;
        if (PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, RequestCodes.PERMISSION_SEND_SMS);
        } else {
            Intent intent = new Intent(context, SmsActivity.class);
            intent.putExtra(Constants.CONTACT_NUMBER, Uri.encode(contactNumber));
            context.startActivity(intent);
        }
    }
}
