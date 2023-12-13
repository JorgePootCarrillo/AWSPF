package com.alumnos.AWSPF.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.alumnos.AWSPF.dto.AlumnoDTO;
import com.alumnos.AWSPF.dto.request.PreAlumnoRequest;
import com.alumnos.AWSPF.entity.Sesion;
import com.alumnos.AWSPF.services.AlumnoServices;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alumnos")
@CrossOrigin(origins = {"*"})
public class AlumnoController {
    private final AlumnoServices AlumnoServices;

    public AlumnoController(AlumnoServices AlumnoServices){
        this.AlumnoServices = AlumnoServices;
    }

    @GetMapping("")
    @Operation(summary = "Listar a todos los alumnos")
    public ResponseEntity<List<AlumnoDTO>> getAllAlumnos() {
        List<AlumnoDTO> alumnos = this.AlumnoServices.getAlumnos();
        return new ResponseEntity<>(alumnos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar a un alumno segpun su id")
    public ResponseEntity<AlumnoDTO> getAlumnoById(@PathVariable int id){
        AlumnoDTO newAlumno = this.AlumnoServices.getAlumnoById(id);
        if(newAlumno == null){
            return new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(newAlumno,HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo alumno")
    public ResponseEntity<?> createAlumno(@Valid @RequestBody PreAlumnoRequest info){
        AlumnoDTO newAlumno = this.AlumnoServices.createAlumno(info);
        return new ResponseEntity<>(newAlumno, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar alumno existente")
    public ResponseEntity<AlumnoDTO> editAlumnoByid(@Valid @RequestBody PreAlumnoRequest info, @PathVariable int id){
        AlumnoDTO newAlumno = this.AlumnoServices.actualizar(id,info);
        return new ResponseEntity<>(newAlumno,HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un estudiante según su ID")
    public ResponseEntity<AlumnoDTO> delete(@PathVariable int id){
        AlumnoDTO alumnoAEliminar = this.AlumnoServices.deleteAlumno(id);

        if (alumnoAEliminar != null) {
            return new ResponseEntity<>(alumnoAEliminar, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/session/login")
    @Operation(summary = "Crear sesión nueva")
    public ResponseEntity<?> createSesion(@PathVariable int id, @RequestBody Map<String, Object> body){
        String password = (String) body.get("password");
        Sesion sesion = this.AlumnoServices.createSesion(id, password);
        if(sesion != null){
            return new ResponseEntity<>(sesion, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/{id}/session/verify")
    @Operation(summary = "Verificar sesion existente")
    public ResponseEntity<?> verifySesion(@PathVariable int id, @RequestBody Sesion sessionString){
        Sesion sesion = this.AlumnoServices.verifySesion(sessionString.getSessionString());
        if(sesion != null){
            return new ResponseEntity<>(sesion, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(sessionString,HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/session/logout")
    @Operation(summary = "Cerrar sesion")
    public ResponseEntity<?> logut(@PathVariable int id,@RequestBody Sesion sessionString){
        Sesion sesion = this.AlumnoServices.logOut(sessionString.getSessionString());
        if(sesion != null){
            return new ResponseEntity<>(sesion, HttpStatus.OK);
        }else {
            return new ResponseEntity<>(sessionString, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/{id}/fotoPerfil")
    @Operation(summary = "Subir foto de perfil alumno")
    public ResponseEntity<?> uploadPhotho(@PathVariable int id,@RequestParam("foto") MultipartFile file){
        AlumnoDTO alumnoFoto = this.AlumnoServices.uploadPhoto(id, file);
        return new ResponseEntity<>(alumnoFoto, HttpStatus.OK);
    }

    @PostMapping("/{id}/email")
    @Operation(summary = "Enviar correo")
    public ResponseEntity<?> sendEmail(@PathVariable int id){
        AlumnoDTO alumnoFoto = this.AlumnoServices.sendEmail(id);
        if (alumnoFoto != null){
            return new ResponseEntity<>(alumnoFoto, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

}
