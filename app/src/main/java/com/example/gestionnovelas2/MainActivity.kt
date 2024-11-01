package com.example.gestionnovelas2

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestionnovelas2.ui.theme.GestionNovelas2Theme
import java.io.File

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: BookDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = BookDatabaseHelper(this)

        enableEdgeToEdge()
        val selectedColor = getSelectedColor(this)
        setContent {
            GestionNovelas2Theme {
                Interface(
                    dbHelper,
                    modifier = Modifier,
                    backgroundColor = GetColor(selectedColor))
            }
        }
    }
}

data class Book(
    val id: String? = null,
    val title: String,
    val author: String,
    val year: String,
    val summary: String
)

class BookDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_BOOKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT,
                $COLUMN_AUTHOR TEXT,
                $COLUMN_YEAR TEXT,
                $COLUMN_SUMMARY TEXT
            )
        """
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKS")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "books.db"
        const val DATABASE_VERSION = 1
        const val TABLE_BOOKS = "books"
        const val COLUMN_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_YEAR = "year"
        const val COLUMN_SUMMARY = "summary"
    }
}

// DAO pour interagir avec la base de données SQLite
class BookDao(private val dbHelper: BookDatabaseHelper) {

    fun addBook(book: Book) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(BookDatabaseHelper.COLUMN_TITLE, book.title)
            put(BookDatabaseHelper.COLUMN_AUTHOR, book.author)
            put(BookDatabaseHelper.COLUMN_YEAR, book.year)
            put(BookDatabaseHelper.COLUMN_SUMMARY, book.summary)
        }
        db.insert(BookDatabaseHelper.TABLE_BOOKS, null, values)
    }

    fun getAllBooks(): List<Book> {
        val db = dbHelper.writableDatabase
        val cursor = db.query(
            BookDatabaseHelper.TABLE_BOOKS,
            null, null, null, null, null, null
        )

        val books = mutableListOf<Book>()
        with(cursor) {
            while (moveToNext()) {
                val book = Book(
                    id = getString(getColumnIndexOrThrow(BookDatabaseHelper.COLUMN_ID)),
                    title = getString(getColumnIndexOrThrow(BookDatabaseHelper.COLUMN_TITLE)),
                    author = getString(getColumnIndexOrThrow(BookDatabaseHelper.COLUMN_AUTHOR)),
                    year = getString(getColumnIndexOrThrow(BookDatabaseHelper.COLUMN_YEAR)),
                    summary = getString(getColumnIndexOrThrow(BookDatabaseHelper.COLUMN_SUMMARY))
                )
                books.add(book)
            }
        }
        cursor.close()
        return books
    }

    fun deleteBook(book: Book) {
        val db = dbHelper.writableDatabase
        db.delete(BookDatabaseHelper.TABLE_BOOKS, "${BookDatabaseHelper.COLUMN_ID} = ?", arrayOf(book.id))
    }

    fun backupBooksToFile(context: Context) {
        val books = getAllBooks()
        val file = File(context.filesDir, "backup_books.txt")
        file.printWriter().use { out ->
            books.forEach { book ->
                out.println("${book.title},${book.author},${book.year},${book.summary}")
            }
        }
    }

    fun restoreBooksFromFile(context: Context): List<Book> {
        val file = File(context.filesDir, "backup_books.txt")
        val books = mutableListOf<Book>()

        if (file.exists()) {
            file.forEachLine { line ->
                val parts = line.split(",")
                if (parts.size == 4) {
                    val book = Book(
                        title = parts[0],
                        author = parts[1],
                        year = parts[2],
                        summary = parts[3]
                    )
                    books.add(book)
                }
            }
        } else {
            Log.e("BookDao", "Backup file not found")
        }

        return books
    }
}

@Composable
fun Interface(dbHelper: BookDatabaseHelper, modifier: Modifier = Modifier, backgroundColor: Color) {
    val dao = BookDao(dbHelper)
    var newTitle by remember { mutableStateOf("") }
    var newAuthor by remember { mutableStateOf("") }
    var newYear by remember { mutableStateOf("") }
    var newSummary by remember { mutableStateOf("") }
    var bookList by remember { mutableStateOf(dao.getAllBooks()) }
    val context = LocalContext.current
    val userData = getUserData(context)

    Surface(color = backgroundColor) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.offset(0.dp, 50.dp)) {
                Button(onClick = {
                    dao.backupBooksToFile(context)
                }) {
                    Text("Almacenar libros en externo")
                }
            }
            Column(modifier = Modifier.offset(250.dp, 0.dp)) {
                Button(onClick = {
                    bookList = dao.restoreBooksFromFile(context)
                    newTitle = ""
                    newAuthor = ""
                    newYear = ""
                    newSummary = ""
                }) {
                    Text("Restaurar libros")
                }
            }

            Text(
                text = userData,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Column(modifier = Modifier.offset(150.dp, 10.dp)) {
                Button(onClick = {
                    val intent = Intent(context, SettingsPantalla::class.java)
                    context.startActivity(intent)
                }) {
                    Text(text = stringResource(R.string.Principal), fontSize = 20.sp)
                }
            }
            TextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            TextField(
                value = newAuthor,
                onValueChange = { newAuthor = it },
                label = { Text("Autor") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            TextField(
                value = newYear,
                onValueChange = { newYear = it },
                label = { Text("Año") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            TextField(
                value = newSummary,
                onValueChange = { newSummary = it },
                label = { Text("Resumen") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            Button(onClick = {
                if (newTitle.isNotBlank() && newAuthor.isNotBlank() && newYear.isNotBlank() && newSummary.isNotBlank()) {
                    val newBook = Book(
                        title = newTitle,
                        author = newAuthor,
                        year = newYear,
                        summary = newSummary
                    )
                    dao.addBook(newBook)
                    bookList = dao.getAllBooks()
                    newTitle = ""
                    newAuthor = ""
                    newYear = ""
                    newSummary = ""
                }
            }) {
                Text("Añadir")
            }

            LazyColumn {
                items(bookList) { book ->
                    BookItem(
                        book = book,
                        onDelete = {
                            dao.deleteBook(book)
                            bookList = dao.getAllBooks()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Título: ${book.title}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Autor: ${book.author}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Año: ${book.year}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Resumen: ${book.summary}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Borrar")
            }
        }
    }
}




