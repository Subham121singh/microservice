package com.mircoservice.customcurcuitbreaker.controller;

import com.mircoservice.customcurcuitbreaker.annotations.CircuitBreaker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;
@RestController
@RequestMapping("/api")
public class TestController {
    private AtomicInteger counter = new AtomicInteger();

    @GetMapping("/unstable")
    @CircuitBreaker(name="unstableService", failureThreshHold = 3, resetTimeOut = 10000)
    public String unstableEndpoint(){
        int c = counter.incrementAndGet();

        // FAIL FIRST 3 CALLS, then succeed, then fail first 3 again, for testing
        // real life scenario we can use sliding window, or decide what approach needs to follow
        // depending upon the approac, it may need to update the circuitBreaker
        // and use datastructure accordingly
        if ((c - 1) % 6 < 3) {
            throw new RuntimeException("Simulated failure " + c);
        }
        return "Success #" + c;
    }

}
