import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference

@RequiresApi(Build.VERSION_CODES.O)

//https://stackoverflow.com/questions/7849067/how-can-i-convert-a-string-into-a-gzip-base64-string
fun compressText(text: String): String {
    val byteArray = text.toByteArray(Charsets.UTF_8)
    val byteArrayOutputStream = java.io.ByteArrayOutputStream()
    //GZIPOutputStream gzip = new GZIPOutputStream(b64os); équivalent en dessosu
    val gzipOutputStream = java.util.zip.GZIPOutputStream(byteArrayOutputStream)
    gzipOutputStream.write(byteArray)
    gzipOutputStream.close()
    val compressedBytes = byteArrayOutputStream.toByteArray()
    return java.util.Base64.getEncoder().encodeToString(compressedBytes)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookForm(booksRef: DatabaseReference) {
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
                    val compressedSummary = compressText(summary)
                    val newBook = Book(bookId, title, author, year, compressedSummary)
                    booksRef.child(bookId).setValue(newBook)
                    message = "Libro añadido !"
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
