// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaLogin.kt
// Descripció:    Activity que gestiona la pantalla d inici de sessió de l aplicació.
//                Valida les credencials contra el backend i enruta segons el rol.
// ============================================================================

package com.example.servitec_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.servitec_frontend.R
import com.example.servitec_frontend.repository.UsuariRepository

/**
 * Activity principal d autenticació de l aplicació ServiTec.
 */
class PantallaLogin : AppCompatActivity() {

    private lateinit var userRepository: UsuariRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_login)

        userRepository = UsuariRepository(this)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etUser = findViewById<EditText>(R.id.idUsuari)
        val etPass = findViewById<EditText>(R.id.idContrasenya)
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)

        btnLogin.setOnClickListener {
            val user = etUser.text.toString()
            val pass = etPass.text.toString()

            userRepository.loginUser(user, pass) { usuari, error ->
                if (usuari != null) {
                    Toast.makeText(
                        this,
                        "Hola, ${usuari.nomUsuari}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Guardem les dades d usuari i el token JWT
                    sharedPreferences.edit {
                        putInt("idUsuari", usuari.idUsuari)
                        putString("rolUsuari", usuari.rol.toString())
                        putBoolean("esAdmin", usuari.admin)
                        putString("jwt_token", usuari.token)
                    }

                    // Enrutament segons el rol
                    val intent = when (usuari.rol.toString().lowercase()) {
                        "admin" -> Intent(this, PantallaGerent::class.java)
                        "1", "cambrer", "camarero" -> Intent(this, PantallaPanell::class.java)
                        else -> Intent(this, PantallaCuina::class.java)
                    }

                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        error ?: "Error de connexió",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}