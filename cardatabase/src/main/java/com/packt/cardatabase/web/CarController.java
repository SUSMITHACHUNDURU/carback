package com.packt.cardatabase.web;

import com.packt.cardatabase.domain.Car;
import com.packt.cardatabase.domain.CarRepository;
import com.packt.cardatabase.domain.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CarController {

    @Autowired
    private CarRepository carRepository;

    // GET: Retrieve all cars
    @GetMapping("/cars")
    public ResponseEntity<CollectionModel<EntityModel<Car>>> getAllCars() {
        List<EntityModel<Car>> carModels = carRepository.findAll().stream()
            .map(car -> EntityModel.of(car,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getCarById(car.getId())).withSelfRel(),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getOwnerByCarId(car.getId())).withRel("owner")))
            .collect(Collectors.toList());

        CollectionModel<EntityModel<Car>> collectionModel = CollectionModel.of(carModels);
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getAllCars()).withSelfRel());
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getProfile()).withRel("profile"));

        return ResponseEntity.ok(collectionModel);
    }

    // GET: Retrieve a car by its ID
    @GetMapping("/cars/{id}")
    public EntityModel<Car> getCarById(@PathVariable Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        return EntityModel.of(car,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getCarById(id)).withSelfRel(),
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getOwnerByCarId(id)).withRel("owner"));
    }

    // POST: Create a new car
    @PostMapping("/cars")
    public ResponseEntity<EntityModel<Car>> createCar(@RequestBody Car car) {
        Car savedCar = carRepository.save(car);
        EntityModel<Car> carModel = EntityModel.of(savedCar,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getCarById(savedCar.getId())).withSelfRel());
        return new ResponseEntity<>(carModel, HttpStatus.CREATED);
    }

    // PUT: Update an existing car
    @PutMapping("/cars/{id}")
    public ResponseEntity<EntityModel<Car>> updateCar(@PathVariable Long id, @RequestBody Car carDetails) {
        Car car = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        car.setBrand(carDetails.getBrand());
        car.setModel(carDetails.getModel());
        car.setColor(carDetails.getColor());
        car.setRegisterNumber(carDetails.getRegisterNumber());
        car.setYear(carDetails.getYear());
        car.setPrice(carDetails.getPrice());
        car.setOwner(carDetails.getOwner());

        Car updatedCar = carRepository.save(car);
        EntityModel<Car> carModel = EntityModel.of(updatedCar,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getCarById(updatedCar.getId())).withSelfRel());
        return ResponseEntity.ok(carModel);
    }

    // DELETE: Delete a car by its ID
    @DeleteMapping("/cars/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        carRepository.delete(car);
        return ResponseEntity.noContent().build();
    }

    // GET: Retrieve the owner of a car by the car's ID
    @GetMapping("/cars/{id}/owner")
    public EntityModel<Owner> getOwnerByCarId(@PathVariable Long id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        Owner owner = car.getOwner();
        return EntityModel.of(owner,
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getOwnerByCarId(id)).withSelfRel());
    }

    // GET: Retrieve profile information
    @GetMapping("/profile/cars")
    public EntityModel<String> getProfile() {
        return EntityModel.of("Car Profile",
            WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CarController.class).getAllCars()).withRel("cars"));
    }
}


