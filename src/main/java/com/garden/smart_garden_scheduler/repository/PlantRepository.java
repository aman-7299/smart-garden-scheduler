package com.garden.smart_garden_scheduler.repository;

import com.garden.smart_garden_scheduler.model.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  PlantRepository extends JpaRepository<Plant, Long> {
}
