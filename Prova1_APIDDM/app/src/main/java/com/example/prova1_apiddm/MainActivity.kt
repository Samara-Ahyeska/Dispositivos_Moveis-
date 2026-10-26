package com.example.prova1_apiddm

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var recyclerViewBooks: RecyclerView
    private lateinit var bookAdapter: BookAdapter

    private val apiKey = "AIzaSyBigHhyj1iX4dh6j3xn2g4NBrNBqnAnLgo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSearch = findViewById(R.id.etSearch)
        btnSearch = findViewById(R.id.btnSearch)
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks)

        recyclerViewBooks.layoutManager = LinearLayoutManager(this)
        bookAdapter = BookAdapter(emptyList())
        recyclerViewBooks.adapter = bookAdapter

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchBooks(query)
            } else {
                etSearch.error = "Digite um termo para busca"
            }
        }
    }

    private fun searchBooks(query: String) {
        btnSearch.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.searchBooks(query, apiKey)

                withContext(Dispatchers.Main) {
                    btnSearch.isEnabled = true
                    if (response.isSuccessful) {
                        val bookModels = response.body()
                        val items = bookModels?.items

                        if (!items.isNullOrEmpty()) {
                            bookAdapter.updateData(items)
                        } else {
                            bookAdapter.updateData(emptyList())
                            Toast.makeText(this@MainActivity, "Nenhum livro encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        when (response.code()) {
                            503 -> Toast.makeText(this@MainActivity, "Serviço temporariamente indisponível (Erro 503). Tente mais tarde.", Toast.LENGTH_LONG).show()
                            429 -> Toast.makeText(this@MainActivity, "Muitas requisições (Erro 429). Aguarde um instante.", Toast.LENGTH_LONG).show()
                            else -> Toast.makeText(this@MainActivity, "Erro na resposta: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    btnSearch.isEnabled = true
                    Toast.makeText(this@MainActivity, "Erro de conexão: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}