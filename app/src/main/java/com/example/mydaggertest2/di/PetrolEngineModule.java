package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.engine.Engine;
import com.example.mydaggertest2.car.engine.PetrolEngine;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class PetrolEngineModule {

//    @Provides
//    Engine provideEngine(PetrolEngine engine) {
//        return engine;
//    }

    // this is simpler than writing "engine" 5 times like in the above method. And is more efficient.
    // Use a PetrolEngine and BIND it to the Engine field somewhere
    // should always use @Binds for such cases (intefaces/abstracts and their implementations)
    // ! @Binds methods take a single argument, the implementation for the interface we defined as return type
    @Binds
    abstract Engine bindEngine(PetrolEngine engine);

    // NOTE: abstract methods are never instantiated, so we can't use normal @Provides methods, only static @Provides methods


}
