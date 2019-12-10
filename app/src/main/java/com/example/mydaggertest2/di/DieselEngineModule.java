package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.DieselEngine;
import com.example.mydaggertest2.car.Engine;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class DieselEngineModule {
 
    @Binds
    abstract Engine bindEngine(DieselEngine engine);
}