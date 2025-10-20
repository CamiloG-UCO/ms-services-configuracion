package co.edu.hotel.configuracionservice.domain.habitacion;


public enum EstadoHabitacion {
    
    ACTIVO("Activo"),

    INACTIVO("Inactivo");
    
    private final String descripcion;

    EstadoHabitacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean permiteReservas() {
        return this == ACTIVO;
    }
}