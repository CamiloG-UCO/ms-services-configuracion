package co.edu.hotel.configuracionservice.services.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;


public interface INotificacionModulosService {


    void notificarModuloReservas(Habitacion habitacion, String tipoOperacion);

    void notificarModuloTareas(Habitacion habitacion, String tipoOperacion);

    void notificarModuloClientes(Habitacion habitacion, String tipoOperacion);

    void notificarTodosLosModulos(Habitacion habitacion, String tipoOperacion);
}