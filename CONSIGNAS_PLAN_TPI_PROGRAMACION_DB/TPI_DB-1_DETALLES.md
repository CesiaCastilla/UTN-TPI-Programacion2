## Trabajo Final Integrador (TFI) — Bases de Datos I

Resumen: el TFI integra lo visto en Bases de Datos I con el sistema desarrollado en Programación II. Con el mismo equipo de trabajo de Programación II, debés modelar e implementar una base de datos para uno o más procesos clave del sistema, realizar una carga masiva de datos (ver volumen recomendado en la CONSIGNA), producir consultas y vistas útiles, aplicar seguridad e integridad (usuarios/roles, ocultamiento de datos) y trabajar transacciones/concurrencia (niveles de aislamiento).

Deberás entregar PDF + scripts .sql + video.

# Objetivo del Trabajo

Integrar los contenidos de Bases de Datos I en un caso aplicado al sistema desarrollado en Programación II, demostrando competencias técnicas y capacidad de comunicación.

- Modelado y constraints: diseño relacional correcto (PK, FK, UNIQUE, CHECK) y validaciones.
- Implementación y carga: creación del esquema y carga masiva de datos con SQL.
- Consultas y reportes: consultas de negocio (JOIN, GROUP BY/HAVING, subconsultas) y vistas útiles.
- Seguridad e integridad: usuario con privilegios mínimos y protección de datos sensibles.
- Concurrencia y transacciones: manejo de transacciones, niveles de aislamiento y documentación de resultados.
- Rendimiento (ver Etapa 3): medición comparativa con/sin índices en consultas representativas (igualdad, rango, JOIN), según la metodología de la CONSIGNA.
- El detalle por etapas y criterios está en el archivo de la sección [CONSIGNA](Trabajo_Final_Integrador_Bases_de_Datos1.pdf).

# Tema y Marco Referencial

El trabajo se articula con el sistema desarrollado en Programación II. Consiste en modelar e implementar la base de datos que soporte uno o más procesos clave del sistema, documentando las reglas de negocio y las decisiones de diseño.

Alcance mínimo sugerido:

- Dominio acotado (p. ej., ventas, turnos, stock, matriculación).
- Reglas de negocio explícitas (supuestos, restricciones, integridad).
- Volumen de datos y casos de uso que justifiquen consultas y reportes.

# Formato de Entrega

Debe incluir:

- Carpeta digital:
    - PDF (único): portada, resumen ejecutivo (5–7 líneas), reglas de negocio, DER/MR, decisiones de diseño y constraints, evidencias (consultas/resultados, verificaciones de consistencia, comparación con/sin índice, concurrencia), referencia cruzada a scripts (p. ej., “ver 03_carga_masiva.sql”), anexo IA en texto (no capturas), enlace al video (acceso no privado).
    - ZIP de scripts .sql: 01_esquema.sql (PK/FK/UNIQUE/CHECK) · 02_catalogos.sql · 03_carga_masiva.sql · 04_indices.sql · 05_consultas.sql (+ 05_explain.sql) · 06_vistas.sql · 07_seguridad.sql  · 08_transacciones.sql · 09_concurrencia_guiada.sql · README.txt (orden y versión). Requisito: scripts idempotentes (usar DROP IF EXISTS).
    - Se considera entregado cuando estén cargados todos los recursos: PDF + ZIP de scripts + video.
    - Evidencia del uso de IA como tutoría: anexo en formato texto (no capturas), organizado por Tema/Etapa. Puede ir al final del PDF o en un PDF complementario.
    - Nomenclatura: TFI_BDI_ComisionX_GrupoY_Apellidos.pdf y TFI_BDI_ComisionX_GrupoY_Apellidos.zip (scripts).
- Video del equipo (10–15 minutos). Ver indicaciones en la siguiente seccion Guía para desarrollar el video. Incluir el enlace dentro del PDF y cargarlo en la plataforma.

# Guía para desarrollar el video

- Duración: 10–15 minutos.
- Inicio: cada integrante se presenta con cámara encendida y nombre completo.
- Temática: indicar claramente el caso del proyecto.
- Desarrollo: puede continuar con cámara apagada, pero siempre con voz propia. No se permiten voces generadas por IA.
- Enlace: incluir el link al video dentro del PDF y cargar el archivo/enlace en la plataforma.
- Estructura sugerida:
    - Presentación de integrantes
    - Introducción al tema y objetivos
    - Marco teórico (conceptos/fundamentos)
    - Caso práctico (modelo y base implementada)
    - Consultas y resultados relevantes
    - Conclusiones (aprendizajes y mejoras)
