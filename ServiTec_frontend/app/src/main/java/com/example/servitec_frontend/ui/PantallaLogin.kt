// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaLogin.kt
// Descripció:    Activity que gestiona la pantalla d'inici de sessió de l'aplicació.
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
import com.example.servitec_frontend.data.model.LoginResponse
import com.example.servitec_frontend.repository.UsuariRepository

/**
 * Activity principal d'autenticació de l'aplicació ServiTec.
 */
class PantallaLogin : AppCompatActivity() {

    private lateinit var userRepository: UsuariRepository

    /**
     * Inicialitza la pantalla de login, obté les referències dels elements visuals i gestiona l'esdeveniment d'autenticació.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_login)

        // Inicialització del repositori d'usuaris i referències visuals
        userRepository = UsuariRepository(this)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val etUser = findViewById<EditText>(R.id.idUsuari)
        val etPass = findViewById<EditText>(R.id.idContrasenya)
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)

        // Configuració de l'esdeveniment de clic per al botó d'inici de sessió
        btnLogin.setOnClickListener {
            val user = etUser.text.toString().trim()
            val pass = etPass.text.toString().trim()

            // Validació de camps buits
            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Si us plau, omple tots els camps", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnLogin.isEnabled = false

            // Petició d'autenticació al backend via repository
            userRepository.loginUser(user, pass) { response: LoginResponse?, error: String? ->
                btnLogin.isEnabled = true

                if (response != null) {
                    Toast.makeText(
                        this,
                        "Hola, ${response.nomUsuari}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Emmagatzematge de la sessió i token JWT a SharedPreferences
                    sharedPreferences.edit {
                        putInt("idUsuari", response.idUsuari)
                        putString("nomUsuari", response.nomUsuari)
                        putString("rolUsuari", response.rol)
                        putBoolean("esAdmin", response.admin)
                        putString("jwt_token", response.token)
                    }

                    // Enrutament dinàmic de l'activitat segons el rol de l'usuari autenticat
                    val intent = when (response.rol.lowercase()) {
                        "admin", "gerent" -> Intent(this, PantallaGerent::class.java)
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