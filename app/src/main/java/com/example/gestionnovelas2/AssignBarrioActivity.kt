package com.example.gestionnovelas2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class AssignBarrioActivity : AppCompatActivity() {

    private lateinit var booksRef: DatabaseReference
    private var bookList: MutableList<String> = mutableListOf()
    private var selectedBookId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_barrio)

        booksRef = FirebaseDatabase.getInstance().getReference("books")

        val spinner = findViewById<Spinner>(R.id.book_spinner)
        val tetuanButton = findViewById<Button>(R.id.button_tetuan)
        val moncloaButton = findViewById<Button>(R.id.button_moncloa)
        val solButton = findViewById<Button>(R.id.button_sol)
        val retiroButton = findViewById<Button>(R.id.button_retiro)
        val latinaButton = findViewById<Button>(R.id.button_latina)
        val backButton = findViewById<Button>(R.id.button_back)

        booksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear()
                val bookNames = mutableListOf<String>()
                snapshot.children.forEach {
                    val book = it.getValue(Book::class.java)
                    book?.id?.let { id ->
                        bookNames.add(book.title)
                        bookList.add(id)
                    }
                }

                val adapter = ArrayAdapter(
                    this@AssignBarrioActivity,
                    android.R.layout.simple_spinner_item,
                    bookNames
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AssignBarrioActivity, "Error : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedBookId = bookList[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedBookId = null
            }
        }

        tetuanButton.setOnClickListener { assignBarrio("Tetuan") }
        moncloaButton.setOnClickListener { assignBarrio("Moncloa") }
        solButton.setOnClickListener { assignBarrio("Sol") }
        retiroButton.setOnClickListener { assignBarrio("Retiro") }
        latinaButton.setOnClickListener { assignBarrio("Latina") }
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  }
    }

    private fun assignBarrio(barrio: String) {
        if (selectedBookId != null) {
            booksRef.child(selectedBookId!!).child("barrio").setValue(barrio)
            Toast.makeText(this, "Barrio = $barrio", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Elegir un libro", Toast.LENGTH_SHORT).show()
        }
    }
}
