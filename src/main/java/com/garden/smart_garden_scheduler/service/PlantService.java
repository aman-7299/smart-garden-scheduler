package com.garden.smart_garden_scheduler.service;
import com.garden.smart_garden_scheduler.model.Plant;
import com.garden.smart_garden_scheduler.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class PlantService {
    private final PlantRepository repository;

    public PlantService(PlantRepository repository) {
        this.repository = repository;
    }

    public Plant savePlant(Plant plant) {
        return repository.save(plant);
    }

    public List<Plant> getAllPlants() {
        return repository.findAll();
    }

    public Map<String, String> getSchedule(String city) {
        List<Plant> plants = repository.findAll();
        Map<String, String> schedule = new LinkedHashMap<>();
        String weather = getWeatherFromAPI(city); // dummy now

        for (Plant plant : plants) {
            String message = "Water every " + plant.getWateringIntervalDays() + " days.";
            if (weather.contains("rain")) {
                message = "Rain expected – Skip watering!";
            }
            schedule.put(plant.getName(), message);
        }
        return schedule;
    }

    public String getWeather(String city) {
        return getWeatherFromAPI(city);
    }

    // Dummy method for weather - you can later integrate real API
    private String getWeatherFromAPI(String city) {
        if (city.equalsIgnoreCase("chandigarh")) return "Rainy";
        return "Sunny";
    }
}

