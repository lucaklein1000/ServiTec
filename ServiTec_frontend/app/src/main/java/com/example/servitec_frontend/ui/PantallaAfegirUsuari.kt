package com.example.servitec_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.CrearUsuariDTO
import com.example.servitec_frontend.data.model.CreateLiniaComandaDTO
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.example.servitec_frontend.repository.UsuariRepository
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import kotlin.String

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
        etNomUsuari = findViewById(R.id.etNomUsuari)
        etPasswordUsuari = findViewById(R.id.etPasswordUsuari)
        spinnerRol = findViewById(R.id.spinnerRol)
        btnGuardarUsuari = findViewById(R.id.btnGuardarUsuari)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnTornar = findViewById(R.id.btnTornar)
        val switchEsGerent = findViewById<SwitchMaterial>(R.id.switchEsGerent)

        val rols = arrayOf("Cambrer", "Cuiner")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, rols)
        spinnerRol.setAdapter(adapter)

        btnGuardarUsuari.setOnClickListener {
            val nom = etNomUsuari.text.toString().trim()
            val password = etPasswordUsuari.text.toString().trim()
            var rol = spinnerRol.text.toString().trim()
            val esGerent = switchEsGerent.isChecked
            btnGuardarUsuari.isEnabled = false

            if (nom.isEmpty() || password.isEmpty() || rol.isEmpty()) {
                Toast.makeText(this, "Si us plau, omple tots els camps", Toast.LENGTH_SHORT).show()
            } else {
                if (esGerent) rol = "Admin"
                val usuariCrear = CrearUsuariDTO(
                    postNomUsuari = nom,
                    postContrasenya = password,
                    postActiu = true,
                    postAdmin = esGerent,
                    postRol = rol
                )
                lifecycleScope.launch {
                    repository.crearUsuari(usuariCrear)
                }
            }

            Toast.makeText(this, "Usuari $nom ($rol) creat correctament", Toast.LENGTH_SHORT)
                .show()

            btnGuardarUsuari.isEnabled = true
            finish()
        }

        btnTornar.setOnClickListener {
           finish()
        }
    }


}