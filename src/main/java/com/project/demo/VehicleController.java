package com.project.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

@RestController
public class VehicleController {

    /**
     * Adds a vehicle into the file containing the inventory of vehicles
     * @param newVehicle vehicle that is going to be added into the inventory
     * @return returns the new vehicle
     * @throws IOException in case the file doesn't exist, or cant be made
     */
    @RequestMapping(value = "/addVehicle", method = RequestMethod.POST)
    public Vehicle addVehicle(@RequestBody Vehicle newVehicle) throws IOException {
        //ObjectMapper provides functionality for reading and writing JSON
        ObjectMapper mapper = new ObjectMapper();

        //Create a FileWrite to write to inventory.txt and APPEND mode is true
        FileWriter output = new FileWriter("./inventory.txt", true);

        //serialize greeting object to JSON and write it to file
        mapper.writeValue(output, newVehicle);

        //Append a new line character to the file
        //The above FileWriter ("output") is automatically closed by the mapper.
        FileUtils.writeStringToFile(new File("./inventory.txt"),
                System.lineSeparator(),     //newline string
                StandardCharsets.UTF_8,     //encoding type
                true);              //Append mode is true
        return newVehicle;
    }

    /**
     * Returns the vehicle with the given id
     * @param id id that is used to identify which vehicle will be returned
     * @return returns vehicle with the given id
     * @throws IOException in case the file doesn't exist, or cant be made
     */
    @RequestMapping(value = "/getVehicle/{id}", method = RequestMethod.GET)
    public Vehicle getVehicle(@PathVariable("id") int id) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("./inventory.txt");
        Scanner scanner = new Scanner(file);

        Vehicle foundVehicle = new Vehicle();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            Vehicle tempVehicle = mapper.readValue(line, Vehicle.class);
            if (tempVehicle.getId() == id) {
                foundVehicle = tempVehicle;
            }
        }

        return foundVehicle;
    }

    /**
     * scans the inventory of vehicles, finding the one with the same id as new vehicle and then updating the info
     * @param newVehicle vehicle that will replace the current vehicle in the list
     * @return the new vehicle that was updated in the list
     * @throws IOException in case the file doesn't exist, or cant be made
     */
    @RequestMapping(value = "/updateVehicle", method = RequestMethod.PUT)
    public Vehicle updateVehicle(@RequestBody Vehicle newVehicle) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        List<String> list = FileUtils.readLines(new File("./inventory.txt"), StandardCharsets.UTF_8);

        for (int i = 0; i < list.size(); i++) {
            Vehicle tempVehicle = mapper.readValue(list.get(i), Vehicle.class);
            if (newVehicle.getId() == tempVehicle.getId()) {
                list.set(i, "{\"id\":" + newVehicle.getId() + ",\"makeModel\":\"" + newVehicle.getMakeModel() + "\",\"year\":"
                        + newVehicle.getYear() + ",\"retailPrice\":" + newVehicle.getRetailPrice() + "}");
            }
        }
        FileUtils.writeLines(new File("./inventory.txt"), list);
        return newVehicle;
    }

    /**
     * scans through the inventory and deletes the vehicle with the given id
     * @param id to identify which vehicle is being deleted
     * @return a response entity that says whether it was deleted or not found
     * @throws IOException in case the file doesn't exist, or cant be made
     */
    @RequestMapping(value = "/deleteVehicle/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteVehicle(@PathVariable("id") int id) throws IOException {

        if(getVehicle(id) != null) {
            ObjectMapper mapper = new ObjectMapper();

            List<String> list = FileUtils.readLines(new File("./inventory.txt"), StandardCharsets.UTF_8);

            for (int i = 0; i < list.size(); i++) {
                Vehicle tempVehicle = mapper.readValue(list.get(i), Vehicle.class);
                if (tempVehicle.getId() == id) {
                    list.remove(i);
                }
            }
            FileUtils.writeLines(new File("./inventory.txt"), list);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not Found", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * gets the last 10 vehicles that were added into the inventory.
     * @return the list of the last 10 vehicles in the inventory
     * @throws IOException in case the file doesn't exist, or cant be made
     */
    @RequestMapping(value = "/getLatestVehicles", method = RequestMethod.GET)
    public List<Vehicle> getLatestVehicles() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        List<String> list = FileUtils.readLines(new File("./inventory.txt"), StandardCharsets.UTF_8);
        List<Vehicle> vehicleList = new ArrayList<>();

        Collections.reverse(list);
        for (int i = 0; i  < 10; i++) {
            Vehicle tempVehicle = mapper.readValue(list.get(i), Vehicle.class);
            vehicleList.add(tempVehicle);
        }
        return vehicleList;
    }
}
