============================================================
TRABAJO FINAL INTEGRADOR - BASES DE DATOS I
============================================================

INTEGRANTES:
- Alfredo Castillo
- Cesia Catilla
- Rocio Aguero

VERSION DE MYSQL UTULIZADA:
- MySQL Server 8.0

============================================================
ORDEN DE EJECUCIÓN DE SCRIPTS SQL
============================================================

Se deben ejecutar la correcta creacion del esquema, restricciones 
y carga de datos, los scrips deben ejecutarse de forma secuencial 
en el siguiente orden:

1. 01_esquema.sql (creacion de tablas y restricciones)
Creacion de la base de datos 'food_store', tablas del sistema y sus
restricciones de integridad (Primary kay, Foreign key, unique, check).

2. 02_catalogo.sql (Carga de datos semilla iniciales)
Carga inicial de datos maestros fundamentales para las tablas de
categorias y usuarios administradores.

3. 03_carga_masiva.sql (Generacion y carga de datos masivos)
Generacion automatizada mediante CTEs recursivas de un volumen
masivo de registros de prueba, seguido de validaciones de integredad
referencial.

4. 04_indices.sql (Indices de soporte para las consultas)
Creacion mediante procedimientos almacenados de indices secundarios
optimizados para acelerar las busquedad sobre claves foraneas y 
campos filtrados.

5. 05_consultas.sql (Consultas complejas)
Ejecucion del lote de consultas que implementan operaciones Join 
complejos, agrupamientos (GROUP BY), filtros (HAVING) y subconsultas.

5. 05_explain.sql (Analisis de rendimiento)
Analisis de planes de ejecucion (EXPLAIN ANALYZE) para medir el impacto
real y el rendimiento de las consultas antes y despues de crear los indices.

6. 06_vistas.sql (Creacion de vistas del sistema)
Creacion y abstraccion de la capa de reportes mediante
vistas.

7. 07_seguridad.sql (Usuarios)
Implementar una politica estricta de seguridad defensiva y control de acceso en
la base de datos.

8. 08_transacciones.sql (Transacciones manejo de errores y retry ante deadLock)
Creacion del procedimiento almacenado logico con control trnsaccional estricto 
(STRAT TRANSACTION, COMMINT, ROLLBACK), validacion de stock.

9. 09_concurrencia_guiada.sql (Simulacion guiada de concurrencia)
Guia interactiva paso a paso orientada al testing del comportamiento concurrente
bajo distintos niveles de aislamiento.
