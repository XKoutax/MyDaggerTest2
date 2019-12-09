# MyDaggerTest2

In a nutshell, Dagger creates objects and provides them at the right time.  What we need to do is tell dagger __how__ to do it: 
* annotating the constructor of a class with ```@Inject``` (useful if we __own__ the class and can annotate it's constuctor)
* using the ```@Provides``` annotation on methods inside modules (classes annotated with ```@Module```)

## 1. @Inject annotation ##

```@Inject``` lets Dagger know how to create classes
```java
@Inject
public Engine() {

}

...

@Inject
public Wheels() {

}
```

For it to work, ```@Inject``` must be put on fields that are __not__ private or final.

```@Inject``` can be put on constructor, field(variable) or method. In order for the field or method injection to work however, we must:  
* either have the constructor injected as well (using the ```@Inject``` annotation)
* either trigger the injection using the ```CarComponent.inject(MainAcitivty mainActivity);``` method in our main activity, letting dagger know to inject all the necessary field from our MainActivity class. This is especially useful when we can't use constructor injection (in Activities / Fragment for example)

- - - -

## 2. @Component annotation ##

Interfaces annotated with ```@Component``` are what we are going to use in order to achieve our dependancy injection.





- - - - 

## 3. @Module annotaton ##

A module is a class that contributes to the object graph (adds objects to the dependency graph, through the ```@Provides``` methods). Especially useful if we can't annotate ```@Inject``` on constructor, or if we require some configuration on a class __after__ instantiating it.

