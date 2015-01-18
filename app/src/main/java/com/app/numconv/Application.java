package com.app.numconv;

import android.content.Context;

public class Application extends android.app.Application {
    private static Application _application;

    @Override
    public void onCreate() {
        super.onCreate();
        _application = this;
    }

    public static Context getContext() {
        return _application.getApplicationContext();
    }
}
