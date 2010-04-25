package com.byluroid.spring.beans.factory.config.annotations;


import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @author ricardo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Property {

    String key();
    String defaultValue() default "";
    boolean update() default false;

}
