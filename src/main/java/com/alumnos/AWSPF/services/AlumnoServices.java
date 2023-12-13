package com.alumnos.AWSPF.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alumnos.AWSPF.dto.AlumnoDTO;
import com.alumnos.AWSPF.dto.request.PreAlumnoRequest;
import com.alumnos.AWSPF.entity.Sesion;
import com.alumnos.AWSPF.exceptions.BusinessException;
import com.alumnos.AWSPF.repositories.RepositorioAlumno;
import com.alumnos.AWSPF.repositories.RepositorioBucket;
import com.alumnos.AWSPF.repositories.RepositorioSNS;
import com.alumnos.AWSPF.repositories.RepositorioSesion;
import com.alumnos.AWSPF.schemas.AlumnoSchema;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
@Service
public class AlumnoServices {
    private final RepositorioAlumno RepositorioAlumno;
    private final RepositorioSesion RepositorioSesion;
    private final RepositorioBucket RepositorioBucket;
    private final RepositorioSNS RepositorioSNS;

    public AlumnoServices(RepositorioAlumno RepositorioAlumno, RepositorioSesion RepositorioSesion, RepositorioBucket RepositorioBucket, RepositorioSNS RepositorioSNS) {
        this.RepositorioAlumno = RepositorioAlumno;
        this.RepositorioSesion = RepositorioSesion;
        this.RepositorioBucket = RepositorioBucket;
        this.RepositorioSNS = RepositorioSNS;
    }

    public List<AlumnoDTO> getAlumnos() {
        return RepositorioAlumno
                .findAll()
                .stream()
                .map(AlumnoDTO::getFromSchema)
                .toList();
    }

    public AlumnoDTO getAlumnoById(int id){
        Optional<AlumnoSchema> alumno = RepositorioAlumno
                .findById(id);
        return alumno.map(AlumnoDTO::getFromSchema).orElse(null);
    }

    public AlumnoDTO createAlumno(PreAlumnoRequest alumnoAux){
        if(RepositorioAlumno.findById(alumnoAux.getId()).isPresent()){
            throw BusinessException
                    .builder()
                    .message("Mismo id")
                    .build();
        }

        AlumnoSchema alumnoSchema = new AlumnoSchema();
        alumnoSchema.setNombres(alumnoAux.getNombres());
        alumnoSchema.setApellidos(alumnoAux.getApellidos());
        alumnoSchema.setMatricula(alumnoAux.getMatricula());
        alumnoSchema.setPromedio(alumnoAux.getPromedio());
        alumnoSchema.setPassword(alumnoAux.getPassword());

        AlumnoSchema alumnoGuardado = RepositorioAlumno.save(alumnoSchema);

        return AlumnoDTO.getFromSchema(alumnoGuardado);
    }

    public AlumnoDTO actualizar(int id, PreAlumnoRequest alumnoAux){
        Optional<AlumnoSchema> alumnoExistente = RepositorioAlumno.findById(id);
        if (alumnoExistente.isPresent()) {
            AlumnoSchema alumno = alumnoExistente.get();
            alumno.setNombres(alumnoAux.getNombres());
            alumno.setApellidos(alumnoAux.getApellidos());
            alumno.setMatricula(alumnoAux.getMatricula());
            alumno.setPromedio(alumnoAux.getPromedio());

            AlumnoSchema alumnoUpdated = RepositorioAlumno.save(alumno);

            return AlumnoDTO.getFromSchema(alumnoUpdated);
        } else {
            return null;
        }
    }

    public AlumnoDTO deleteAlumno(int id){
        Optional<AlumnoSchema> alumnoAEliminar = RepositorioAlumno.findById(id);
        if (alumnoAEliminar.isPresent()) {
            RepositorioAlumno.deleteById(id);
            System.out.println("Alumno con ID " + id + " eliminado correctamente.");
            return AlumnoDTO.getFromSchema(alumnoAEliminar.get());
        } else {
            System.out.println("No se encontró ningún alumno con el ID: " + id);
            return null;
        }
    }

    public Sesion createSesion(int alumnoId,String info){
        Optional<AlumnoSchema> alumnoOptional = RepositorioAlumno.findById(alumnoId);
        Sesion sesion = new Sesion();
        if(alumnoOptional.isPresent()) {
            AlumnoSchema alumno = alumnoOptional.get();

            // Compara las contraseñas
            if (info.equals(alumno.getPassword())) {
                String uuid = UUID.randomUUID().toString();
                sesion.setId(uuid);
                sesion.setAlumnoId(alumnoId);  // Asigna el alumno a la sesión si es necesario
                sesion.setActive(Boolean.TRUE);
                sesion.setFecha(System.currentTimeMillis());
                sesion.setSessionString(getRandomString(128));
                RepositorioSesion.save(sesion);
                return sesion;
            } else {
                // Contraseña incorrecta
                return null;
            }
        } else {
            // No se encontró al alumno con el ID proporcionado
            return null;
        }
    }

    public Sesion verifySesion(String sessionString){
        Sesion sesionAux = RepositorioSesion.getSessionBySessionString(sessionString);
        if (sesionAux != null && sesionAux.getActive()) {
            return sesionAux;
        }
        return null;
    }

    public Sesion logOut(String sessionString){
        Sesion sesionAux = RepositorioSesion.getSessionBySessionString(sessionString);
        if (sesionAux != null && sesionAux.getActive()) {
            sesionAux.setActive(false);
            RepositorioSesion.save(sesionAux);
            return sesionAux;
        }
        return null;
    }

    public static String getRandomString(int length) {

        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        return  random.ints(leftLimit, rightLimit + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public AlumnoDTO uploadPhoto(int id, MultipartFile file){
        String filename = file.getOriginalFilename();
        try {
            File tempFile = convertMultiPartToFile(file);
            Optional<AlumnoSchema> alumnoExistente = RepositorioAlumno.findById(id);
            if (alumnoExistente.isPresent()) {
                AlumnoSchema alumno = alumnoExistente.get();
                alumno.setFotoPerfilUrl("https://s3.amazonaws.com/a17000706/"+filename);
                AlumnoSchema alumnoUpdated = RepositorioAlumno.save(alumno);
                RepositorioBucket.uploadFileToS3( "a17000706", filename,tempFile);
                return AlumnoDTO.getFromSchema(alumnoUpdated);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public AlumnoDTO sendEmail(int id){
        Optional<AlumnoSchema> alumnoExistente = RepositorioAlumno.findById(id);
        if (alumnoExistente.isPresent()) {
            AlumnoSchema alumno = alumnoExistente.get();
            RepositorioSNS.sendEmailToTopic("Promedio: "+alumno.getPromedio()+"\nNombres: "+ alumno.getNombres()+"\nApellidos: "+alumno.getApellidos(),"sicei");
            return AlumnoDTO.getFromSchema(alumno);
        }else{
            return null;
        }
    }
}
