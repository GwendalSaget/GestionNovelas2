package com.example.gestionnovelas2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

data class Book(
    val id: String? = null,
    val title: String,
    val author: String,
    val year: String,
    val summary: String,
    var isfav: Boolean = false,
    var barrio: String = "Todos"
) {
    constructor() : this(
        id = null,
        title = "",
        author = "",
        year = "",
        summary = "",
        isfav = false,
        barrio = "Todos"
    )
}

class BookListFragment : Fragment() {
    private lateinit var booksRef: DatabaseReference
    private var bookList by mutableStateOf(listOf<Book>())
    private var barrios by mutableStateOf(setOf("Todos"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        booksRef = FirebaseDatabase.getInstance().getReference("books")

        booksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()
                val barrioSet = mutableSetOf("Todos")
                snapshot.children.forEach {
                    val book = it.getValue(Book::class.java)?.copy(id = it.key)
                    if (book != null) {
                        books.add(book)
                        if (book.barrio.isNotEmpty()) barrioSet.add(book.barrio)
                    }
                }
                bookList = books
                barrios = barrioSet
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        return ComposeView(requireContext()).apply {
            setContent {
                BookListScreen(
                    bookList = bookList,
                    barrios = barrios,
                    onDelete = { book ->
                        book.id?.let { booksRef.child(it).removeValue() }
                    },
                    onFav = { book ->
                        book.id?.let {
                            booksRef.child(it).child("isfav").setValue(!book.isfav)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BookListScreen(
    bookList: List<Book>,
    barrios: Set<String>,
    onDelete: (Book) -> Unit,
    onFav: (Book) -> Unit
) {
    var selectedBarrio by remember { mutableStateOf("Todos") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text("Filtro de barrio :", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        BarrioDropdown(
            barrios = barrios,
            selectedBarrio = selectedBarrio,
            onBarrioSelected = { selectedBarrio = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            val filteredBooks =
                if (selectedBarrio == "Todos") bookList else bookList.filter { it.barrio == selectedBarrio }
            items(filteredBooks.sortedByDescending { it.isfav }) { book ->
                BookItem(
                    book = book,
                    onDelete = { onDelete(book) },
                    onFav = { onFav(book) }
                )
            }
        }
    }
}

@Composable
fun BarrioDropdown(
    barrios: Set<String>,
    selectedBarrio: String,
    onBarrioSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(selectedBarrio)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            barrios.forEach { barrio ->
                DropdownMenuItem(
                    text = { Text(barrio) },
                    onClick = {
                        onBarrioSelected(barrio)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun BookItem(book: Book, onDelete: () -> Unit, onFav: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Titulo : ${book.title}", style = MaterialTheme.typography.titleMedium)
            Text(text = "Autor : ${book.author}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Año : ${book.year}", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Resumen : ${book.summary}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Borar")
                }

                Button(
                    onClick = onFav,
                    colors = if (!book.isfav) {
                        ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    } else {
                        ButtonDefaults.buttonColors(containerColor = Color.Yellow)
                    }
                ) {
                    Text("Favorito")
                }
            }
        }
    }
}
