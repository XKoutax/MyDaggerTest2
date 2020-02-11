# MyDaggerTest2

##### DAGger - Directed Acyclic Graph  

In a nutshell, Dagger creates objects and provides them at the right time.  What we need to do is tell dagger __how__ to do it: annotating the constructor of a class with ```@Inject``` (useful if we __own__ the class and can annotate it's constuctor), and using the ```@Provides``` annotation on methods inside modules (classes annotated with ```@Module```)

* __@Inject__: marks those dependencies which should be provided by Dependency Injection framework
* __@Provides__: to marks methods that return dependencies, used inside module classes 
* __@Module__: marks a classe which provides dependencies.
* __@Component__: marks an interface that Dagger will use to generate the code that will do the dependency injection for us.  


- - - - 



## 1. @Inject 


```@Inject``` essentially lets Dagger know how to create classes.

```java
@Inject
public Engine() {
}
```

```java

@Inject
public Wheels() {
}
```

If the constructor of a class requires as aguments classes that we __own__, we can simply annotate the constructor of our class and the constructor of the required classes with ```@Inject```. This way, dagger will know how to create the classes/arguments that will then be injected into our main class.
```java
public Car(Engine engine, Wheels wheels) {
}
```

For it to work, ```@Inject``` must be put on fields that are __not__ private or final.

```@Inject``` can be put on constructor, field(variable) or method. In order for the field or method injection to work however, we must:  
* either have the constructor injected as well (using the ```@Inject``` annotation)
* either trigger the injection using the ```CarComponent.inject(MainAcitivty mainActivity);``` method in our main activity, letting dagger know to inject all the necessary field from our MainActivity class. This is especially useful when we can't use constructor injection (in Activities / Fragment for example)


- - - -



## 2. @Component

Interfaces annotated with ```@Component``` are what we are going to use in order to achieve our dependancy injection.

```java
@Component(modules = WheelsModule.class)
public interface CarComponent {

    Car getCar();
    ...
}
```

The WheelsModule class provides Rims and Tires, which are required in order to build a Wheels object. In turn, the getCar() method inside the CarComponent class requires Wheels in order to create an instance of Car. By adding the module to our component ( ```java @Component(modules = WheelsModule.class) ```), dagger knows that whenever CarComponent needs Rims, Tires or Wheels(in this example, we need Wheels in the getCar() method), it will get them from the WheelsModule.



- - - - 



## 3. @Module 

A module is a class that contributes to the object graph (adds objects to the dependency graph, through the ```@Provides``` methods). Especially useful if we can't annotate ```@Inject``` on constructor, or if we require some configuration on a class __after__ instantiating it.

```java

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
    // (!! in this example, Tires will always be .inflate()-ed.)
    @Provides
    static Wheels provideWheels(Rims rims, Tires tires) {
        return new Wheels(rims, tires);
    }

}
```

In order for a Module class to provide an object of a certain type, the return type of the ```provide``` method must be that object. Also for efficiency, if possible (no stateful parameters -parametes which depends on the curent state of the app- are required), provide methods should always be static. This way, the generated code won't have to create an instance of our Module class, instead it will simply call the static methods.

```@Provides```  is especially useful if we can't annotate @Inject on constructor, or if we require some configuration on the object __after__ instantiating it. Most cases with 3rd party libraries.  

In this example, we assume that both the Wheels class and the classes that it needs are not our own, therefore we can't ```@Inject``` their constuctors. So we must create a module, instantiating and configuring them according to our needs from which we will later __provide__ them to components.  
We provide a new instance of Rims ```provideRims()``` and a new instance of Tires ```ProvideTires()```, the latter requiring some configuration ( ```.inflate()``` ). Now, after we've also added this module into our CarComponent class, we should be able to create inject Rims and Tires which need to be injected for creating Wheels, which in turn will be required for creating a Car.



- - - -



## 4. @Binds

Assume that our Engine class was an interface instead.
```java
public interface Engine {
    void start();
}
```
Firstly, we no longer have a constructor, so we have nothing to annotate with ```@Inject```.  Secondly and most importantly, dagger won't know what implementation of Engine to use when creating a Car. 
For 2 implementations of the Engine interface, we can't just annotate both of them with ```@Inject```, since they can both fit in the constructor of the Car, and dagger won't choose one randomly.

We should create a Module, which should ```@Provide``` the necessery engine.
```java
@Module
public class PetrolEngineModule {
    @Provides
    Engine provideEngine(PetrolEngine engine) {
        return engine;
    }
}
```
And add it to the CarComponent list of modules
```java
@Component(modules = {
        WheelsModule.class,
        PetrolEngineModule.class})
public interface CarComponent {
     ...
}
```
Now it should work. But to make it easier and more efficient, instead of our current ```provideEngine(...)``` method, we can instead use a ```@Binds``` method. A method annotated with ```@Binds``` does exactly that, bind an implementation of an interface/abstract to the required field of its base type (PetrolEngine -----> Engine).  

Methods that use ```@Binds``` must be abstract, therefore must reside in abstract classes.
```java

@Module
public abstract class PetrolEngineModule {
    // this is simpler than writing "engine" 5 times like in the previous method. And more efficient.
    // Use a PetrolEngine(argument) and BIND it to the Engine field(method return type)
    // should always use @Binds for such cases (intefaces/abstracts and their implementations)
   
    @Binds
    abstract Engine bindEngine(PetrolEngine engine);

    // NOTE: abstract methods are never instantiated, so we can't use normal @Provides methods, only static @Provides methods

}
```

```@Binds``` methods take a single argument, the implementation for the interface we defined as return type.  
Also, the CarComponent cannot contain more than 1 module for the Engine implmentations module (PetrolEngineModule and DieselEngineModule). That is where we specify the object type for our Engine dependency.  

! ```@Binds``` does not support any configuration!

## 5. Stateful Modules 

__Recap:__ * use ```@Inject``` on the constructor of a class so dagger can instantiate it directly.
           * for more complex situations, we use ```@Module```s, in which we put either ```@Provides``` or ```@Binds```.
         
Up to this point however, everything we have created has to be known at compile time. But what if we had a sitution in which our information comes later, at runtime, for example from a stream or an endpoint. We'd have to pass this data and inject it at runtime.  

What if Engine had a field ```int horsePower``` that got passed in it's constructor:
```java
public DieselEngine(int horsePower) {
    this.horsePower = horsePower;
}
```
Assuming we dont know this value beforehand, and we want to pass it at runtime, when we are building the component.  

Since dagger can no longer instantiate this constuctor directly, we can remove ```@Inject``` from it. Because now we have to call this constructor and pass the ```int horsePower``` value.  

And because of removing ```@Inject```, we can no longer use ```@Binds``` in the DieselEngineModule, because ```@Binds``` does not support any configuration. So we have to replace it with ```@Provides``` back.

```java
@Module
public class DieselEngineModule {

    private int horsePower;

    public DieselEngineModule(int horsePower) {
        this.horsePower = horsePower;
    }

    @Provides
    Engine provideEngine() {
        return new DieselEngine(horsePower);
    }
}
```
We create a field horsePower in out module, which will be set at runtime, through which we will instantiate/provide our DieselEngine.  

_Instead of putting ```horsePower``` into the DieselEngine constructor directly in the DieselEngineModule, we could also create a ```@Provides``` method for ```horsePower```, if we would've wanted it to be available in other places as well. The ApplicationContext for example would be a real candidate for this, since we would only have it available at runtime, but we want to use it in many diferent places. For this, we would've passed the ApplicationContext in the constructor of the Module, inorder to save it within the Module's field (```this.appC = appC```), and then we would've made a ```@Provides``` method which would return this ApplicationContext._  

Now if we were to rebuild the project, the ```DaggerCarComponent.create();``` will no longer exist/work, because it is only available if none of the modules of the component take arguments over the constructor. Instead, now we have a builder, in which we can build our DieselEngineModule with it's runtime parameter:
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.activity_main);
       
     CarComponent component = DaggerCarComponent.builder()
             .dieselEngineModule(new DieselEngineModule(100))
             .build();
     car = component.getCar();
     car.drive();
}
```
Notice the deprecared generated method inside out builder: ```wheelsModule()```. Checking the method description and implementation will say:  

```java
/**
* @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
*/
@Deprecated
public Builder wheelsModule(WheelsModule wheelsModule) {
  Preconditions.checkNotNull(wheelsModule);
  return this;
}
```

This is because our WheelsModule contains __only__ static methods. We don't need an instance of our wheelsModule, unlike the dieselEngineModule. We need an instante of dieselEngineModule in order to add a variable into it at runtime(```horsePower```). In WheelsModule however, dagger can simply call it's methods, which are all static, without ever instantiating the class.  

An even better optimisation would be to make the WheelsModule class abstract. After rebuilding, the generated method for wheelsModule dissapears altogether, since abstract classes can't be instantiated. Another benefit of this is that dagger will not compile unless all classes of the abstract Module are static.  

_In other words, if all Module methods are static, you should make that module abstract._  



- - - -



### 5.1 ```@Provides``` method for ```horsePower```

Create the providesHorsePower() method inside our DieselEngineModule.
```java
@Provides
int provideHorsePower() {
    return horsePower;
}
```
Now, in order for it to be injected, we need to annotate the constructor of ```DieselEngine```:
```java
@Inject
public DieselEngine(int horsePower) {
    this.horsePower = horsePower;
}
```
And finally, now we don't need to instantiate our ``` DieselEngine``` object in our provideEngine method, since dagger will do that for us, now that it knows how to create it and it's dependancies:
```java
@Provides
Engine provideEngine(DieselEngine dieselEngine) {
    return dieselEngine;
}
```
The code should still work after doing these changes.  
This basicaly means for dagger that __whenever__ we need an ```int```, it will use this ```int provideHorsePower()``` method. Note that this don't mention using it for the ```horsePower``` variable alone, but for any integer. Dagger will only care about the return type.

And now that we have have separated ```@Provides``` horsepower from the dieselEngine, we should separate them in different modules, so we could use them independently from each other. 



- - - - 



## 6. @Component.Builder, @BindsInstance and @Named

### 6.1  ```@Component.Builder``` and ```@BindsInstance```

In MainActivity, instead of passing our horsePower integer to the Module, which is then passed to the builder
```java
CarComponent component = DaggerCarComponent.builder()
                .dieselEngineModule(new DieselEngineModule(100))
                .build();
