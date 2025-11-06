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

  Escenario: Crear habitación con tipo inválido
    Dado el hotel "Santa Marta Resort"
    Cuando el administrador ingrese nombre "Suite Mar Caribe", tipo "Desconocido", numero_de_camas "2", capacidad_personas "3", descripcion "Jacuzzi, balcón con vista al mar", estado "Disponible"
    Entonces mostrar el mensaje "Tipo de habitación inválido: Desconocido"

  Escenario: Crear habitación con ID duplicado
    Dado el hotel "Santa Marta Resort"
    Y existe una habitación con ID "H-456" en el hotel "Santa Marta Resort"
    Cuando el administrador ingrese nombre "Suite Mar Caribe", tipo "Premium", numero_de_camas "2", capacidad_personas "3", descripcion "Jacuzzi, balcón con vista al mar", estado "Disponible"
    Entonces mostrar el mensaje "Ya existe una habitación con el ID: H-456"

  Escenario: Crear habitación en hotel inexistente
    Dado no existe el hotel "Hotel Fantasma"
    Cuando el administrador ingrese nombre "Suite Fantasma", tipo "Premium", numero_de_camas "2", capacidad_personas "3", descripcion "Jacuzzi invisible", estado "Disponible"
    Entonces mostrar el mensaje "Hotel no encontrado"

