package com.example.mydaggertest2.di;

import android.util.Log;

import com.example.mydaggertest2.car.engine.DieselEngine;
import com.example.mydaggertest2.car.engine.Engine;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class DieselEngineModule {

    private int horsePower;
//
//    public DieselEngineModule() {
//
//    }
//
//    @Provides
//    int provideHorsePower(@Named("dieselParam") int horsePower) {
//        return horsePower;
//    }

    public DieselEngineModule(int horsePower) {
        this.horsePower = horsePower;
    }

    @Provides
    int provideHorsePower(int horsePower) {
        return horsePower;
    }

//    We no longer need to manually instantiate DieselEngine obj ( new DieselEngine(horsePower) )
//    Because we created a "provideHorsePower()" method, and we @Inject-ed the constructor of
//    DieselEngine "DieselEngine(int horsePower){..}" so that the provideHorsePower() can inject into it.
    @Provides
    Engine provideEngine(DieselEngine dieselEngine) {
        return dieselEngine;
    }
}