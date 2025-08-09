package com.mircoservice.customcurcuitbreaker.aspect;

import com.mircoservice.customcurcuitbreaker.annotations.CircuitBreaker;
import com.mircoservice.customcurcuitbreaker.service.CircuitBreakerService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class CircuitBreakerAspect {
    private final CircuitBreakerService circuitBreakerService;

    public CircuitBreakerAspect(CircuitBreakerService circuitBreakerService) {
        this.circuitBreakerService = circuitBreakerService;
    }

    @Around("@annotation(circuitBreaker)")
    public Object handleCircuitBreaker(ProceedingJoinPoint pjp, CircuitBreaker circuitBreaker) throws Throwable{
        String name = circuitBreaker.name();
        int failureThreshhold = circuitBreaker.failureThreshHold();
        long resetTimeout = circuitBreaker.resetTimeOut();

        if(!circuitBreakerService.allowRequest(name, failureThreshhold, resetTimeout)){
            throw new RuntimeException("Circuit is OPEN for " + name);
        }
        try {
            Object result = pjp.proceed();
            circuitBreakerService.recordSuccess(name);
            return result;
        }
        catch (Throwable t){
            circuitBreakerService.recordFailure(name, failureThreshhold);
            throw t;
        }
    }
}
