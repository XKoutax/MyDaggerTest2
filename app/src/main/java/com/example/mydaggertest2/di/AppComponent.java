package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.Driver;

import javax.inject.Singleton;

import dagger.Component;


// we want this Component to provide the objects that are scoped with @Singleton ( just Driver ),
// because we want to get this object from our AppComponent, and NOT from our ActivityComponent (which has @PerActivity, and not @Singleton)
@Singleton
@Component(modules = DriverModule.class)
public interface AppComponent {

    // add provision method which returns the SubComponent type, and takes as parameters
    // all modules of the subComponent that: are not abstract AND don't have a default constructor
    // ActivityComponent has WheelsModule and DieselEngineModule.
    //                       WheelsModule is abstract
    //                       DieselEngineModule takes horsePower as constructor argument, so it will be passed here at runtime
    ActivityComponent getActivityComponent(DieselEngineModule dem);
}
