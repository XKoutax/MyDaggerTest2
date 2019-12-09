# MyDaggerTest2

```@Inject``` lets Dagger know how to create these classes
```java
@Inject
public Engine() {

}

...

@Inject
public Wheels() {

}
```
```Inject``` can be put on constructor, field(variable) or method. In order for the field or method injection to work however, we must:\
* either have the constructor injected as well (using the ```@Inject``` adnotation)
* either trigger the injection using the ```CarComponent.inject(MainAcitivty mainActivity);``` method in our main activity, letting dagger know to inject all the necessary field from our MainActivity class. This is especially useful when we can't use constructor injection (in Activities / Fragment for example)


