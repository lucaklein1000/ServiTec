package com.example.servitec_frontend

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PantallaLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_login)

        val txtUser = findViewById<EditText>(R.id.idUsuari)
        val txtPass = findViewById<EditText>(R.id.idContrasenya)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val usuario = txtUser.text.toString()
            val password = txtPass.text.toString()


            if (usuario == "admin" && password == "1234") {


                val intent = Intent(this, PantallaPanell::class.java)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

    }
}