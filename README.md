# MyDaggerTest2

##### DAGger - Directed Acyclic Graph  

In a nutshell, Dagger creates objects and provides them at the right time.  What we need to do is tell dagger __how__ to do it: 
* annotating the constructor of a class with ```@Inject``` (useful if we __own__ the class and can annotate it's constuctor)
* using the ```@Provides``` annotation on methods inside modules (classes annotated with ```@Module```)

## 1. @Inject 


```@Inject``` lets Dagger know how to create classes.

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
So dagger still doesn't have to instantiate it, which makes the code more efficient.  

So @BindsInstance with @Component.Builder should always be prefered over Module constructor arguments whenever possible.

