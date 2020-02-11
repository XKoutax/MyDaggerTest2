package com.example.mydaggertest2.di;

import com.example.mydaggertest2.MainActivity;
import com.example.mydaggertest2.car.Car;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;


@PerActivity
@Component(dependencies = AppComponent.class,
        modules = {
                WheelsModule.class,
                PetrolEngineModule.class,
        })
public interface ActivityComponent {

    Car getCar();

    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder horsePower(@Named("horse power") int horsePower);

        @BindsInstance
        Builder engineCapacity(@Named("engine capacity") int engineCapacity);

        @BindsInstance
        Builder moduleParam(@Named("dieselParam") int someNumber);

        Builder appComponent(AppComponent component);

        ActivityComponent build();

    }

}