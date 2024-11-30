package com.example.gestionnovelas2

import BookForm
import BookListFragment
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestionnovelas2.ui.theme.GestionNovelas2Theme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {
    private lateinit var booksRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        booksRef = FirebaseDatabase.getInstance().getReference("books")

        setContent {
            GestionNovelas2Theme {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {
                    Text("Gestion de Novelas", fontSize = 30.sp, modifier = Modifier.padding(bottom = 16.dp))
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

