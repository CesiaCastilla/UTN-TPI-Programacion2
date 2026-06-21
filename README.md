# Food Store — TPI Programación 2

Trabajo Práctico Integrador de **Programación 2** — Tecnicatura Universitaria en Programación (UTN).

Sistema de consola para la gestión de pedidos de un negocio de comidas ("Food Store"), desarrollado en Java aplicando Programación Orientada a Objetos. Todo el almacenamiento es **en memoria** mediante Colecciones (no usa base de datos ni login): el acceso es directo a través de un menú principal.

## Integrantes

| Integrante | Rol en el TPI |
|---|---|
| Alfredo Alexander Castillo Belisario | Arquitectura base del proyecto (`Base`, enums, `Calculable`) y Gestión de Categorías (`Categoria`, repository, service y menú). Informe PDF de Programación 2. |
| Cesia Madelein Chihuan Castilla | Gestión de Productos y Gestión de Usuarios (entidades, repository, service y menú de cada una). README.md. |
| Rocío Marianela Agüero | Gestión de Pedidos y Detalles (`Pedido`, `DetallePedido`, repository, service y menú) y paquete `exception/`. Diagrama UML. |

## Requisitos previos

- **Java 21** (JDK) instalado y configurado en el `PATH`.
- Un IDE compatible con proyectos Ant/NetBeans (NetBeans) **o** simplemente una terminal — el proyecto no depende del IDE para compilar/ejecutar.
- No se requiere base de datos ni configuración adicional: toda la información vive en memoria durante la ejecución del programa y se reinicia al cerrarlo.

## Estructura del proyecto

```
src/integrado/prog2/
├── Main.java          → punto de entrada, menú principal
├── config/            → DataInitializer (datos de demo precargados)
├── entities/          → clases del dominio (Base, Categoria, Producto, Usuario, Pedido, DetallePedido)
├── enums/             → Rol, EstadoPedido, FormaPago
├── interfaces/        → Calculable
├── exception/         → excepciones propias (EntidadNoEncontradaException, StockInvalidoException, ValidationException)
├── repository/        → almacenamiento en memoria (ArrayList) por entidad
├── service/           → lógica de negocio y validaciones
└── menu/               → submenús de consola (Categoria, Producto, Usuario, Pedido)
```

- **Main/Menu**: solo interacción con el usuario (Scanner) y delegación a los Services.
- **Service**: validaciones y reglas de negocio (precio/stock no negativos, mail y nombre únicos, baja lógica, etc.).
- **Repository**: persistencia en memoria sobre `ArrayList`, con baja lógica (`eliminado = true`) en vez de eliminar físicamente.
- **Entities**: modelo de dominio (POO), todas heredan de `Base` (id, eliminado, createdAt) y tienen `toString()` propio.

## Cómo ejecutar

### Opción 1 — Desde NetBeans
1. Abrir la carpeta del proyecto en NetBeans (reconoce automáticamente `build.xml` y `nbproject/`).
2. Ejecutar el proyecto (▶ Run Project). La clase principal es `integrado.prog2.Main`.

### Opción 2 — Desde la terminal (sin IDE)
Desde la raíz del repositorio:

```bash
# Compilar
mkdir -p build
javac -d build $(find src -name "*.java")

# Ejecutar
java -cp build integrado.prog2.Main
```

Al iniciar, el programa precarga una categoría y algunos usuarios de ejemplo (ver `config/DataInitializer.java`) para facilitar las pruebas, y muestra el menú principal:

```
========== MENÚ PRINCIPAL ==========
1. Gestión de Categorías
2. Gestión de Productos
3. Gestión de Usuarios
4. Gestión de Pedidos
0. Salir
```

Cada opción abre un submenú con operaciones CRUD (Listar, Crear, Editar, Eliminar) validadas e independientes entre sí.

## Funcionalidades principales

- CRUD completo de Categorías, Productos, Usuarios y Pedidos, con baja lógica (las entidades eliminadas nunca se borran de la colección, solo se marcan y dejan de listarse).
- Relación de objeto real `Producto → Categoria` (unidireccional).
- Alta de Pedido con carga de uno o más detalles, validación de stock, cálculo automático del total vía la interfaz `Calculable`, y reversión de stock si la operación falla a mitad de camino.
- Validaciones de negocio: precio y stock no negativos, mail de Usuario único, nombre de Categoría único, cantidad de detalle mayor a cero, aviso al eliminar una Categoría con Productos asociados.
- Manejo de errores con excepciones propias (`EntidadNoEncontradaException`, `StockInvalidoException`, `ValidationException`) capturadas en los menús con mensajes claros de éxito/error.

## Documentación y video

- **Video demostrativo:** _pendiente — completar enlace con permisos públicos de visualización._
- **Informe PDF (Programación 2):** _pendiente — completar enlace o adjuntar el PDF en la raíz del repositorio._
- **Diagrama UML:** _pendiente — completar enlace o adjuntar la imagen exportada._
