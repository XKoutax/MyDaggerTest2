package com.example.mydaggertest2.di;

import com.example.mydaggertest2.car.Driver;

import javax.inject.Singleton;

import dagger.Component;


// we want this Component to provide the objects that are scopes with @Singleton ( just Driver ),
// because we want to get this object from our AppComponent, and NOT from our ActivityComponent (which has @PerActivity, and not @Singleton)
@Singleton
@Component(modules = DriverModule.class)
public interface AppComponent {

    Driver getDiver();

}
