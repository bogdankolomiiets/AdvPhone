package com.epam.rd.advphone.util;

import android.app.Application;
import android.content.Context;

import java.lang.ref.WeakReference;

public class AppContext extends Application {
    private static WeakReference<Context> context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = new WeakReference<>(this);
    }

    public static Context getContext() {
        return context.get();
    }
}
