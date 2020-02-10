package com.example.mydaggertest2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mydaggertest2.car.Car;
import com.example.mydaggertest2.di.ActivityComponent;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    // field injection - works only on fields that are NOT private / final
    @Inject
    public Car car2;

    private Car car1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityComponent component = ((MyAppplication)getApplication()).getAppComponent();


        // 1. Inject the car manually, by calling the getCar() method
        car1 = component.getCar();
        car1.drive();


        // 2. Fill all @Inject fields in this class (MainActivity) using their corresponding @Provides
        // we tell dagger to inject into THIS activity, ALL the fields which contain @Inject.
        // By calling .inject() on the component it will tell the framework to go through the class
        // and inject everything with the @Inject annotation. It will use the dependencies available
        // to the component to provide the fields/constructors with the concrete classes they require.
        component.inject(this);
        car2.drive();





    }
}
