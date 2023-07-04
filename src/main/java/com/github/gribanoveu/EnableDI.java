package com.github.gribanoveu;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  * @author Evgeny Gribanov
 *  * @version 03.07.2023
 * Аннотация, с помощью которой необходимо помечать классы, чтобы они попали в сканирование.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableDI {
    String value() default "";
}
