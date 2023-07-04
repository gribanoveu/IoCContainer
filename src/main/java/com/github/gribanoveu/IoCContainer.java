package com.github.gribanoveu;

import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Evgeny Gribanov
 * @version 03.07.2023
 * Автоматическое создание бинов (IoC и DI)
 * Контейнер сканирует текущий пакет и папки внутри на наличие классов, отмеченных @Container.Bean
 * при нахождении создается обьект, далее при нахождении конструкторов, в которые нужно внедрить
 * зависимость, она добавляется.
 * Для использования вызвать IoCContainer.createPicoContainer("org.example");
 * во время запуска приложения.
 */
public class IoCContainer {
    /**
     * Создать контейнер.
     * Найти классы с аннотацией и передать их PicoContainer для иньекции.
     * @return экземпляр MutablePicoContainer из которого можно получить конкретный
     * контейнер путем вызова getComponent(MyComponent.class);
     * Далее можно вызвать необходимые методы у этого класса.
     */
    public static MutablePicoContainer createPicoContainer() {
        try {
            String basePackage = IoCContainer.class.getPackageName();
            Set<Class<?>> annotatedClasses = findAnnotatedClasses(basePackage);
            MutablePicoContainer container = new DefaultPicoContainer();
            for (Class<?> clazz : annotatedClasses) container.addComponent(clazz);
            return container;
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException("error when find annotated classes");
        }
    }

    /**
     * Создать контейнер.
     * Найти классы с аннотацией и передать их PicoContainer для иньекции.
     * @return экземпляр MutablePicoContainer из которого можно получить конкретный
     * контейнер путем вызова getComponent(MyComponent.class);
     * Далее можно вызвать необходимые методы у этого класса.
     * @param basePackage - базовый пакет, откуда начать сканирование
     */
    public static MutablePicoContainer createPicoContainer(String basePackage) {
        try {
            Set<Class<?>> annotatedClasses = findAnnotatedClasses(basePackage);
            MutablePicoContainer container = new DefaultPicoContainer();
            for (Class<?> clazz : annotatedClasses) container.addComponent(clazz);
            return container;
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException("error when find annotated classes");
        }
    }

    /**
     * Найти Set из классов, которые имеют аннотацию @Container.Bean.
     * Пройти рекурсивно по всем пакетам и найти классы в которых есть аннотация.
     */
    private static Set<Class<?>> findAnnotatedClasses(String basePackage) throws IOException, URISyntaxException, ClassNotFoundException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        String path = basePackage.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        Set<Class<?>> classes = new HashSet<>();

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();

            File file = new File(resource.toURI());
            for (File classFile : findClassFiles(file)) {
                String fileName = classFile.getName();
                if (fileName.endsWith(".class")) {
                    Class<?> classObject = Class.forName(basePackage + "." + getPackageName(classFile));
                    if (classObject.isAnnotationPresent(EnableDI.class)) classes.add(classObject);
                }
            }
        }
        return classes;
    }

    private static List<File> findClassFiles(File dir) {
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .flatMap(file -> file.isDirectory() ? findClassFiles(file).stream() : Stream.of(file))
                .filter(file -> file.getName().endsWith(".class"))
                .collect(Collectors.toList());
    }

    private static String getPackageName(File classFile) {
        String prefix = IoCContainer.class.getPackageName().replace(".", File.separator) + File.separator;
        String path = classFile.getPath();
        return path.substring(path.indexOf(prefix) + prefix.length(), path.lastIndexOf(".")).replace(File.separator, ".");
    }

}
