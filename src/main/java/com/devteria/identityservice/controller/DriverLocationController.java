package com.devteria.identityservice.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/location")
public class DriverLocationController {

    private Map<String, double[]> driverLocations = new ConcurrentHashMap<>();

    @PostMapping("/update")
    public String updateLocation(@RequestParam String driverId,
                                 @RequestParam double latitude,
                                 @RequestParam double longitude) {
        driverLocations.put(driverId, new double[]{latitude, longitude});
        return "Location updated!";
    }

    @GetMapping("/get")
    public Map<String, double[]> getLocations() {
        return driverLocations;
    }

    @GetMapping("/get/{userId}")
    public double[] getLocation(@PathVariable String userId) {
        return driverLocations.getOrDefault(userId, new double[]{0.0, 0.0});
    }
}