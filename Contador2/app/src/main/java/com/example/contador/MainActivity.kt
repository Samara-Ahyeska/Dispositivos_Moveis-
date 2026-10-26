package com.example.contador
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    private var contador = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val textContador = findViewById<TextView>(R.id.textContador)
        val btnAumentar = findViewById<Button>(R.id.btnAumentar)
        val btnDiminuir = findViewById<Button>(R.id.btnDiminuir)
        val btnZerar = findViewById<Button>(R.id.btnZerar)


        textContador.setOnClickListener {
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_NUMBER
            input.setText(contador.toString())
            input.setSelection(input.text.length)

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alterar Contador")
            builder.setMessage("Digite o novo valor:")
            builder.setView(input)

            builder.setPositiveButton("Confirmar") { dialog, _ ->
                val textoDigitado = input.text.toString()
                if (textoDigitado.isNotEmpty()) {
                    val numeroDigitado = textoDigitado.toInt()


                    if (numeroDigitado < 0) {
                        Toast.makeText(this, "Não é possível inserir um número negativo", Toast.LENGTH_SHORT).show()
                    } else {
                        contador = numeroDigitado
                        textContador.text = contador.toString()
                    }
                }
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }


        btnAumentar.setOnClickListener {
            contador++
            textContador.text = contador.toString()
        }


        btnDiminuir.setOnClickListener {
            if (contador > 0) {
                contador--
                textContador.text = contador.toString()
            }
        }


        btnZerar.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmação")
            builder.setMessage("Tem certeza de que deseja zerar o contador?")

            builder.setPositiveButton("Sim") { _, _ ->
                contador = 0
                textContador.text = contador.toString()
            }

            builder.setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }

            builder.show()
        }
    }
}