```
we can pass the horsePower integer to the builder directly.  

First, we must go into ```PetrolEngine``` and change it similary to DieselEngine, so it needs a horsePower as well.
```java
public class PetrolEngine implements Engine {
    private static final String TAG = "Car";

    private int horsePower;

    @Inject
    public PetrolEngine(int horsePower) {
        this.horsePower = horsePower;
    }

    @Override
    public void start() {
        Log.d(TAG, "Petrol engine started. Horsepower = " + horsePower);
    }
}
```
And now we still want to pass the horsePower integer at runtime, but we will do it in a different way.  

Inside ```CarComponent``` interface, we create a nested interface ```Builder```, and annotate it with ```@Component.Builder```. This is where we will define our API for our CarComponent builder (DaggerCarComponent.builder().myAPImethod().2ndAPImethod(). ...)
```java
@Component(modules = {
        WheelsModule.class,
        PetrolEngineModule.class,
        })
public interface CarComponent {

    Car getCar();

    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder horsePower(int horsePower);

        // dagger will automaticaly implement this method, we just have to declare it, because
        // we are overwriting the builder definition
        CarComponent build();
    }
    
}
```
The ```MyComponent build()``` method is always neccessery when overwriting the ```Builder``` definition.  
The ```@BindsInstance``` annotation is used to add variables into our dependency graph at runtime, which has the same effect as passing the variable at runtime to a module and providing it over a ```@Provides``` method. But it is more efficient because daggr doesn't need to create an instance of the Module.

And now we've created the method ```horsePower(int horsePower)``` where we will bind the integer, which we will pass in MainActivity into our CarComponent builder. The Builder return type is simply used for the _builder pattern_, so we can chain builder methods.  

Now our CarComponent will look like this:
```java
CarComponent component = DaggerCarComponent.builder()
                .horsePower(150)
                .build();
