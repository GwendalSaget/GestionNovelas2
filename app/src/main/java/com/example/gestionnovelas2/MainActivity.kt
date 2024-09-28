package com.example.gestionnovelas2

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.gestionnovelas2.ui.theme.GestionNovelas2Theme


//pfernbar@gmail.com

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GestionNovelas2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Interface(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                    )
                }
            }
        }
    }
}

data class Book(
    val title: String,
    val author: String,
    val year: String,
    val summary: String,
    var isfav : Boolean = false
)

@Composable
fun Interface(modifier: Modifier = Modifier) {
    var newTitle by remember { mutableStateOf("") }
    var newAuthor by remember { mutableStateOf("") }
    var newYear by remember { mutableStateOf("") }
    var newSummary by remember { mutableStateOf("") }
    var bookList by remember { mutableStateOf(listOf<Book>()) }

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
                singleLine = false
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
                singleLine = true
            )

            // Bouton pour ajouter le livre
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


@Preview(showBackground = true)
@Composable
fun InterPreview() {
    GestionNovelas2Theme {
    Interface (modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),)
    }
}
