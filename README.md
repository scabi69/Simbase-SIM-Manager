# Simbase SIM Manager

Este es mi primer proyecto para realizar una app en android. No lo he programado yo, lo ha hecho Copilot siguiendo mis instrucciones.

## Requisitos

Para realizar una gestión simple de las tarjetas SIM vinculadas a tu cuenta de Simbase, necesitarás 2 token de la API de Simbase vinculados a tu cuenta.
Para obtenerlos visita: [Dashboard Simbase](https://dashboard.simbase.com/dashboard/integrations?tab=api)

Puedes generar un solo token de "todo escritura" que introducirás en ambos campos de configuración de la app: *Token de lectura* y *Token de escritura*.

Yo lo utilizo en un modo algo más restrictivo, utilizando un token de lectura que como su nombre dice solo lee datos de la cuenta de Simbase. Y a mayores un token de escritura al que solamente se le dan permisos para modificar el estado de activación de las SIM. Os dejo una captura de como sería la configuración de ambos:

| Token de lectura | Token de escritura |
| :----: | :----: |
| ![Configuración para el token de lectura](https://github.com/scabi69/Simbase-SIM-Manager/blob/master/Token%20de%20lectura.png) | ![Configuración para el token de escritura](https://github.com/scabi69/Simbase-SIM-Manager/blob/master/Token%20de%20escritura.png) |
| :----: | :----: |