```
Now similary to our ```@Provides int provideHorsePower``` method in our ```DieselEngineModule```, this "150" value will be added to the dependency graph, and dagger can use it whenever we need an integer. Which is now the case in our ```PetrolEngineModule``` when we inject the constructor for the ```PetrolEngine``` object.  

The difference is that unlike ```DieselEngineModule```, our ```PetrolEngineModule``` is stil abstract , only contains our @Binds method, and doesn't need to have anything(horsePower) passed into it:
```java
@Module
public abstract class PetrolEngineModule {
    @Binds
    abstract Engine bindEngine(PetrolEngine engine);
}
```
So dagger still doesn't have to instantiate it, which makes the code more efficient.So ```@BindsInstance``` with ```@Component.Builder``` should always be prefered over Module constructor arguments whenever possible. Keep in mind also that the naming of the method inside our Builder is purely arbitrary, dagger will simply look to bind an integer inside the Modules of our Component.  


###  6.2 ```@Named```

Let's add another integer inside PetrolEngine, engineCapacity:
```java
public class PetrolEngine implements Engine {
    private static final String TAG = "Car";

    private int horsePower;
    private int engineCapacity;

    @Inject
    public PetrolEngine(int horsePower, int engineCapacity) {
        this.horsePower = horsePower;
        this.engineCapacity = engineCapacity;
    }

