Feature: Gestion de hoteles
  Como administrador general
  Quiero registrar un nuevo hotel
  Para expandar la cadena y habilitar reservas en nuestras ubicaciones

  Scenario: Crear hotel nuevo
    Given los datos del nuevo hotel "Hotel Cartagena Real" con dirección "Calle 45 # 12-89", ciudad "Cartagena" del departamento "Bolívar", pais "Colombia" con el numero de contacto "5555555" con descripcion "tiene piscina olímpica, cancha de futbol"
    When el administrador presione "Guardar"
    Then el sistema debe registrar el hotel con código "HTL-002"
    And mostrar el mensaje "Hotel registrado exitosamente"