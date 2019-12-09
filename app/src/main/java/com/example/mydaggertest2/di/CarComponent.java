package com.example.mydaggertest2.di;

import com.example.mydaggertest2.Car;
import com.example.mydaggertest2.MainActivity;

import dagger.Component;
 
@Component
public interface CarComponent {
 
    Car getCar();

    void inject(MainActivity mainActivity);


}