    @Override
    public void start() {
        Log.d(TAG, "Petrol engine started. " +
                "\nHorsepower = " + horsePower +
                "\nEngine capacity = " + engineCapacity);
    }
}
```
If we run the code now, it will work, and engineCapacity will show as 150. Because when we added the integer to our dependency graph (either through ```@BindsInstance Builder horsePower(int horsePower)``` or through our ```@Provides int provideHorsePower() { return horsePower;} ``` method), we tell dagger that whenever an integer is needed, use this integer value. For any integer that must be provided.

Only the object type matters, not the naming. We could've just named them  ```@BindsInstance Builder intValue(int x)``` and ```@Provides int provideSomeInteger() { return someInteger;} ```.  

Now, if we add another integer at runtime:
```java
@Component.Builder
    interface Builder {
        @BindsInstance
        Builder horsePower(int horsePower);

        @BindsInstance
        Builder engineCapacity(int engineCapacity);

        CarComponent build();
    }
```
and pass it to the CarComponent builder:
```java
CarComponent component = DaggerCarComponent.builder()
        .horsePower(150)
        .engineCapacity(1400)
        .build();
```
We'll get a compile-time error, ``` error: [Dagger/DuplicateBindings] java.lang.Integer is bound multiple times: ... ```. Because now dagger doesn't know which integer to use. In order to fix this problem, we need to name our dependancies, using ```@Named(...)```.
```java
@Component.Builder
    interface Builder {
        @BindsInstance
        Builder horsePower(@Named("horse power")int horsePower);

        @BindsInstance
        Builder engineCapacity(@Named("engine capacity")int engineCapacity);

        CarComponent build();
    }
