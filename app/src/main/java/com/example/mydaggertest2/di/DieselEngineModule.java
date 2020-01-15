package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.DieselEngine;
import com.example.mydaggertest2.car.Engine;
import com.example.mydaggertest2.car.PetrolEngine;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public class DieselEngineModule {

    private int horsePower;

    public DieselEngineModule(int horsePower) {
        this.horsePower = horsePower;
    }

//    @Binds
//    abstract Engine bindEngine(DieselEngine engine);

    @Provides
    Engine provideEngine() {
        return new DieselEngine(horsePower);
    }
}