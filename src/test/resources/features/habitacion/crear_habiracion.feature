Feature: Gestión de habitaciones
  Como administrador del hotel
  Quiero poder crear nuevas habitaciones
  Para mantener actualizada la configuración del hotel

  Scenario: Crear nueva habitación
    Given el hotel "Santa Marta Resort"
    When el administrador ingrese nombre "Suite Mar Caribe", tipo "Premium", numero_de_camas "2", capacidad_personas "3", descripcion "Jacuzzi, balcón con vista al mar", estado "Disponible"
    Then el sistema debe registrar la habitación con ID "H-456" y asignarla al hotel "Santa Marta Resort"
    And mostrar el mensaje "Habitación creada exitosamente"