```
And now we need to do the same in the places where we need those values. In our ```PetrolEngine```, in the constructor:
```java
@Inject
public PetrolEngine(@Named("horse power") int horsePower,
                    @Named("engine capacity")int engineCapacity) {
    this.horsePower = horsePower;
    this.engineCapacity = engineCapacity;
}
```
And now the 2 parameters of PetrolEngine should show the proper integers, 150 and 1400.  

We can use this ```@Named``` annotation wherever we have to provide or consume dependencies (```@Provides``` methods and ```@Inject``` annotated fields). Dagger distinguishes between same-type dependencies using these annotations.  

(Now it would make sense to change our ```DieselEngineModule``` into an abstract class as well, for efficiency.)

_One way to avoid using string tags("horse power","engine capacity") that may be error prone, is by creating our own annotations (```@EngineCapacity``` for example)._

- - - - 
### Important!
Now, with our custom ```Component.Builder``` :
```java
@Component.Builder
    interface Builder {
        @BindsInstance
        Builder horsePower(@Named("horse power")int horsePower);

        @BindsInstance
        Builder engineCapacity(@Named("engine capacity")int engineCapacity);

        @BindsInstance
        Builder moduleParam(@Named("dieselParam")int someNumber);
        CarComponent build();
    }
```

if we wanted to switch back to DieselEngineModule (replace ```PetrolEngineModule``` with ```DieselEngineModule``` in out CarComponent) and run the app, we'd get an error:  

```@Component.Builder is missing setters for required modules or components: [com.example.mydaggertest2.di.DieselEngineModule]```  

What this means is that our Component/Builder does not know how to create a DieselEngineModule. That is because the ```DieselEngineModule``` constructor is parameterized. Dagger knows how to create an empty constructor, but in case the constructor has parameters, it doesn't know where to get them from.  

In order to fix this, we can:  

* Keep the constructor and create the module ourselves(in the Builder)

Kept constructor:
```java
@Module
public class DieselEngineModule {

    private int horsePower;

    public DieselEngineModule(int horsePower) {
        this.horsePower = horsePower;
    }

    @Provides
    int provideHorsePower() {
        return horsePower;
    }

    @Provides
    Engine provideEngine(DieselEngine dieselEngine) {
        return dieselEngine;
    }
}
```

and Builder method for creating the Module:
```java
    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder horsePower(@Named("horse power") int horsePower);

        @BindsInstance
        Builder engineCapacity(@Named("engine capacity")int engineCapacity);

        Builder dieselEngineModule(DieselEngineModule dem);

        CarComponent build();

    }
```
This way, we will declare it when we create the component:
```java
CarComponent component = DaggerCarComponent.builder()
                .dieselEngineModule(new DieselEngineModule(100))
                .horsePower(150)
                .engineCapacity(1400)
                .build();
```
- - - -
* OR remove the parametrized constructor, and bind the parameter:

Same as our ```PetrolEngineModule```, remove the parameter from the constructor:
```java
@Module
public class DieselEngineModule {
    @Provides
    int provideHorsePower(@Named("dieselParam") int horsePower) {
        return horsePower;
    }
    @Provides
    Engine provideEngine(DieselEngine dieselEngine) {
        return dieselEngine;
    }
}
```

And add the integer inside our ```Component.Builder```:
```java
    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder horsePower(@Named("horse power") int horsePower);

        @BindsInstance
        Builder engineCapacity(@Named("engine capacity")int engineCapacity);

        @BindsInstance
        Builder moduleParam(@Named("dieselParam")int someNumber);

        CarComponent build();

    }
```

And set the integer when we create the component:
```java
component = DaggerCarComponent.builder()
                .horsePower(120)
                .engineCapacity(1400)
                .moduleParam(16)
                .build();
