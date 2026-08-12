# Sistema de Inventario

Aplicación de escritorio desarrollada en Java Swing para administrar productos y categorías almacenados en MySQL.

## Funciones principales

- Alta, consulta, modificación y eliminación de productos y categorías.
- Catálogo visual responsivo y tabla de inventario ordenable.
- Búsqueda por nombre, SKU o código de barras y filtro por categoría.
- Estados de existencia: agotado, crítico, advertencia y óptimo.
- Dashboard con totales y gráfica del estado del stock.
- Cambio de precio y reabastecimiento de varios productos.
- SKU, código de barras y ubicación física en almacén.
- Confirmaciones antes de operaciones sensibles.
- Catálogo demostrativo con 10 categorías y 30 productos.

## Requisitos

- JDK 21 o posterior.
- Maven 3.9 o posterior.
- MySQL Server 8 o posterior.
- IntelliJ IDEA, VS Code o cualquier IDE compatible con Maven (opcional).

## Configuración y ejecución

1. Iniciar MySQL Server.
2. Ajustar `USER` y `PASSWORD` en `src/main/java/mx/edu/inventario/db/DatabaseConnection.java`.
3. Abrir el proyecto como proyecto Maven.
4. Ejecutar `mx.edu.inventario.App` o utilizar:

```bash
mvn clean compile exec:java
```

La aplicación crea la base de datos `inventario`, prepara las tablas y carga datos demostrativos sin duplicar registros existentes.

## Estructura

- `model`: entidades del dominio.
- `dao`: operaciones SQL y persistencia.
- `service`: validaciones y reglas de negocio.
- `ui`: ventanas y componentes Swing.
- `db`: conexión, creación de tablas y datos iniciales.
- `exception`: excepciones controladas.
- `database`: script SQL completo para evaluación.
- `documentation`: manual de instalación y operación en PDF.

## Base de datos

El script `database/KSHC_EOH_LRGF_SAHR_3IRD-G1.sql` crea las tablas, relaciones y 30 productos de prueba. El script elimina las tablas existentes antes de reconstruirlas, por lo que debe utilizarse en una base de evaluación o respaldo.

## Repositorio

https://github.com/sergioabi165/Aplicacion
