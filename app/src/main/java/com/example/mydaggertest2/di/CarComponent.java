package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.Car;
import com.example.mydaggertest2.MainActivity;
import com.example.mydaggertest2.car.engine.DieselEngine;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

// now dagger puts WheelsModule into CarComponent, and knows that whenever it needs
// Rims, Tires or Wheels(in this example, we need Wheels in the getCar() method)
// it will get them from the WheelsModule.
@Singleton
@Component(modules = {
        WheelsModule.class,
        DieselEngineModule.class,
        })
public interface CarComponent {

    Car getCar();

    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder horsePower(@Named("horse power") int horsePower);

        @BindsInstance
        Builder engineCapacity(@Named("engine capacity")int engineCapacity);

        @BindsInstance
        Builder moduleParam(@Named("dieselParam")int someNumber);

        Builder dieselEngineModule(DieselEngineModule dem);

        CarComponent build();

    }

}