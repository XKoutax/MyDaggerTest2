package com.example.mydaggertest2.car;
 
import android.util.Log;

import com.example.mydaggertest2.car.engine.Engine;

import javax.inject.Inject;
 
public class Car {
    private static final String TAG = "Car";

    // if we had a field injection here Dagger would
    // 1. call CONSTRUCTOR
    // 2. call/inject FIELD
    // 3. call injected METHOD
    // !! field and method injetion are only automaticaly executed IF we also do constructor injection
    // !! since we cant do contructor injection in MainActivity, we had to trigger the injection process manually
    //    by calling "component.inject(mainActivity);". This means we could've also had an injected method into our MainActivity
    //    since it would also be triggered by the "component.inject(mainActivity);" .

    // @Inject
    // public Engine engine


    private Driver driver;
    private Engine engine;
    private Wheels wheels;


    // constructor injection
    @Inject
    public Car(Driver driver, Engine engine, Wheels wheels) {
        this.driver = driver;
        this.engine = engine;
        this.wheels = wheels;
    }

    // method injection
    // this is the usual use-case: when u have to pass the injected object itself to the dependency
    // Dagger will execute this method immediately after the constructor finishes.
    // so, we dont have to call this method, since dagger calls it. with its necessary arguments (Injected)
    @Inject
    public void enableRemote(Remote remote) {
        remote.setListener(this);
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public Wheels getWheels() {
        return wheels;
    }

    public void setWheels(Wheels wheels) {
        this.wheels = wheels;
    }

    public void drive() {
        engine.start();
        Log.d(TAG, driver.hashCode() + " drives " + this.hashCode());
    }


}