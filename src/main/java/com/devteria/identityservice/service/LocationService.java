package com.devteria.identityservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationService {

    @Value("${graphhopper.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LocationService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        System.out.println("GraphHopper API Key: " + apiKey);
    }

    public EstimationResponse estimateTravelTime(double startLat, double startLon,
                                                 double endLat, double endLon) {
        String url = String.format(
                "https://graphhopper.com/api/1/route" +
                        "?point=%f,%f" +
                        "&point=%f,%f" +
                        "&vehicle=car" +
                        "&locale=en" +
                        "&calc_points=true" +
                        "&points_encoded=false" +
                        "&key=%s",
                startLat, startLon, endLat, endLon, apiKey
        );

        RouteResponse response = restTemplate.getForObject(url, RouteResponse.class);

        if (response == null || response.getPaths() == null || response.getPaths().length == 0) {
            throw new RuntimeException("No route found");
        }

        Path path = response.getPaths()[0];
        double distanceInMeters = path.getDistance();
        long timeInMillis = path.getTime();

        // Convert points to GeoJSON format
        String geoJson = convertPointsToGeoJson(path.getPoints());

        return new EstimationResponse(
                distanceInMeters / 1000, // Convert to km
                timeInMillis / 60000.0,  // Convert to minutes
                geoJson
        );
    }

    private String convertPointsToGeoJson(Points points) {
        try {
            Map<String, Object> geoJson = new HashMap<>();
            geoJson.put("type", "LineString");

            List<List<Double>> coordinates = new ArrayList<>();
            for (int i = 0; i < points.getCoordinates().length; i++) {
                List<Double> point = new ArrayList<>();
                point.add(points.getCoordinates()[i][0]); // longitude
                point.add(points.getCoordinates()[i][1]); // latitude
                coordinates.add(point);
            }

            geoJson.put("coordinates", coordinates);
            return objectMapper.writeValueAsString(geoJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert points to GeoJSON", e);
        }
    }

    // Inner classes for GraphHopper API response
    public static class RouteResponse {
        private Path[] paths;

        public Path[] getPaths() { return paths; }
        public void setPaths(Path[] paths) { this.paths = paths; }
    }

    public static class Path {
        private double distance;
        private long time;
        private Points points;

        public double getDistance() { return distance; }
        public void setDistance(double distance) { this.distance = distance; }
        public long getTime() { return time; }
        public void setTime(long time) { this.time = time; }
        public Points getPoints() { return points; }
        public void setPoints(Points points) { this.points = points; }
    }

    public static class Points {
        private double[][] coordinates;

        public double[][] getCoordinates() { return coordinates; }
        public void setCoordinates(double[][] coordinates) { this.coordinates = coordinates; }
    }

    public static class EstimationResponse {
        private double distanceKm;
        private double timeMinutes;
        private String routeGeometry;

        public EstimationResponse(double distanceKm, double timeMinutes, String routeGeometry) {
            this.distanceKm = distanceKm;
            this.timeMinutes = timeMinutes;
            this.routeGeometry = routeGeometry;
        }

        public double getDistanceKm() { return distanceKm; }
        public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
        public double getTimeMinutes() { return timeMinutes; }
        public void setTimeMinutes(double timeMinutes) { this.timeMinutes = timeMinutes; }
        public String getRouteGeometry() { return routeGeometry; }
        public void setRouteGeometry(String routeGeometry) { this.routeGeometry = routeGeometry; }
    }
}