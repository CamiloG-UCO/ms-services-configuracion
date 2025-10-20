# language: es
Característica: Estado operativo de la habitacion
  Como administrador
  Quiero desactivar una habitacion por mantenimiento general
  Para bloquear temporalmente las reservas en ese hotel

  Escenario: Desactivar habitacion
    Dado el hotel "Santa Marta Resort" con estado actual "Activo"
    Cuando el administrador seleccione "Desactivar por mantenimiento"
    Entonces el sistema debe cambiar su estado a "Inactivo"
    Y notificar a todos los módulos dependientes (reservas, tareas, clientes)

  Escenario: Intentar desactivar habitacion ya inactiva
    Dado el hotel "Santa Marta Resort" con estado actual "Inactivo"
    Cuando el administrador seleccione "Desactivar por mantenimiento"
    Entonces el sistema debe mostrar un error indicando que ya está inactiva

  Escenario: Reactivar habitacion después del mantenimiento
    Dado el hotel "Santa Marta Resort" con estado actual "Inactivo"
    Cuando el administrador seleccione "Reactivar habitacion"
    Entonces el sistema debe cambiar su estado a "Activo"
    Y permitir nuevamente las reservas

  Escenario: Usuario sin permisos intenta desactivar habitacion
    Dado un usuario con rol "CUSTOMER"
    Cuando intente desactivar una habitacion
    Entonces el sistema debe denegar el acceso
    Y mostrar un error de autorización