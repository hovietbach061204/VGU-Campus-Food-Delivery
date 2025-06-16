package com.devteria.identityservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devteria.identityservice.service.LocationService;

@RestController
@RequestMapping("/api/routing")
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
