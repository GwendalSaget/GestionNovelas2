Documentación Detallada del Código

1. Clase de Datos Book
La clase Book es una data class en Kotlin, que representa un libro con : un titulo, un autor, unaño, un resumen y un Boolean para saber si, s o no, es un favorito

data class Book(
    val title: String,
    val author: String,
    val year: String,
    val summary: String,
    var isfav : Boolean = false
)

2. Composable Interface
   
El composable Interface se encarga de gestionar la interfaz de usuario para agregar libros y mostrar la lista de libros.

@Composable
fun Interface(modifier: Modifier = Modifier) {
    // ...
}

Dentro del composable, se declaran varias variables de estado utilizando remember y mutableStateOf:

var newTitle by remember { mutableStateOf("") }
var newAuthor by remember { mutableStateOf("") }
var newYear by remember { mutableStateOf("") }
var newSummary by remember { mutableStateOf("") }
var bookList by remember { mutableStateOf(listOf<Book>()) }

newTitle, newAuthor, newYear, newSummary: Almacenan los valores ingresados por el usuario en los campos de texto.
bookList: Almacena la lista de objetos Book que el usuario ha añadido.

Se utiliza un Surface para proporcionar un fondo de color gris y una Column para organizar los elementos verticalmente.

Surface(color = Color.Gray, modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Elementos de la interfaz
    }
}

Elementos de la Interfaz
Texto de bienvenida: Un Text que da la bienvenida al usuario.
TextFields: Cuatro TextField para ingresar el título, autor, año y resumen del libro.

En los TextFields, ponemos una limita de longitud que puede cambiar segun el dato que debemos rellenar

Especialidad del TextField para Año:
value: Se vincula a newYear.
onValueChange: Filtra la entrada para aceptar solo dígitos y limita la longitud a 4.

Botón para Añadir un Libro
Un Button permite al usuario añadir un libro a la lista:
Para hacer eso verificamos que todos los campos de texto sean rellenados

Button(
    onClick = {
        if (newTitle.isNotBlank() && newAuthor.isNotBlank() && newYear.isNotBlank() && newSummary.isNotBlank()) {
            bookList = bookList + Book(newTitle, newAuthor, newYear, newSummary)
            newTitle = ""
            newAuthor = ""
            newYear = ""
            newSummary = ""
        }
    },
    modifier = Modifier
        .padding(vertical = 16.dp)
        .align(Alignment.CenterHorizontally)
) {
    Text("Añadir")
}


Lista de Libros
Se utiliza LazyColumn para mostrar los libros añadidos:

LazyColumn(modifier = Modifier.fillMaxSize()) {
    val sortedBooks = bookList.sortedByDescending { it.isfav }
    items(sortedBooks) { book ->
        BookItem(
            book = book,
            onDelete = { bookList = bookList - book },
            onFav = {
                bookList = bookList.map {
                    if (it == book) it.copy(isfav = !it.isfav) else it
                }
            }
        )
    }
}

sortedBooks: Ordena la lista de libros, con en primero los que son favoritos (isfav es true).
items: Itera sobre cada libro en la lista y lo muestra mediante el composable BookItem.

3. Composable BookItem
   
El composable BookItem representa un libro individual en la lista.

@Composable
fun BookItem(book: Book, onDelete: () -> Unit, onFav: () -> Unit) {
    // ...
}

Estructura
Dentro del BookItem, se utiliza un Card para encapsular el contenido:

Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)
        .border(1.dp, MaterialTheme.colorScheme.primary),
    elevation = CardDefaults.cardElevation(4.dp)
) {
    Column(modifier = Modifier.padding(8.dp)) {
        // Elementos del libro
    }
}

Elementos del Libro
Text: Muestra el título, autor, año y resumen del libro. Todos tienen una disposicion diferente (tamaña del texto, Bold...)
Botón para Borrar: Un botón que llama a onDelete para eliminar el libro.
Botón para Favorito: Un botón que llama a onFav para marcar o desmarcar el libro como favorito, con colores diferentes según el estado.
Campo de Reseñas: Permite al usuario agregar notas sobre el libro:

var remark by remember { mutableStateOf("") }
TextField(
    value = remark,
    onValueChange = { if (it.length <= 100) remark = it },
    label = { Text("Reseñas") },
    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    singleLine = false
)

remark: Almacena el texto ingresado para las reseñas, permitiendo un máximo de 100 caracteres.

Añadiduras con el segundo plano : 

1. AsyncTask
AsyncTask es una clase utilizada para realizar operaciones en segundo plano de manera asíncrona. En este caso lo usamos para almacener los datos en la base de datos Firebase en segundo plano gracias al metodo doInBackground.
2. AlarmManager
AlarmManager permite programar tareas para que se ejecuten en momentos específicos, incluso si la aplicación no está en ejecución. Aqui setInexactRepeating permite programar una tarea que se ejecuta en intervalos regulares (por ejemplo, cada hora).
3. BroadcastReceiver
BroadcastReceiver permite activar la sincronización de datos cuando el dispositivo se conecta a una red Wi-Fi gracias a onReceive
4. AsyncTaskLoader
AsyncTaskLoader es una clase que permite cargar datos de manera asíncrona.

Enlace del repositorio : https://github.com/GwendalSaget/GestionNovelas2
Enlace de la base de datos Firebase : https://gestion-novelas-default-rtdb.europe-west1.firebasedatabase.app/
