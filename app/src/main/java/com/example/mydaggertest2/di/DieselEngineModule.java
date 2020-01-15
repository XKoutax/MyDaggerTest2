package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.DieselEngine;
import com.example.mydaggertest2.car.Engine;

import javax.inject.Inject;

import dagger.Binds;
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

//    We no longer need to manually instantiate DieselEngine obj like in the below method.
//    Because we created a "provideHorsePower()" method, and we @Inject-ed the constructor of
//    DieselEngine "DieselEngine(int horsePower){..}" so that the provideHorsePower can inject into it.

//    @Provides
//    Engine provideEngine() {
//        return new DieselEngine(horsePower);
//    }
}