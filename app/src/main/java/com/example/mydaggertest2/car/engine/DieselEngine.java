package com.example.mydaggertest2.car.engine;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

public class DieselEngine implements Engine {
    private static final String TAG = "Car";

    private int horsePower;

    @Inject
    public DieselEngine(int horsePower) {
        this.horsePower = horsePower;
    }

    @Override
    public void start() {
        Log.d(TAG, "Diesel engine started. Horsepower = " + horsePower);
    }
}