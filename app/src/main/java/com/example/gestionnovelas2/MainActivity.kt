package com.example.gestionnovelas2

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestionnovelas2.ui.theme.GestionNovelas2Theme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var booksRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        booksRef = FirebaseDatabase.getInstance().getReference("books")

        setContent {
            GestionNovelas2Theme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(this@MainActivity, AssignBarrioActivity::class.java)
                            startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text("Mapa de barrios")
                    }

                    Text(
                        "Gestion de Novelas",
                        fontSize = 30.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    BookForm(booksRef = booksRef)
                    Spacer(modifier = Modifier.height(24.dp))
                    supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, BookListFragment())
                        .commit()
                }
            }
        }
    }
}
