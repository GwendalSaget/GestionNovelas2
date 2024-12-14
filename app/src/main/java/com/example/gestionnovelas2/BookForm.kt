package com.example.gestionnovelas2

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference

@Composable
fun BookForm(booksRef: DatabaseReference, barrioDefaut: String = "Tetuan") {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Titulo") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Autor") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        TextField(
            value = year,
            onValueChange = { if (it.length <= 4) year = it },
            label = { Text("Año") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        TextField(
            value = summary,
            onValueChange = { if (it.length <= 500) summary = it },
            label = { Text("Resumen") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Button(
            onClick = {
                if (title.isNotEmpty() && author.isNotEmpty() && year.isNotEmpty()) {
                    val bookId = booksRef.push().key ?: ""
                    val newBook = Book(bookId, title, author, year, summary, barrio = barrioDefaut)
                    booksRef.child(bookId).setValue(newBook)
                    message = "Libro añadido en $barrioDefaut!"
                    title = ""
                    author = ""
                    year = ""
                    summary = ""
                } else {
                    message = "Todos los campos son obligatorios"
                }
            },
            modifier = Modifier.padding(top = 16.dp).align(Alignment.End)
        ) {
            Text("Añadir")
        }

        if (message.isNotEmpty()) {
            Text(message, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
