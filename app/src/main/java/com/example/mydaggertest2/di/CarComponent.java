package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.Car;
import com.example.mydaggertest2.MainActivity;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

// now dagger puts WheelsModule into CarComponent, and knows that whenever it needs
// Rims, Tires or Wheels(in this example, we need Wheels in the getCar() method)
// it will get them from the WheelsModule.
@Component(modules = {
        WheelsModule.class,
        PetrolEngineModule.class,
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

        // dagger will automaticaly implement this method, we just have to declare it, because
        // we are overwriting the builder definition
        CarComponent build();

    }

}