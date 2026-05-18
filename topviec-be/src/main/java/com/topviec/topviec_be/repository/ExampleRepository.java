package com.topviec.topviec_be.repository;

import com.topviec.topviec_be.entity.ExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExampleRepository extends JpaRepository<ExampleEntity, Long> {
}
