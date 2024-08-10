package com.packt.cardatabase.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
    // JpaRepository already includes methods for all CRUD operations
}




