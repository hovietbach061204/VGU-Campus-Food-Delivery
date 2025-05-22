package com.example.deliver_tracking.controller;

import com.example.deliver_tracking.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routing")
@CrossOrigin(origins = "http://localhost:3000")
public class RoutingController {

    private final LocationService locationService;

    @Autowired
    public RoutingController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/estimate")
    public LocationService.EstimationResponse estimateRoute(
            @RequestParam double startLat,
            @RequestParam double startLon,
            @RequestParam double endLat,
            @RequestParam double endLon) {
                
        return locationService.estimateTravelTime(startLat, startLon, endLat, endLon);
    }
}