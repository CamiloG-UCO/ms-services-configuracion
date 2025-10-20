package co.edu.hotel.configuracionservice.domain.habitacion;


public enum TipoHabitacion {

    STANDARD("Standard"),

    PREMIUM("Premium"),

    SUITE("Suite"),

    ECONOMICA("Economica"),

    EJECUTIVA("Ejecutiva");

    private final String descripcion;

    TipoHabitacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static TipoHabitacion fromDescripcion(String descripcion) {
        for (TipoHabitacion tipo : values()) {
            if (tipo.getDescripcion().equalsIgnoreCase(descripcion)) {
                return tipo;
            }
        }
        return null;
    }
}