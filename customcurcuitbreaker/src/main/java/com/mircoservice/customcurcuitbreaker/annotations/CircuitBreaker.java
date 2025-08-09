package com.mircoservice.customcurcuitbreaker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreaker {
    String name();
    int failureThreshHold() default 3;
    long resetTimeOut() default 10000; // it is in milliseconds
}
