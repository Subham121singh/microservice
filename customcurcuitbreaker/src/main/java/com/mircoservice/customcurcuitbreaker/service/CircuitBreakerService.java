package com.mircoservice.customcurcuitbreaker.service;

import com.mircoservice.customcurcuitbreaker.enums.State;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class CircuitBreakerService {
    private static class CircuitState{
        int failureCount = 0;
        long lastFailureTime = 0;
        State state = State.CLOSED;
    }

    private final ConcurrentHashMap<String, CircuitState> circuits = new ConcurrentHashMap<>();

    public synchronized boolean allowRequest(String name, int failureThreshold, long resetTimeout){
        CircuitState circuit = circuits.computeIfAbsent(name, k-> new CircuitState());

        if(circuit.state == State.OPEN){
            if(System.currentTimeMillis() - circuit.lastFailureTime > resetTimeout){
                circuit.state = State.HALF_OPEN;
                return true;
            }
            return false;
        }
        return true;
    }

    public synchronized void recordSuccess(String name){
        CircuitState circuit = circuits.computeIfAbsent(name, k -> new CircuitState());
        circuit.failureCount = 0;
        circuit.state = State.CLOSED;
    }

    public synchronized void recordFailure(String name, int failureThreshold){
        CircuitState circuit = circuits.computeIfAbsent(name, k -> new CircuitState());
        circuit.failureCount++;
        if(circuit.failureCount>=failureThreshold){
            circuit.state = State.OPEN;
            circuit.lastFailureTime = System.currentTimeMillis();
        }
    }
}
