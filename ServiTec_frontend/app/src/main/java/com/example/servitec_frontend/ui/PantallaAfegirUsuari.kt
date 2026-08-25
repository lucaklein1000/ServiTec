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

class PantallaAfegirUsuari : AppCompatActivity() {

    private lateinit var etNomUsuari: TextInputEditText
    private lateinit var etPasswordUsuari: TextInputEditText
    private lateinit var spinnerRol: AutoCompleteTextView
    private lateinit var btnGuardarUsuari: MaterialButton
    private lateinit var btnCancelar: MaterialButton
    private lateinit var btnTornar: MaterialButton
    private lateinit var repository: UsuariRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_afegir_usuari)

        repository = UsuariRepository(this)

        initViews()
        setupDropdownRols()
        setupListeners()
    }

    private fun initViews() {
        etNomUsuari = findViewById(R.id.etNomUsuari)
        etPasswordUsuari = findViewById(R.id.etPasswordUsuari)
        spinnerRol = findViewById(R.id.spinnerRol)
        btnGuardarUsuari = findViewById(R.id.btnGuardarUsuari)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnTornar = findViewById(R.id.btnTornar)
    }

    private fun setupDropdownRols() {
        val rols = arrayOf("Cambrer", "Cuiner", "Admin")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, rols)
        spinnerRol.setAdapter(adapter)
    }

    private fun setupListeners() {
        btnGuardarUsuari.setOnClickListener {
            val nom = etNomUsuari.text.toString().trim()
            val password = etPasswordUsuari.text.toString().trim()
            val rol = spinnerRol.text.toString().trim()

            if (nom.isEmpty() || password.isEmpty() || rol.isEmpty()) {
                Toast.makeText(this, "Si us plau, omple tots els camps", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnGuardarUsuari.isEnabled = false

            // Si el rol triat és "Gerent", establim el flag admin a true
            val isAdmin = rol.equals("Gerent", ignoreCase = true)

            val usuariCrear = CreateUsuariDTO(
                nomUsuari = nom,
                contrasenya = password,
                actiu = true,
                admin = isAdmin,
                rol = rol
            )

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