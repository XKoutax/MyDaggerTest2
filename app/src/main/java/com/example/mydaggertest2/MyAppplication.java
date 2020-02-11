package com.example.mydaggertest2;

import android.app.Application;

import com.example.mydaggertest2.di.ActivityComponent;
import com.example.mydaggertest2.di.AppComponent;
import com.example.mydaggertest2.di.DaggerAppComponent;
import com.example.mydaggertest2.di.DieselEngineModule;

public class MyAppplication extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        // move the creation of the ActivityComponent here, so that it will be created only once, when
        // the application starts. @Singleton on @Components will create objects only once, for the
        // SAME component. If we rotate screen(destroy and re-create activity) or make another component,
        // the objects will be created again.
        // So in order to make it a true "Singleton", we moved the component here.
//        component = DaggerActivityComponent.builder()
//               // .dieselEngineModule(new DieselEngineModule(100))
//                .horsePower(120)
//                .engineCapacity(1400)
//                .moduleParam(16)
//                .build();

        component = DaggerAppComponent.create();
    }

    public AppComponent getAppComponent() {
        return component;
    }

}
