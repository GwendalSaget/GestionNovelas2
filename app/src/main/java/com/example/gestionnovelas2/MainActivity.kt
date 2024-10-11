package com.example.gestionnovelas2

import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestionnovelas2.ui.theme.GestionNovelas2Theme
import com.google.firebase.database.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.AsyncTaskLoader
import androidx.loader.content.Loader
import java.util.concurrent.CountDownLatch

class MainActivity : ComponentActivity() {
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var booksRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseDatabase = FirebaseDatabase.getInstance()
        booksRef = firebaseDatabase.getReference("books")

        // Initialiser AlarmManager pour planifier des tâches de synchronisation périodiques
        schedulePeriodicSync()

        // Register the network broadcast receiver to trigger data sync on network changes
        registerReceiver(NetworkChangeReceiver(), NetworkChangeReceiver.getNetworkChangeIntentFilter())

        enableEdgeToEdge()
        setContent {
            GestionNovelas2Theme {
                Interface(booksRef)
            }
        }
    }

    // Méthode pour planifier la synchronisation périodique avec AlarmManager
    private fun schedulePeriodicSync() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, SyncBroadcastReceiver::class.java)
        val requestCode = 0 // ID unique pour le PendingIntent

        // Définir les flags en fonction de la version de l'API
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_CANCEL_CURRENT
        }

        // Créer le PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, flags)

        // Planifier l'alarme pour se répéter toutes les heures
        val intervalMillis = AlarmManager.INTERVAL_HOUR
        val triggerAtMillis = SystemClock.elapsedRealtime() + intervalMillis

        // Définir une alarme répétitive
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerAtMillis,
            intervalMillis,
            pendingIntent
        )
    }

}

data class Book(
    val id: String? = null,
    val title: String,
    val author: String,
    val year: String,
    val summary: String,
    var isfav: Boolean = false
)

@Composable
fun Interface(booksRef: DatabaseReference, modifier: Modifier = Modifier) {
    var newTitle by remember { mutableStateOf("") }
    var newAuthor by remember { mutableStateOf("") }
    var newYear by remember { mutableStateOf("") }
    var newSummary by remember { mutableStateOf("") }
    var bookList by remember { mutableStateOf(listOf<Book>()) }
    val context = LocalContext.current
    val loaderManager = remember {
        (context as? ComponentActivity) ?: throw IllegalArgumentException("Context must be ComponentActivity")
    }.let { LoaderManager.getInstance(it) }

    val loaderId = 0 // ID unique pour le Loader

    // Utiliser LaunchedEffect pour initialiser le Loader
    LaunchedEffect(Unit) {
        loaderManager.initLoader(loaderId, null, object : LoaderManager.LoaderCallbacks<List<Book>> {
            override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Book>> {
                return BookLoader(context, booksRef)
            }


            override fun onLoadFinished(
                loader: androidx.loader.content.Loader<List<Book>>,
                data: List<Book>?
            ) {
                bookList = data ?: emptyList()
            }

            override fun onLoaderReset(loader: androidx.loader.content.Loader<List<Book>>) {
                bookList = emptyList()
            }
        })
    }

    Surface(color = Color.Gray, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡ Bienvenido !",
                color = Color.Black,
                fontSize = 45.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            TextField(
                value = newTitle,
                onValueChange = { if (it.length <= 30) newTitle = it },
                label = { Text("Título") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            TextField(
                value = newAuthor,
                onValueChange = { if (it.length <= 30) newAuthor = it },
                label = { Text("Autor") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            TextField(
                value = newYear,
                onValueChange = {
                    if (it.length <= 4) {
                        newYear = it.filter { char -> char.isDigit() }
                    }
                },
                label = { Text("Año") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            TextField(
                value = newSummary,
                onValueChange = { if (it.length <= 500) newSummary = it },
                label = { Text("Resumen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = false
            )

            Button(
                onClick = {
                    if (newTitle.isNotBlank() && newAuthor.isNotBlank() && newYear.isNotBlank() && newSummary.isNotBlank()) {
                        val newBook = Book(
                            title = newTitle,
                            author = newAuthor,
                            year = newYear,
                            summary = newSummary
                        )
                        bookList = bookList + newBook
                        AddBookTask(booksRef).execute(newBook)
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
            Log.d("Interface", "Book list size: ${bookList.size}")
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
        }
    }
}

@Composable
fun BookItem(book: Book, onDelete: () -> Unit, onFav: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Titulo : ${book.title}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Autor : ${book.author}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Año : ${book.year}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Resumen : ${book.summary}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Borar")
            }
            Button(
                onClick = onFav,
                colors = if (!book.isfav) {ButtonDefaults.buttonColors(containerColor = Color.LightGray)} else {ButtonDefaults.buttonColors(containerColor = Color.Yellow)},
                modifier = Modifier.align(Alignment.End)
            ){
                Text("Favorito")
            }
            var remark by remember { mutableStateOf("") }

            TextField(
                value = remark,
                onValueChange = { if (it.length <= 100) remark = it },
                label = { Text("Reseñas") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                singleLine = false
            )
        }
    }
}

// AsyncTask pour ajouter un livre à Firebase
class AddBookTask(private val booksRef: DatabaseReference) : AsyncTask<Book, Void, Void>() {
    override fun doInBackground(vararg params: Book): Void? {
        val book = params[0]
        val newBookRef = booksRef.push() // Générer un nouvel ID pour le livre
        newBookRef.setValue(book.copy(id = newBookRef.key)) // Ajouter le livre à Firebase
        return null
    }
}

// AsyncTaskLoader pour charger les livres depuis Firebase
class BookLoader(context: Context, private val booksRef: DatabaseReference) : AsyncTaskLoader<List<Book>>(context) {
    private var bookList: List<Book>? = null

    override fun loadInBackground(): List<Book>? {
        val result = mutableListOf<Book>()
        val latch = CountDownLatch(1) // Pour synchroniser le chargement

        booksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    book?.let { result.add(it) }
                }
                latch.countDown()
            }

            override fun onCancelled(error: DatabaseError) {
                latch.countDown()
                Log.e("FirebaseError", error.message)
            }
        })

        latch.await() // Attendre que le chargement soit terminé
        return result
    }

    override fun deliverResult(data: List<Book>?) {
        if (data != null) {
            bookList = data
            super.deliverResult(data)
        }
    }
}

// BroadcastReceiver pour les changements de réseau
class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (isConnectedToWifi(context)) {
            Toast.makeText(context, "Wi-Fi Connected! Syncing data...", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun isConnectedToWifi(context: Context?): Boolean {
            val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            return activeNetwork != null && activeNetwork.type == ConnectivityManager.TYPE_WIFI
        }

        fun getNetworkChangeIntentFilter(): IntentFilter {
            return IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        }
    }
}

// BroadcastReceiver pour gérer les événements AlarmManager
class SyncBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Alarm Triggered: Syncing Data!", Toast.LENGTH_SHORT).show()
    }
}


