package com.alumnos.AWSPF.repositories;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alumnos.AWSPF.schemas.ProfesorSchema;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioProfesor extends JpaRepository<ProfesorSchema, Integer> {
    Optional<ProfesorSchema> findById(int id);
    @NotNull
    List<ProfesorSchema> findAll();
}