```
In this case, whenever the ```DieselEngineModule``` will be required to provide an ```int```, the ```@Provides int provideHorsePower(@Named("dieselParam") int horsePower)``` method will be called, which will always return the ```@Named("dieselParam")``` value saved in the ```CarComponent```. In other words, our ```@Named``` variable from within the ```Component``` will be injected inside our ```Module```, which in turn will provide / "inject" it to any of it's ```@Provides``` methods when needed.

In this case, the ```provideEngine(DieselEngine dieselEngine)``` method will be provided with the horsePower from the ```@Provides provideHorsePower(@Named("dieselParam") int horsePower)``` method. Now however, we are no longer saving the horsePower value inside our DieselEngineModule, since it's never set anymore.


_*Recap :*_ 

We use ```@Inject``` on classes that we own and wish to add to the dependancy graph, and ```@Provides``` inside modules, especially when we don't own the classes(classes from libraries) and when we need some configuration outside the constructor (Factory objects) 

We use ```@Binds``` on abstract methods, in order to bind an implementation to its interface: 
```@Binds abstract Random bindRandom(SecureRandom secureRandom);```. It's a more efficient approach instead of ```@Provides``` in such cases, because the generated implementation is likely to be more efficient.


- - - -



##  7. @Singleton

Add driver class, with an ```@Inject``` constructor, and add it to ```Car```, field and constructor.
```java
public class Driver {
    @Inject
    public Driver() {
    }
}
```
If we'll print the Driver object inside car as well, for 2 cars we'll have 2 different drivers(same as ```Car``` and the rest of the objects inside it). But what if we want to have the same driver for all our cars.  

Annotate ```Driver``` class with ```@Singleton```. This should intuitively be enough, but we will get a compile-time error. Because now our Component contains a ```@Singleton``` annotated module. In which case, our Component must also be annotated with ```@Singleton```.  

 _Whenever our ```Components``` contain a ```@Singleton``` module, they must also be annotated as ```@Singleton```._  

Now we'll have the same driver for our 2 different cars.  

* If your class doesn't come from an ```@Inject``` annotated constructor, but from a ```@Provides```method, then you have to annotated that ```@Provides``` method directly with the ```@Singleton``` annotation.
* ```@Binds``` methods (the ones used for binding an interface implementation to the interface-type field) can also be annotated with ```@Singleton```: ```@Binds abstract Engine bindEngine(PetrolEngine engine);```. But in this case it makes more sense to annotated the PetrolEngine directly, because it also has an ```@Inject``` annotated constructor, and ```@Singleton``` scope annotation is more of a implementation detail, not something you want todefine at a level(module) where you just decide which implementation to return for an interface. So usually you wouldn't wanna put ```@Singleton``` on a binds annotation.

__NOTE:__ if we were to create 2 CarComponents and get a car from each, and call our ```.drive()``` method, the Driver object would be different. That is because ```@Singleton``` only works within the same Component object. So, if we want a real application-wide singleton, you have to instantiate the Component once, in the application class (since Application wraps the entire lifetime/lifecycle of the app) and acces the Component from there.

```java
public class MyAppplication extends Application {

    private CarComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        // move the creation of the CarComponent here, so that it will be created only once, when the application starts. 
        // @Singleton on @Components will create objects only once, for the SAME component. 
        // If we rotate screen(destroy and re-create activity) or make another component,
        // the objects will be created again.
        // So in order to make it a true "Singleton", we moved the component here.
        component = DaggerCarComponent.builder()
//                .dieselEngineModule(new DieselEngineModule(100))
                .horsePower(120)
                .engineCapacity(1400)
                .moduleParam(16)
                .build();
    }

    public CarComponent getAppComponent() {
        return component;
    }

}
```

Don't forget to add it in the manifest file ```AndroidManifest.xml```:
```xml
...
    <application
        android:name=".MyAppplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
    ...
...
```

- - - -


##  7. Custom Scopes / Annotation

Go inside the ```@Singleton``` annotation class, copy the definition and create a new annotation called ```@PerActivity```. Annotate the Car class with it.

```java
@PerActivity
public class Car {
    private static final String TAG = "Car";
....
```
This will now work exactly like the ```@Singleton``` annotation. It tells Dagger to only create a single instance of a Car within the same component.

In other words, it does not know when the Car object should be destroyed. It works only as "documentation". If we used this in our AppComponent, Dagger would still just create an Application-wide singleton. We are responsible for actually realising this new scope functionality.

The way we do this is by creating a second component, which will only live as long as the Activity. Unlike the component that we declared in our Application class, which lives as long as the Application.


We will use the CarComponent as our ```ActivityComponent```, and create a new Component for the App class.
* rename CarComponent to ```  ``` and change ```@Singleton``` to ```@PerActivity```
```java
@PerActivity
@Component(modules = {
        WheelsModule.class,
        DieselEngineModule.class,
        })
public interface ActivityComponent {

