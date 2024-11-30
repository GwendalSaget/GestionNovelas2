import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream


@RequiresApi(Build.VERSION_CODES.O)
fun decompressText(compressedText: String): String {
    val compressedBytes = java.util.Base64.getDecoder().decode(compressedText)
    val byteArrayInputStream = ByteArrayInputStream(compressedBytes)
    val gzipInputStream = GZIPInputStream(byteArrayInputStream)
    val decompressedBytes = gzipInputStream.readBytes()
    return String(decompressedBytes, Charsets.UTF_8)
}

data class Book(
    val id: String? = null,
    val title: String,
    val author: String,
    val year: String,
    var summary: String,
    var isfav: Boolean = false
) {
    constructor() : this(id = null, title = "", author = "", year = "", summary = "", isfav = false)
}

class BookListFragment : Fragment() {
    private lateinit var booksRef: DatabaseReference
    private var bookList by mutableStateOf(listOf<Book>())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        booksRef = FirebaseDatabase.getInstance().getReference("books")

        booksRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<Book>()
                snapshot.children.forEach {
                    val book = it.getValue(Book::class.java)?.copy(id = it.key)
                    if (book != null) {
                        val decompressedSummary = decompressText(book.summary)
                        book.summary = decompressedSummary
                        books.add(book)
                    }
                }
                bookList = books
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        return ComposeView(requireContext()).apply {
            setContent {
                LazyColumn(modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth()
                    .padding(top = 380.dp)
                    .padding(50.dp)){
                    items(bookList.sortedByDescending { it.isfav }) { book ->
                        BookItem(
                            book = book,
                            onDelete = {
                                book.id?.let { booksRef.child(it).removeValue() }
                            },
                            onFav = {
                                book.id?.let {
                                    booksRef.child(it).child("isfav").setValue(!book.isfav)
                                }
                            }
                        )
                    }
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
            .padding(vertical = 2.dp)
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
                Text("Borrar")
            }

            Button(
                onClick = onFav,
                colors = if (!book.isfav) {
                    ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                } else {
                    ButtonDefaults.buttonColors(containerColor = Color.Yellow)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Favorito")
            }
        }
    }
}
