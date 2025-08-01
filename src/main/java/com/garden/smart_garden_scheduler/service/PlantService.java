package com.garden.smart_garden_scheduler.service;
import com.garden.smart_garden_scheduler.model.Plant;
import com.garden.smart_garden_scheduler.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.io.*;
import java.net.*;
import org.json.JSONObject;

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
        String weather = getWeatherFromAPI(city);

        for (Plant plant : plants) {
            StringBuilder message = new StringBuilder();
            message.append("Water every ").append(plant.getWateringIntervalDays()).append(" days.");
            if (weather.toLowerCase().contains("rain")) {
                message.append(" 🌧️ Rain expected – you can skip today's watering!");
            }
            schedule.put(plant.getName(), message.toString());
        }

        return schedule;
    }

    // 🔽 Place this OUTSIDE of any method, but INSIDE the class
    public String getWeatherFromAPI(String city) {

        String apiKey = "e7bf094abc9e499793e145152253107";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";

        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject json = new JSONObject(response.toString());
            String weather = json.getJSONArray("weather")
                    .getJSONObject(0)
                    .getString("description");

            return weather;

        } catch (Exception e) {
            e.printStackTrace();
            return "Unavailable";
        }
    }
}