package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.DieselEngine;
import com.example.mydaggertest2.car.Engine;
import com.example.mydaggertest2.car.Rims;
import com.example.mydaggertest2.car.Tires;
import com.example.mydaggertest2.car.Wheels;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public class WheelsModule {

    // Since we dont own the Rims class and can't annotate the constructor,
    // we can just call the constructor ourselves here. The return type MUST be of type Rims.
    @Provides
    static Rims provideRims(){
        return new Rims();
    }

    // Especially useful if we can't annotate @Inject on constructor,
    // or if we require some configuration on the object AFTER instantiating it.
    @Provides
    static Tires provideTires() {
        Tires tires = new Tires();
        tires.inflate();
        return tires;
    }

    // Wheels need Rims and Tires. Now, thanks to provideRims and provideTires, dagger knows how to
    // create Rims and Tires. So now, same as in @Inject-ed constructors, we can pass these objects as arguments.
    // Dagger will pass these 2 arguments when it will call this method, using our provideRims/provideTires.
    // (!! which means these Tires will always be .inflate()-ed. )
    @Provides
    static Wheels provideWheels(Rims rims, Tires tires) {
        return new Wheels(rims, tires);
    }


}
