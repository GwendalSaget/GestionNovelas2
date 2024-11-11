Funcionalidades:
Base de Datos SQLite: La aplicación utiliza una base de datos SQLite para almacenar los libros. Cada libro tiene los siguientes campos: título, autor, año y resumen.
Agregar y Borrar Libros: Los usuarios pueden agregar nuevos libros rellenando un formulario con los detalles del libro y también pueden borrar libros individuales de la lista.
Respaldo y Restauración: Los libros pueden ser guardados en un archivo de texto ("backup_books.txt") que se almacena en el almacenamiento interno del dispositivo. Los usuarios pueden restaurar su colección de libros desde este archivo, reemplazando la lista actual.
Interfaz de Usuario: La interfaz está creada con Jetpack Compose, utilizando LazyColumn para mostrar la lista de libros y TextField para capturar los datos de los nuevos libros.
Navegación a Pantalla de Configuración: La aplicación permite la navegación a una pantalla de configuración donde el usuario puede modificar preferencias (aunque los detalles de esta pantalla no se incluyen en este resumen).
Temas y Personalización: La aplicación adapta el color de fondo según las preferencias del usuario, guardadas en SharedPreferences.

Enlace del repositorio : https://github.com/GwendalSaget/GestionNovelas2

