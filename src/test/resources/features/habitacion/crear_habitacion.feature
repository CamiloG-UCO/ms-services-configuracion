# language: es
Característica: Gestión de habitaciones
  Como administrador del hotel
  Quiero poder crear nuevas habitaciones
  Para mantener actualizada la configuración del hotel

  Escenario: Crear nueva habitación
    Dado el hotel "Santa Marta Resort"
    Cuando el administrador ingrese nombre "Suite Mar Caribe", tipo "Premium", numero_de_camas "2", capacidad_personas "3", descripcion "Jacuzzi, balcón con vista al mar", estado "Disponible"
    Entonces el sistema debe registrar la habitación con ID "H-456" y asignarla al hotel "Santa Marta Resort"
    Y mostrar el mensaje "Habitación creada exitosamente"
