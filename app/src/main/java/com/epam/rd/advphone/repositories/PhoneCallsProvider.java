package com.epam.rd.advphone.repositories;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.epam.rd.advphone.models.Call;
import com.epam.rd.advphone.viewmodels.CallsViewModel;
import com.epam.rd.advphone.views.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class PhoneCallsProvider implements CallsProvider {
    private static PhoneCallsProvider phoneCallsProvider;
    private ContentResolver resolver;
    private CallsViewModel callsViewModel;

    private PhoneCallsProvider(Context context) {
        this.resolver = context.getContentResolver();
        this.callsViewModel = MainActivity.obtainViewModel((FragmentActivity) context, CallsViewModel.class);
    }

    public static PhoneCallsProvider getInstance(Context context) {
        if (phoneCallsProvider == null) {
            phoneCallsProvider = new PhoneCallsProvider(context);
        }
        return phoneCallsProvider;
    }

    @Override
    public List<Call> getCalls() {
        List<Call> callList = new ArrayList<>();
        Uri uri = CallLog.Calls.CONTENT_URI;

        try (@SuppressLint("MissingPermission") Cursor cursor = resolver.query(uri, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER)) {

            while (cursor.moveToNext()) {

                int callId = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
                String callName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                String callPhone = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                String callType = cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE));
                Long callDate = cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE));

                String checkedCallType = checkCallType(callType);

                String callPhoto = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    callPhoto = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI));
                }

                Call call = new Call.Builder()
                        .setId(callId)
                        .setName(callName)
                        .setPhone(callPhone)
                        .setType(checkedCallType)
                        .setDate(callDate)
                        .setPhoto(callPhoto)
                        .build();

                if (!callList.contains(call)) {
                    if (callName == null)
                        lookupContactInfo(call);
                    callList.add(call);
                }
            }
        }
        return callList;
    }

    private String checkCallType(String callType) {
        String dirCode = null;

        int typeCode = Integer.parseInt(callType);
        switch (typeCode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dirCode = "OUTGOING";
                break;
            case CallLog.Calls.INCOMING_TYPE:
                dirCode = "INCOMING";
                break;
            case CallLog.Calls.MISSED_TYPE:
                dirCode = "MISSED";
                break;
        }

        return dirCode;
    }

    private void lookupContactInfo(Call call) {
        Cursor cursor = null;
        try {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(call.getPhone()));
            cursor = resolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup.PHOTO_URI}, null, null, null);
        } catch (IllegalArgumentException e) {
            //This is sometimes thrown when number is in invalid format, so phone cannot recognize it.
        } catch (SecurityException e) {
            return;
        }

        if (cursor != null) {
            if (cursor.moveToNext()) {
                call.setName(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
                call.setPhoto(cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI)));
            }
            cursor.close();
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void deleteCalls(int id) {
        resolver.delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + "=?", new String[]{String.valueOf(id)});
        callsViewModel.refreshCallsLog();
    }

    @Override
    public void clearAllCalls() {

    }
}
