package com.alumnos.AWSPF.repositories;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alumnos.AWSPF.schemas.AlumnoSchema;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioAlumno extends JpaRepository<AlumnoSchema, Integer>{
    @NotNull
    List<AlumnoSchema> findAll();
    Optional<AlumnoSchema> findById(int id);
}
