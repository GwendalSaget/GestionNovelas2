package com.example.gestionnovelas2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.gestionnovelas2.ui.theme.GestionNovelas2Theme

class SettingsPantalla : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            GestionNovelas2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    Fondo(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun Fondo(modifier: Modifier) {
    var selectedColor by remember { mutableStateOf(0) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var bookList by remember { mutableStateOf(listOf<Book>()) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = GetColor(selectedColor)
    ) {
        inter(
            modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
            onColorSelected = { selectedColor = it },
            firstName = firstName,
            lastName = lastName,
            age = age,
            onFirstNameChange = { firstName = it },
            onLastNameChange = { lastName = it },
            onAgeChange = { age = it },
            bookList = bookList,
            onBookListChange = { bookList = it }
        )
    }
}

@Composable
fun inter(
    modifier: Modifier,
    onColorSelected: (Int) -> Unit,
    firstName: String,
    lastName: String,
    age: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    bookList: List<Book>,
    onBookListChange: (List<Book>) -> Unit

) {
    var selectedColor2 by remember { mutableStateOf(0) }
    val context = LocalContext.current

    Column(modifier = Modifier.offset(0.dp, -55.dp)) {
        Text(
            text = "¡Elige el color de fondo!",
            color = Color.White,
            fontSize = 30.sp,
            modifier = modifier
        )
    }

    // Champs de texte pour le nom, prénom et âge
    Column(modifier = Modifier.offset(0.dp, 100.dp)) {
        TextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            label = { Text("Nombre") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = lastName,
            onValueChange = onLastNameChange,
            label = { Text("Appellido") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = age,
            onValueChange = onAgeChange,
            label = { Text("Edad") }
        )
    }

    Column(modifier = Modifier.offset(150.dp, 320.dp)) {
        Button(onClick = {
            SaveUserData(context, firstName, lastName, age)
            Toast.makeText(context, "Datos guardados", Toast.LENGTH_SHORT).show()
        }) {
            Text("Gardar nombre")
        }
    }

    Column(modifier = Modifier.offset(150.dp, 750.dp)) {
        Button(onClick = {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = stringResource(R.string.Principal2), fontSize = 20.sp)
        }
    }

    // Botones pour sélectionner différentes couleurs
    Column(modifier = Modifier.offset(75.dp, 500.dp)) {
        Button(
            onClick = {
                onColorSelected(1) // Sélectionne le rouge
                selectedColor2 = 1
                SaveSelectedColor(context, selectedColor2)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {}
    }

    Column(modifier = Modifier.offset(175.dp, 500.dp)) {
        Button(
            onClick = {
                onColorSelected(2) // Sélectionne le bleu
                selectedColor2 = 2
                SaveSelectedColor(context, selectedColor2)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {}
    }

    Column(modifier = Modifier.offset(275.dp, 500.dp)) {
        Button(
            onClick = {
                onColorSelected(3) // Sélectionne le gris
                selectedColor2 = 3
                SaveSelectedColor(context, selectedColor2)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
        ) {}
    }

    Column(modifier = Modifier.offset(75.dp, 600.dp)) {
        Button(
            onClick = {
                onColorSelected(4) // Sélectionne le vert
                selectedColor2 = 4
                SaveSelectedColor(context, selectedColor2)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {}
    }

    Column(modifier = Modifier.offset(175.dp, 600.dp)) {
        Button(
            onClick = {
                onColorSelected(5) // Sélectionne le jaune
                selectedColor2 = 5
                SaveSelectedColor(context, selectedColor2)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow)
        ) {}
    }

    Column(modifier = Modifier.offset(275.dp, 600.dp)) {
        Button(
            onClick = {
                onColorSelected(6) // Sélectionne le magenta
                selectedColor2 = 6
                SaveSelectedColor(context, selectedColor2)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Magenta)
        ) {}
    }
}


fun SaveSelectedColor(context: Context, color: Int) {
    val sharedPref = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putInt("SELECTED_COLOR", color)
        apply()
    }
}

// Función para obtener el color guardado en las preferencias
public fun getSelectedColor(context: Context): Int {
    val sharedPref = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    // Recupera el color guardado o devuelve 0 (gris) si no se ha seleccionado ninguno
    return sharedPref.getInt("SELECTED_COLOR", 0)
}

fun SaveUserData(context: Context, firstName: String, lastName: String, age: String) {
    val sharedPref = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    with(sharedPref.edit()) {
        putString("FIRST_NAME", firstName)
        putString("LAST_NAME", lastName)
        putString("AGE", age)
        apply()
    }
}

fun getUserData(context: Context): String {
    val sharedPref = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    val firstName = sharedPref.getString("FIRST_NAME", "") ?: ""
    val lastName = sharedPref.getString("LAST_NAME", "") ?: ""
    val age = sharedPref.getString("AGE", "") ?: ""

    return "Hola $firstName $lastName quien tiene $age años"
}


@Composable
fun GetColor(value: Int): Color {
    return when (value) {
        1 -> Color.Red
        2 -> Color.Blue
        3 -> Color.Gray
        4 -> Color.Green
        5 -> Color.Yellow
        6 -> Color.Magenta
        else -> Color.Gray
    }
}

@Preview(showBackground = true)
@Composable
fun GetColorPreview() {
    GestionNovelas2Theme {
        Fondo(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center))
    }
}
