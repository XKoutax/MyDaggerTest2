package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.engine.DieselEngine;
import com.example.mydaggertest2.car.engine.Engine;

import dagger.Module;
import dagger.Provides;

@Module
public class DieselEngineModule {
    private int horsePower;

    public DieselEngineModule(int horsePower) {
        this.horsePower = horsePower;
    }

    @Provides
    int provideHorsePower() {
        return horsePower;
    }

    @Provides
    Engine provideEngine(DieselEngine dieselEngine) {
        return dieselEngine;
    }


}