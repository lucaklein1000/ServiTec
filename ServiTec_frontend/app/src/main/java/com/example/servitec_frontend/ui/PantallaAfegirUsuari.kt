// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaAfegirUsuari.kt
// Descripció:    Activity per a la creació i alta de nous usuaris al sistema.
//                Permet introduir les credencials, assignar rols (Cambrer, Cuiner,
//                Admin/Gerent) i registrar l'usuari a la base de dades mitjançant l'API REST.
// ============================================================================

package com.example.servitec_frontend.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.CreateUsuariDTO
import com.example.servitec_frontend.repository.UsuariRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Activity encarregada del formulari d'alta de nous usuaris personalitzats.
 * Gestiona la selecció de rols, la validació dels camps i la comunicació amb el repositori d'usuaris.
 */
class PantallaAfegirUsuari : AppCompatActivity() {

    // Components visuals del formulari
    private lateinit var etNomUsuari: TextInputEditText
    private lateinit var etPasswordUsuari: TextInputEditText
    private lateinit var spinnerRol: AutoCompleteTextView
    private lateinit var btnGuardarUsuari: MaterialButton
    private lateinit var btnCancelar: MaterialButton
    private lateinit var btnTornar: MaterialButton

    // Repositori per realitzar les operacions CRUD d'usuaris
    private lateinit var repository: UsuariRepository

    /**
     * Inicialitza la pantalla de creació d'usuaris, vincula les vistes i configura els desplegables i escoltadors.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_afegir_usuari)

        // Inicialització del repositori d'usuaris
        repository = UsuariRepository(this)

        initViews()
        setupDropdownRols()
        setupListeners()
    }

    /**
     * Vincula les variables locals amb els elements del layout XML corresponent.
     */
    private fun initViews() {
        etNomUsuari = findViewById(R.id.etNomUsuari)
        etPasswordUsuari = findViewById(R.id.etPasswordUsuari)
        spinnerRol = findViewById(R.id.spinnerRol)
        btnGuardarUsuari = findViewById(R.id.btnGuardarUsuari)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnTornar = findViewById(R.id.btnTornar)
    }

    /**
     * Configura el selector desplegable amb la llista predefinida de rols del sistema.
     */
    private fun setupDropdownRols() {
        val rols = arrayOf("Cambrer", "Cuiner", "Admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, rols)
        spinnerRol.setAdapter(adapter)
    }

    /**
     * Defineix els esdeveniments de clic per al desament del formulari i les accions de navegació/cancel·lació.
     */
    private fun setupListeners() {
        btnGuardarUsuari.setOnClickListener {
            val nom = etNomUsuari.text.toString().trim()
            val password = etPasswordUsuari.text.toString().trim()
            val rol = spinnerRol.text.toString().trim()

            // Validació de camps obligatoris buits
            if (nom.isEmpty() || password.isEmpty() || rol.isEmpty()) {
                Toast.makeText(this, "Si us plau, omple tots els camps", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Desactivació temporal del botó per evitar duplicitats en peticions concurrents
            btnGuardarUsuari.isEnabled = false

            // Determinació del permís d'administrador segons el rol triat
            val isAdmin = rol.equals("Gerent", ignoreCase = true) || rol.equals("Admin", ignoreCase = true)

            val usuariCrear = CreateUsuariDTO(
                nomUsuari = nom,
                contrasenya = password,
                actiu = true,
                admin = isAdmin,
                rol = rol
            )

            // Petició asíncrona al backend per a la creació de l'usuari
            lifecycleScope.launch {
                val exit = repository.crearUsuari(usuariCrear)
                if (exit != null) {
                    Toast.makeText(this@PantallaAfegirUsuari, "Usuari $nom ($rol) creat correctament", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@PantallaAfegirUsuari, "Error en crear l'usuari", Toast.LENGTH_SHORT).show()
                    btnGuardarUsuari.isEnabled = true
                }
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }

        btnTornar.setOnClickListener {
            finish()
        }
    }
}