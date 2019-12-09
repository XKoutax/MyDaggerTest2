# MyDaggerTest2

## 1. @Inject adnotation##

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
* either have the constructor injected as well (using the ```@Inject``` adnotation)
* either trigger the injection using the ```CarComponent.inject(MainAcitivty mainActivity);``` method in our main activity, letting dagger know to inject all the necessary field from our MainActivity class. This is especially useful when we can't use constructor injection (in Activities / Fragment for example)

- - - -

## 2. @Component adnotation##

Interfaces adnotated with ```@Component``` are what we are going to use in order to achieve our dependancy injection.
