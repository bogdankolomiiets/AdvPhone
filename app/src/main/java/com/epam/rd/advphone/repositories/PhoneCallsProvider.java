package com.epam.rd.advphone.repositories;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import com.epam.rd.advphone.models.Call;

import java.util.ArrayList;
import java.util.List;

public class PhoneCallsProvider implements CallsProvider {
    private static PhoneCallsProvider phoneCallsProvider;
    private ContentResolver resolver;

    private PhoneCallsProvider(Context context) {
        this.resolver = context.getContentResolver();
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

        String sortOrder = CallLog.Calls.DATE + " DESC";
        try (Cursor cursor = resolver.query(uri, null, null, null, sortOrder)) {

            int callId = cursor.getColumnIndex(CallLog.Calls._ID);
            int callName = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int callPhone = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int callType = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int callImage = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                callImage = cursor.getColumnIndex(CallLog.Calls.CACHED_PHOTO_URI);
            }
            int callDate = cursor.getColumnIndex(CallLog.Calls.DATE);

            while (cursor.moveToNext()) {
                callList.add(new Call(
                        cursor.getInt(callId),
                        cursor.getString(callImage),
                        cursor.getString(callName),
                        cursor.getString(callPhone),
                        cursor.getString(callType),
                        cursor.getLong(callDate)));
            }
        }
        return callList;
    }

    @Override
    public void deleteCalls(int id) {

    }

    @Override
    public void clearAllCalls() {

    }
}
