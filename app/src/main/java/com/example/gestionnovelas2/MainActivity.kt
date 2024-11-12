package com.example.gestionnovelas2

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gestionnovelas2.ui.theme.GestionNovelas2Theme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GestionNovelas2Theme {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text("¡ Bienvenido !", fontSize = 45.sp, modifier = Modifier.padding(16.dp))
                    supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, BookListFragment())
                        .commit()
                }
            }
        }
    }
}
