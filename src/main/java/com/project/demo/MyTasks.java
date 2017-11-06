package com.project.demo;

import org.apache.commons.text.RandomStringGenerator;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;

@Component
public class MyTasks {

    private int id = 1;
    RestTemplate restTemplate = new RestTemplate();

    /**
     * Adds a random vehicle every second, with randomly generated name, year, and price
     */
    @Scheduled(cron = "*/1 * * * * *")
    public void addVehicle() {
        Random random = new Random();
        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
        int randYear = random.nextInt((2017 - 1980) + 1) + 1980;
        double randPrice = (double) random.nextInt((80000 - 2000) + 1)  + 2000;

        Vehicle newVehicle = new Vehicle(id++, generator.generate(10), randYear, randPrice);
        restTemplate.postForObject("http://localhost:8080/addVehicle", newVehicle, Vehicle.class);
    }

    /**
     * deletes a random vehicle every 10 seconds, using the random id.
     */
    @Scheduled(cron = "*/10 * * * * *")
    public void deleteVehicle() {
        Random random = new Random();

        int randId = random.nextInt((100 - 1) + 1) + 1;

        restTemplate.delete("http://localhost:8080/deleteVehicle/" + randId, Vehicle.class);
        System.out.println("Deleted a random vehicle");
    }

    /**
     * updates a random vehicle every 15 seconds with new random information
     */
    @Scheduled(cron = "*/15 * * * * *")
    public void updateVehicle() {
        Random random = new Random();

        RandomStringGenerator generator = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
        int randYear = random.nextInt((2017 - 1980) + 1) + 1980;
        double randPrice = (double) random.nextInt((80000 - 2000) + 1)  + 2000;
        int randId = random.nextInt((100 - 1) + 1) + 1;

        Vehicle newVehicle = new Vehicle(id++, generator.generate(10), randYear, randPrice);
        restTemplate.postForObject("http://localhost:8080/updateVehicle" + randId, newVehicle, Vehicle.class);
        System.out.println("Updated random vehicle");
    }

    /**
     * returns the last 10 vehicles in the inventory at the top of every hour.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void latestVehiclesReport() {
        List<Vehicle> list = restTemplate.getForObject("http://localhost:8080/getLatestVehicles", List.class);
        System.out.println(list.toString());
    }
}
