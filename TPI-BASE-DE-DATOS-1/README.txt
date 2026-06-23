============================================================
TRABAJO FINAL INTEGRADOR - BASES DE DATOS I
============================================================

INTEGRANTES:
- Alfredo Castillo
- Cesia Castilla
- Rocío Agüero

VERSIÓN DE MYSQL UTILIZADA:
- MySQL Server 8.0

============================================================
ORDEN DE EJECUCIÓN DE SCRIPTS SQL
============================================================

Para garantizar la correcta creación del esquema, restricciones
y carga de datos, los scripts deben ejecutarse de forma secuencial
en el siguiente orden:

1. 01_esquema.sql (Creación de tablas y restricciones)
Creación de la base de datos 'food_store', tablas del sistema y sus
restricciones de integridad (Primary key, Foreign key, unique, check).

2. 02_catalogo.sql (Carga de datos semilla iniciales)
Carga inicial de datos maestros fundamentales para las tablas de
categorías y usuarios administradores.

3. 03_carga_masiva.sql (Generación y carga de datos masivos)
Generación automatizada mediante CTEs recursivas de un volumen
masivo de registros de prueba, seguido de validaciones de integridad
referencial.

4. 04_indices.sql (Índices de soporte para las consultas)
Creación mediante procedimientos almacenados de índices secundarios
optimizados para acelerar la búsqueda sobre claves foráneas y
campos filtrados.

5. 05_consultas.sql (Consultas complejas)
Ejecución del lote de consultas que implementan operaciones JOIN
complejas, agrupamientos (GROUP BY), filtros (HAVING) y subconsultas.

6. 05_explain.sql (Análisis de rendimiento)
Análisis de planes de ejecución (EXPLAIN ANALYZE) para medir el impacto
real y el rendimiento de las consultas antes y después de crear los índices.

7. 06_vistas.sql (Creación de vistas del sistema)
Creación y abstracción de la capa de reportes mediante
vistas.

8. 07_seguridad.sql (Usuarios)
Implementación de una política estricta de seguridad defensiva y control
de acceso en la base de datos.

9. 08_transacciones.sql (Transacciones, manejo de errores y retry ante deadlock)
Creación del procedimiento almacenado con control transaccional estricto
(START TRANSACTION, COMMIT, ROLLBACK), validación de stock.

10. 09_concurrencia_guiada.sql (Simulación guiada de concurrencia)
Guía interactiva paso a paso orientada al testing del comportamiento
concurrente bajo distintos niveles de aislamiento.
