package com.example.mydaggertest2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydaggertest2.di.CarComponent;
import com.example.mydaggertest2.di.DaggerCarComponent;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private Car car;

    // field injection - works only on fields that are NOT private / final
    @Inject
    public Car car2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        CarComponent component = DaggerCarComponent.create();
        car = component.getCar();
        car.drive();


        //we tell dagger to inject into THIS activity, all the fields which contain @Inject
        component.inject(this);
        car2.drive();





    }
}
