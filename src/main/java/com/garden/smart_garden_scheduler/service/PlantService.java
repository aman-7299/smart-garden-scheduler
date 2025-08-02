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

    // ✅ New weather API integration using Open-Meteo (No API key required)
//    public String getWeatherFromAPI(String city) {
//        Map<String, double[]> coordinatesMap = Map.of(
//                "chandigarh", new double[]{30.7333, 76.7794},
//                "delhi", new double[]{28.6139, 77.2090},
//                "mumbai", new double[]{19.0760, 72.8777},
//                "bangalore", new double[]{12.9716, 77.5946},
//                "kolkata", new double[]{22.5726, 88.3639}
//        );
//
//        double[] coords = coordinatesMap.getOrDefault(city.toLowerCase(), null);
//
//        if (coords == null) {
//            return "City not supported";
//        }
//
//        String apiUrl = String.format(
//                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true",
//                coords[0], coords[1]
//        );
//
//        try {
//            URL url = new URL(apiUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(connection.getInputStream())
//            );
//
//            StringBuilder response = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//            reader.close();
//
//            JSONObject json = new JSONObject(response.toString());
//            JSONObject currentWeather = json.getJSONObject("current_weather");
//
//            double code = currentWeather.getDouble("weathercode");
//
//            // Simple weather description mapping based on weathercode (not exact, but decent)
//            String description = switch ((int) code) {
//                case 0 -> "clear sky";
//                case 1, 2, 3 -> "partly cloudy";
//                case 45, 48 -> "foggy";
//                case 51, 53, 55 -> "light drizzle";
//                case 61, 63, 65 -> "rain";
//                case 66, 67 -> "freezing rain";
//                case 71, 73, 75, 77 -> "snow";
//                case 80, 81, 82 -> "rain showers";
//                case 95, 96, 99 -> "thunderstorm";
//                default -> "unknown";
//            };
//
//            return description;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Unavailable";
//        }
//    }
//}
    public String getWeatherFromAPI(String city) {
        Map<String, double[]> coordinatesMap = Map.ofEntries(
                Map.entry("chandigarh", new double[]{30.7333, 76.7794}),
                Map.entry("delhi", new double[]{28.6139, 77.2090}),
                Map.entry("mumbai", new double[]{19.0760, 72.8777}),
                Map.entry("bangalore", new double[]{12.9716, 77.5946}),
                Map.entry("kolkata", new double[]{22.5726, 88.3639}),
                Map.entry("chennai", new double[]{13.0827, 80.2707}),
                Map.entry("hyderabad", new double[]{17.3850, 78.4867}),
                Map.entry("ahmedabad", new double[]{23.0225, 72.5714}),
                Map.entry("pune", new double[]{18.5204, 73.8567}),
                Map.entry("jaipur", new double[]{26.9124, 75.7873}),
                Map.entry("lucknow", new double[]{26.8467, 80.9462}),
                Map.entry("kanpur", new double[]{26.4499, 80.3319}),
                Map.entry("nagpur", new double[]{21.1458, 79.0882}),
                Map.entry("visakhapatnam", new double[]{17.6868, 83.2185}),
                Map.entry("bhopal", new double[]{23.2599, 77.4126}),
                Map.entry("patna", new double[]{25.5941, 85.1376}),
                Map.entry("indore", new double[]{22.7196, 75.8577}),
                Map.entry("vadodara", new double[]{22.3072, 73.1812}),
                Map.entry("ludhiana", new double[]{30.9010, 75.8573}),
                Map.entry("agra", new double[]{27.1767, 78.0081}),
                Map.entry("nashik", new double[]{19.9975, 73.7898}),
                Map.entry("meerut", new double[]{28.9845, 77.7064}),
                Map.entry("rajkot", new double[]{22.3039, 70.8022}),
                Map.entry("varanasi", new double[]{25.3176, 82.9739}),
                Map.entry("srinagar", new double[]{34.0837, 74.7973}),
                Map.entry("amritsar", new double[]{31.6340, 74.8723}),
                Map.entry("ranchi", new double[]{23.3441, 85.3096}),
                Map.entry("guwahati", new double[]{26.1445, 91.7362}),
                Map.entry("dehradun", new double[]{30.3165, 78.0322}),
                Map.entry("trivandrum", new double[]{8.5241, 76.9366})
        );

        double[] coords = coordinatesMap.getOrDefault(city.toLowerCase(), null);

        if (coords == null) {
            return "City not supported";
        }

        String apiUrl = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current_weather=true",
                coords[0], coords[1]
        );

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
            JSONObject currentWeather = json.getJSONObject("current_weather");

            double code = currentWeather.getDouble("weathercode");

            String description = switch ((int) code) {
                case 0 -> "clear sky";
                case 1, 2, 3 -> "partly cloudy";
                case 45, 48 -> "foggy";
                case 51, 53, 55 -> "light drizzle";
                case 61, 63, 65 -> "rain";
                case 66, 67 -> "freezing rain";
                case 71, 73, 75, 77 -> "snow";
                case 80, 81, 82 -> "rain showers";
                case 95, 96, 99 -> "thunderstorm";
                default -> "unknown";
            };

            return description;

        } catch (Exception e) {
            e.printStackTrace();
            return "Unavailable";
        }
    }
}

