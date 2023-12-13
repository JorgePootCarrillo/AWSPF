package com.alumnos.AWSPF.services;

import org.springframework.stereotype.Service;

import com.alumnos.AWSPF.entity.Sesion;
import com.alumnos.AWSPF.repositories.RepositorioSesion;

import java.util.UUID;

@Service
public class SesionService {
    private final RepositorioSesion RepositorioSesion;

    public SesionService(RepositorioSesion RepositorioSesion) {
        this.RepositorioSesion = RepositorioSesion;
    }

    public Sesion createSesion(Sesion sesion){
        String uuid = UUID.randomUUID().toString();
        sesion.setId(uuid);
        sesion.setActive(true);
        sesion.setFecha(System.currentTimeMillis());
        RepositorioSesion.save(sesion);
        return sesion;
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
}
