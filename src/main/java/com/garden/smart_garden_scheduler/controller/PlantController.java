package com.garden.smart_garden_scheduler.controller;

import com.garden.smart_garden_scheduler.model.Plant;
import com.garden.smart_garden_scheduler.service.PlantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/plants")
public class PlantController {
    private final PlantService service;

    public PlantController(PlantService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public String addPlant(@RequestBody Plant plant) {
        service.savePlant(plant);
        return "✅ Plant added successfully!";
    }

    @GetMapping("/schedule")
    public Map<String, String> getSchedule(@RequestParam String city) {
        return service.getSchedule(city);
    }

    @GetMapping("/weather")
    public String getWeather(@RequestParam String city) {
        return service.getWeatherFromAPI(city); // ✅ Now fixed
    }
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("✅ Smart Garden Scheduler Backend is Running!");
    }

}
