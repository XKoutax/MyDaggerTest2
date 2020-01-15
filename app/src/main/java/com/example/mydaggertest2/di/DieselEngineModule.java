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


//    We no longer need to manually instantiate DieselEngine obj using "new DieselEngine(horsePower)".
//    Because we created a "provideHorsePower()" method, and we @Inject-ed the constructor of
//    DieselEngine "DieselEngine(int horsePower){..}" so that the provideHorsePower() can inject into it.
    @Provides
    Engine provideEngine(DieselEngine dieselEngine) {
        return dieselEngine;
    }


}