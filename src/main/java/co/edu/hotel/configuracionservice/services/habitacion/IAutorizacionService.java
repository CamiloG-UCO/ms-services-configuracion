package co.edu.hotel.configuracionservice.services.habitacion;

public interface IAutorizacionService {
    boolean tieneRol(String username, String rol);
}
