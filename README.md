# Контейнер для использования DI на базе PicoContainer.

Автоматически сканирует классы, на которых есть аннотация `@EnableDI` для добавления их в PicoContainer.
Для использования нужно вызвать метод `createPicoContainer()` и передать аргументом имя базового пакета, откуда начать сканирование.
```java
public class Main {
    public static void main(String[] args) {
        IoCContainer.createPicoContainer("org.example");
    }
}
```
Или вызвать метод без параметров, сканирование начнется от места, где находится класс IoCContainer и далее внутрь пакетов.

Метод возвращает MutablePicoContainer, из-за чего можно получить определенный контейнер и вызвать его методы.
```java
public class Main {
    public static void main(String[] args)  {
        MutablePicoContainer pico = IoCContainer.createPicoContainer("org.example");
        AutoPilot pilot = pico.getComponent(AutoPilot.class);
        pilot.drive();
    }
}
```
---
Пример работы:
```java
public interface Vehicle {
    void start();
    int status();
}
```

```java
import com.github.gribanoveu.EnableDI;

@EnableDI
public class Ford implements Vehicle {
    @Override
    public void start() {
        System.out.println("ford start");
    }

    @Override
    public int status() {
        return 1;
    }
}
```

```java
import com.github.gribanoveu.EnableDI;

@EnableDI
public class Driver {
    private final Vehicle vehicle;

    public Driver(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void start() {
        ford.start();
    }
}
```

```java
import com.github.gribanoveu.EnableDI;

@EnableDI
public class AutoPilot {
    private final Vehicle vehicle;
    private final Driver driver;

    public AutoPilot(Vehicle vehicle, Driver driver) {
        this.vehicle = vehicle;
        this.driver = driver;
    }

    public void drive() {
        int status = this.vehicle.status();
        if (status == 1) {
            this.driver.start();
        } else {
            System.out.println("car not work");
        }
    }
}
```
```java
public class Main {
    public static void main(String[] args) {
        MutablePicoContainer pico = IoCContainer.createPicoContainer("org.example");
        AutoPilot pilot = pico.getComponent(AutoPilot.class);
        pilot.drive();
    }
}
```
```
$ ford start
```
---
После вызова `IoCContainer.createPicoContainer()` бины находятся в контейнере и готовы для внедрения через конструктов, где есть аннотация `@EnableDI`
---
Использование lombok для внедрения бинов, вместо конструкторов
```java
import com.github.gribanoveu.EnableDI;

@EnableDI
@AllArgsConstructor
public class AutoPilot {
    private final Vehicle vehicle;
    private final Driver driver;

    public void drive() {
        int status = this.vehicle.status();
        if (status == 1) {
            this.driver.start();
        } else {
            System.out.println("car not work");
        }
    }
}
```
