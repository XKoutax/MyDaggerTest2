package com.example.mydaggertest2;

import android.app.Application;

import com.example.mydaggertest2.di.ActivityComponent;
import com.example.mydaggertest2.di.AppComponent;
import com.example.mydaggertest2.di.DaggerAppComponent;
import com.example.mydaggertest2.di.DieselEngineModule;

public class MyAppplication extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.create();
    }

    public AppComponent getAppComponent() {
        return component;
    }

}
