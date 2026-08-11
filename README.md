# Sistema de Inventario

Este programa sirve para guardar productos. Se puede poner el nombre, precio, cantidad y categoría de cada producto.

También permite agregar, editar, buscar y borrar productos. Las categorías se pueden administrar desde el botón de la ventana principal.

## Cómo abrirlo

1. Tener un servidor MySQL corriendo en tu máquina (por ejemplo con XAMPP, WAMP o MySQL Server directo).
2. Ajustar el usuario y la contraseña en `src/main/java/mx/edu/inventario/db/DatabaseConnection.java` (constantes `USER` y `PASSWORD`) para que coincidan con tu servidor.
3. Abrir la carpeta del proyecto en IntelliJ.
4. Abrir el archivo `pom.xml` como proyecto Maven.
5. Ejecutar la clase `App.java`.

También se puede ejecutar desde la terminal con:

```bash
mvn clean compile exec:java
```

La primera vez necesita Internet para descargar el conector de MySQL. El programa crea solo la base de datos `inventario` y sus tablas si no existen (requiere que el servidor MySQL ya esté encendido).

## Carpetas principales

- `model`: clases de Producto y Categoria.
- `dao`: consultas para guardar y leer datos.
- `ui`: ventanas del programa.
- `database`: script de la base de datos con ejemplos.

## Nota

Si aparecen mensajes amarillos de SQLite o Java en la consola, no pasa nada mientras abra la ventana del programa. No afectan el funcionamiento.

Para subirlo a Git cuando toque:

```bash
git init
git add .
git commit -m "Primer avance del inventario"
```
