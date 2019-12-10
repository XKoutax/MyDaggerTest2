# MyDaggerTest2

In a nutshell, Dagger creates objects and provides them at the right time.  What we need to do is tell dagger __how__ to do it: 
* annotating the constructor of a class with ```@Inject``` (useful if we __own__ the class and can annotate it's constuctor)
* using the ```@Provides``` annotation on methods inside modules (classes annotated with ```@Module```)

## 1. @Inject annotation ##

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

## 2. @Component annotation ##

Interfaces annotated with ```@Component``` are what we are going to use in order to achieve our dependancy injection.

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

```java
@Component(modules = WheelsModule.class)
public interface CarComponent {

    Car getCar();
    ...
}
```

The WheelsModule class provides Rims and Tires, which are required in order to build a Wheels object. In turn, the getCar() method inside the CarComponent class requires Wheels in order to create an instance of Car. By adding the module to our component ( ```java @Component(modules = WheelsModule.class) ```), dagger knows that whenever CarComponent needs Rims, Tires or Wheels(in this example, we need Wheels in the getCar() method), it will get them from the WheelsModule.





- - - - 

## 3. @Module annotaton ##

A module is a class that contributes to the object graph (adds objects to the dependency graph, through the ```@Provides``` methods). Especially useful if we can't annotate ```@Inject``` on constructor, or if we require some configuration on a class __after__ instantiating it.