    Car getCar();
    ...
...
```

* create the AppComponent, which we want it scoped with ```@Singleton```
```java
// we want this Component to provide the objects that are scopes with @Singleton ( just Driver ),
// because we want to get this object from our AppComponent, and NOT from our ActivityComponent (which has @PerActivity, and not @Singleton)
@Singleton
@Component
public interface AppComponent {
}
```

_What we'll later want to do is connect our 2 components. We want to instantiate an ActivityComponent in our Activity, but when we want to get a Driver,we DON'T instantiate it from ActivityComponent, but get it from the AppComponent._

We must define what we want to expose to the outside from the AppComponent, otherwise the ActivityComponent won't have access to it.

```java
@Singleton
@Component
public interface AppComponent {

    Driver getDiver();

}
```
_(Again, the name of the provision method doesn't matter)_

_Doc: Component must have signatures that conform to either provision or members-injection contracts._

_Provision methods have no parameters and return an injected or provided type. The following are all valid provision method declarations:_

SomeType getSomeType();

Provider<SomeType> getSomeTypeProvider();

Lazy<SomeType> getLazySomeType();

    
_Members-injection methods have a single parameter and inject dependencies into each of the Inject-annotated fields and methods of the passed instance. A members-injection method may be void or return its single parameter as a convenience for chaining. The following are all valid members-injection method declarations:_


   void injectSomeType(SomeType someType);

   SomeType injectAndReturnSomeType(SomeType someType);



Our Driver class has @Inject constructor, so dagger can instantiate it directly. But to ake it more clear, we'll assume we dont own the driver class, and we'll make a module for it.

```java
public class Driver {
    //we don't own this class so we can't annotate it with @Inject
}
```

```java
@Module
public abstract class DriverModule {
    
    @Provides
    @Singleton
    static Driver provideDriver() {
        return new Driver();
    }
}
```

And in AppComponent, add this module to the declaration of the Component:

```java
@Singleton
@Component(modules = DriverModule.class)
public interface AppComponent {

    Driver getDiver();

}
```
Now it is more clear that our AppComponent contains the DriverModule, so it is responsible for provider a Driver. And our ActivityComponent does not.

Add the dependancy to AppComponent in the ActivityComponent:

```java
@PerActivity
@Component(dependencies = AppComponent.class,
        modules = {
                WheelsModule.class,
                PetrolEngineModule.class,
        })
public interface ActivityComponent {
```

and in its Builder, add the AppComponent. Remember that if we didn't implement the Component.Builder ourselves, we woudn't have to do this because it would be generated automaticaly. But since we did, we have to explicitely add AppComponent to the Builder.

```java
@PerActivity
@Component(dependencies = AppComponent.class,
        modules = {
                WheelsModule.class,
                PetrolEngineModule.class,
        })
public interface ActivityComponent {

    Car getCar();

    void inject(MainActivity mainActivity);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder horsePower(@Named("horse power") int horsePower);

        @BindsInstance
        Builder engineCapacity(@Named("engine capacity") int engineCapacity);

        @BindsInstance
        Builder moduleParam(@Named("dieselParam") int someNumber);

        Builder appComponent(AppComponent component);

