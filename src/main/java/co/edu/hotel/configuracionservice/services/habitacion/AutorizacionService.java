package co.edu.hotel.configuracionservice.services.habitacion;

import org.springframework.stereotype.Service;

@Service
public class AutorizacionService implements IAutorizacionService {
    @Override
    public boolean tieneRol(String username, String rol) {
        // Lógica mínima: admin.test es ADMIN
        return "ADMIN".equalsIgnoreCase(rol) && "admin.test".equals(username);
    }
}
