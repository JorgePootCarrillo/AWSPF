package com.alumnos.AWSPF.services;

import java.util.*;

import org.springframework.stereotype.Service;

import com.alumnos.AWSPF.dto.ProfesorDTO;
import com.alumnos.AWSPF.dto.request.PreProfesorRequest;
import com.alumnos.AWSPF.exceptions.BusinessException;
import com.alumnos.AWSPF.repositories.RepositorioProfesor;
import com.alumnos.AWSPF.schemas.ProfesorSchema;

@Service
public class ProfesorServices {

    private final RepositorioProfesor RepositorioProfesor;

    public ProfesorServices(RepositorioProfesor RepositorioProfesor){
        this.RepositorioProfesor = RepositorioProfesor;
    }

    public List<ProfesorDTO> getProfesores(){
        return RepositorioProfesor
                .findAll()
                .stream()
                .map(ProfesorDTO::getFromSchema)
                .toList();
    }

    public ProfesorDTO getProfesorById(int id){
        Optional<ProfesorSchema> profesor = RepositorioProfesor
                .findById(id);
        return profesor.map(ProfesorDTO::getFromSchema).orElse(null);
    }

    public ProfesorDTO createProfesor(PreProfesorRequest profesorAux){
        if(RepositorioProfesor.findById(profesorAux.getId()).isPresent()){
            throw BusinessException
                    .builder()
                    .message("Mismo id")
                    .build();
        }

        ProfesorSchema profesorSchema = new ProfesorSchema();
        profesorSchema.setNombres(profesorAux.getNombres());
        profesorSchema.setApellidos(profesorAux.getApellidos());
        profesorSchema.setHorasClase(profesorAux.getHorasClase());
        profesorSchema.setNumeroEmpleado(profesorAux.getNumeroEmpleado());

        ProfesorSchema profesorGuadado = RepositorioProfesor.save(profesorSchema);

        return ProfesorDTO.getFromSchema(profesorGuadado);

    }

    public ProfesorDTO actualizar(int id, PreProfesorRequest profesorAux){
        Optional<ProfesorSchema> profesorExistente = RepositorioProfesor.findById(id);

        if (profesorExistente.isPresent()) {
            ProfesorSchema profesor = profesorExistente.get();
            profesor.setNumeroEmpleado(profesorAux.getNumeroEmpleado());
            profesor.setNombres(profesorAux.getNombres());
            profesor.setApellidos(profesorAux.getApellidos());
            profesor.setHorasClase(profesorAux.getHorasClase());

            ProfesorSchema profesorActualizado = RepositorioProfesor.save(profesor);

            return ProfesorDTO.getFromSchema(profesorActualizado);
        } else {
            return null;
        }
    }

    public ProfesorDTO deleteProfesor(int id){
        Optional<ProfesorSchema> profesorAEliminar = RepositorioProfesor.findById(id);

        if (profesorAEliminar.isPresent()) {
            RepositorioProfesor.deleteById(id);
            return ProfesorDTO.getFromSchema(profesorAEliminar.get());
        } else {
            return null;
        }
    }
}