        ActivityComponent build();

    }

}
```

Now we need to instante the ActivityComponent inside our MainActivity, instead of the Appcomponent.

```java
ActivityComponent component = DaggerActivityComponent.builder()
                .horsePower(120)
                .engineCapacity(1400)
                .moduleParam(30)
                .appComponent(((MyAppplication)getApplication()).getAppComponent())
                .build();
```

So now we will get the same Driver throughout our entire Application. And the same Car throughout our entire Activity. 

Upon first starting the app, we will have the same driver, and the same car now(because it is activity scoped). And when we rotate the device, the driver will stay the same, but the car will change.



- - - -


## 8. Subcomponents

In the previous step, in order to "combine" / make the 2 components communicate, we had to:

* add AppComponent as a dependency inside the ActivityComponent
```java
@Component(dependencies = AppComponent.class, modules = { ... })
```
* add an AppComponent setter method to the ActivityComponent Component.Builder(if we've implemented the Component.Builder), and instantiate it when we first built the ActivityComponent 
```java
@Component.Builder
    interface Builder {
        ...
        Builder appComponent(AppComponent component);
        ActivityComponent build();
    }
```
```java
ActivityComponent component = DaggerActivityComponent.builder()
                .horsePower(120)
                .engineCapacity(1400)
                .moduleParam(30)
                .appComponent(((MyAppplication)getApplication()).getAppComponent())
                .build();
```
* expose the Driver inside the AppComponent
```java
@Singleton
@Component(modules = DriverModule.class)
public interface AppComponent {
    Driver getDiver();
}
```
If we'd remove the ```getDriver()``` provision method from the AppComponent, the project wouldn't compile anymore, because ActivityComponent wouldn't know where it can get the Driver from. Also, if we wuld add other object to the AppComponent, besides the Driver (DriverModule), the ActivityComponent wouldnt be abe to access these objects unless we also expose them explicitely through a provision method.

There is another way however, to connect the 2 components. Instead of declaring the AppComponent as a dependency of the ActivityComponent, we can turn the ActivityComponent into a __sub-component__ of the AppComponent. The difference is that a ```Subcomponent``` can access ALL the objects of the parent component. So we would be able to remove the ```getDriver()``` method, and any other possible provision methods.


__First:__ turn the ActivityComponent into an SubComponent. Also change the module required to DieselEngineModule, and remove the @Component.Builder _for now_, since Subcomponents have a different kind of builders. (_more on that later._) And remove the ```getDriver()``` method.
```java
@PerActivity
@Subcomponent(modules = {
        WheelsModule.class,
        DieselEngineModule.class,
})
public interface ActivityComponent {
    Car getCar();
    void inject(MainActivity mainActivity);
}
```

Our subcomponent will be able to access the Driver -- DriverModule from the AppComponent without us having to expose it explicitely. So we remove the ```getDriver()``` method.
```java
@Singleton
@Component(modules = DriverModule.class)
public interface AppComponent {
     //Driver getDriver()
}
```

What we need to do now though is add a provision method for our subComponent inside the mainComponent.
```java
@Singleton
@Component(modules = DriverModule.class)
public interface AppComponent {
     ActivityComponent getActivityComponent();
}
```
This method will also take as parameters ALL moduled of the subcomponent (ActivityComponent) that __1.are not abstract__ and __2.dont have default constructors__. 

ActivityComponent has WheelsModule (which is abstract, therefore takes no constructor parameters) and DieselEngineModule, which requires horsePower to it's construcor:
```java
@Module
public class DieselEngineModule {

    private int horsePower;

    public DieselEngineModule(int horsePower) {
        this.horsePower = horsePower;
    }

    @Provides
    int provideHorsePower() {
        return horsePower;
    }

    @Provides
    Engine provideEngine(DieselEngine dieselEngine) {
        return dieselEngine;
    }
}
```

so we will add it to the SubComponent provision method, so that we may pass the parameter to the Module constructor at runtime:

```java
@Singleton
@Component(modules = DriverModule.class)
public interface AppComponent {
    ActivityComponent getActivityComponent(DieselEngineModule dem);
}
```
This is called a _Factory Method_. 

Now in the MainActivity, we will retrieve the ActivityComponent this way:
```java
ActivityComponent component = ((MyAppplication)getApplication()).getAppComponent()
                .getActivityComponent(new DieselEngineModule(120));
```
We dont have to call build, since this method already returns the finished/built component.

