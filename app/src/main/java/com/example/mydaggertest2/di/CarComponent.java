package com.example.mydaggertest2.di;

import com.example.mydaggertest2.Car;
import com.example.mydaggertest2.MainActivity;

import dagger.Component;

// now dagger puts WheelsModule into CarComponent, and knows that whenever it needs
// Rims, Tires or Wheels(in this example, we need Wheels in the getCar() method)
// it will get them from the WheelsModule.
@Component(modules = WheelsModule.class)
public interface CarComponent {
 
    Car getCar();

    void inject(MainActivity mainActivity);


}