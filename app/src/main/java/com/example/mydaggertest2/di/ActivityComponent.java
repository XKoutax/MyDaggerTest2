package com.example.mydaggertest2.di;

import com.example.mydaggertest2.MainActivity;
import com.example.mydaggertest2.car.Car;

import dagger.Subcomponent;


@PerActivity
@Subcomponent(modules = {
        WheelsModule.class,
        DieselEngineModule.class,
})
public interface ActivityComponent {

    Car getCar();

    void inject(MainActivity mainActivity);

    //we commented out the Builder, because we no longer need the Named horsePower and engineCapacity variables, since we r using the DieselEngineModule instead of the PetrolEngineModule

//    @Component.Builder
//    interface Builder {
//
//        @BindsInstance
//        Builder horsePower(@Named("horse power") int horsePower);
//
//        @BindsInstance
//        Builder engineCapacity(@Named("engine capacity") int engineCapacity);
//
//        @BindsInstance
//        Builder moduleParam(@Named("dieselParam") int someNumber);
//
//        // Component dependencies must have a setter method IF we implemented the Component.Builder
//        // They are generated automatically if we don't implement the Component.Builder
//        Builder appComponent(AppComponent component);
//
//        ActivityComponent build();
//
//    }

}