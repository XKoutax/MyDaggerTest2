package com.example.mydaggertest2.di;

import android.util.Log;

import com.example.mydaggertest2.car.engine.DieselEngine;
import com.example.mydaggertest2.car.engine.Engine;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

//@Module
//public class DieselEngineModule {
//
//    @Provides
//    int provideHorsePower(@Named("dieselParam") int horsePower) {
//        return horsePower;
//    }
//
//    @Provides
//    Engine provideEngine(DieselEngine dieselEngine) {
//        return dieselEngine;
//    }
//}


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