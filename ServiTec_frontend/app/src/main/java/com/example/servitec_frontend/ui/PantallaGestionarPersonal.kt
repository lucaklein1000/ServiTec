package com.example.servitec_frontend.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.UpdateUsuariDTO
import com.example.servitec_frontend.data.model.UsuariDTO
import com.example.servitec_frontend.repository.UsuariRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class PantallaGestionarPersonal : AppCompatActivity() {

    // Componentes Modificar Usuari
    private lateinit var autoCompleteEditarUsuari: AutoCompleteTextView
    private lateinit var etEditNom: TextInputEditText
    private lateinit var etEditContrasenya: TextInputEditText
    private lateinit var spinnerEditRol: AutoCompleteTextView
    private lateinit var switchActiu: SwitchMaterial
    private lateinit var btnGuardarCanvis: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    // Componentes Navegación
    private lateinit var btnTornar: MaterialButton

    // Datos y Estado
    private var llistaUsuaris: List<UsuariDTO> = emptyList()
    private var usuariSeleccionat: UsuariDTO? = null
    private lateinit var repositoryUsuari: UsuariRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_gestionar_personal)

        repositoryUsuari = UsuariRepository(this)

        initViews()
        setupListeners()
        setupDropdownRols()
        carregarUsuaris()
    }

    private fun initViews() {
        btnTornar = findViewById(R.id.btnTornar)
        autoCompleteEditarUsuari = findViewById(R.id.autoCompleteEditarUsuari)
        etEditNom = findViewById(R.id.etEditNom)
        etEditContrasenya = findViewById(R.id.etEditContrasenya)
        spinnerEditRol = findViewById(R.id.spinnerEditRol)
        switchActiu = findViewById(R.id.switchActiu)
        btnGuardarCanvis = findViewById(R.id.btnGuardarCanvis)
        btnCancelar = findViewById(R.id.btnCancelar)
    }

    private fun setupDropdownRols() {
        val rolsDisponibles = arrayOf("Cambrer", "Cuina", "Admin")
        val adapterRols = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, rolsDisponibles)
        spinnerEditRol.setAdapter(adapterRols)
    }

    private fun setupListeners() {
        btnTornar.setOnClickListener {
            finish()
        }

        btnCancelar.setOnClickListener {
            netejarFormulari()
        }

        // 1. EVENTO: Al seleccionar usuario en el desplegable, autollenar los campos
        autoCompleteEditarUsuari.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position).toString()
            usuariSeleccionat = llistaUsuaris.find { it.nomUsuari == nomSeleccionat }

            usuariSeleccionat?.let { usuari ->
                etEditNom.setText(usuari.nomUsuari)
                etEditContrasenya.setText("") // La contraseña no se muestra por seguridad
                spinnerEditRol.setText(usuari.rol, false)
                switchActiu.isChecked = usuari.actiu
            }
        }

        // 2. EVENTO: Guardar cambios del usuario modificado
        btnGuardarCanvis.setOnClickListener {
            val usuari = usuariSeleccionat

            if (usuari == null) {
                Toast.makeText(this, "Selecciona un usuari de la llista primer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nom = etEditNom.text.toString().trim()
            val contrasenya = etEditContrasenya.text.toString().trim()
            val rol = spinnerEditRol.text.toString().trim()
            val actiu = switchActiu.isChecked

            if (nom.isEmpty() || rol.isEmpty()) {
                Toast.makeText(this, "Si us plau, omple el nom i el rol", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnGuardarCanvis.isEnabled = false

            // Si el campo de contraseña está vacío, puedes pasar una cadena vacía
            // o manejar en tu backend que si llega vacía no la actualice.
            val usuariModificat = UpdateUsuariDTO(
                nomUsuari = nom,
                contrasenya = contrasenya,
                rol = rol,
                actiu = actiu,
                admin = usuari.admin // Mantenemos el valor original que ya tenía
            )

            lifecycleScope.launch {
                val exit = repositoryUsuari.actualitzarUsuari(usuari.idUsuari, usuariModificat)
                if (exit) {
                    Toast.makeText(this@PantallaGestionarPersonal, "Usuari $nom actualitzat correctament", Toast.LENGTH_SHORT).show()
                    netejarFormulari()
                    carregarUsuaris()
                } else {
                    Toast.makeText(this@PantallaGestionarPersonal, "Error en actualitzar l'usuari", Toast.LENGTH_SHORT).show()
                }
                btnGuardarCanvis.isEnabled = true
            }
        }
    }

    private fun carregarUsuaris() {
        lifecycleScope.launch {
            llistaUsuaris = repositoryUsuari.llistarUsuaris() ?: emptyList()
            val nomsUsuaris = llistaUsuaris.map { it.nomUsuari }

            val adapter = ArrayAdapter(this@PantallaGestionarPersonal, android.R.layout.simple_dropdown_item_1line, nomsUsuaris)
            autoCompleteEditarUsuari.setAdapter(adapter)
        }
    }

    private fun netejarFormulari() {
        usuariSeleccionat = null
        autoCompleteEditarUsuari.setText("")
        etEditNom.setText("")
        etEditContrasenya.setText("")
        spinnerEditRol.setText("")
        switchActiu.isChecked = false
    }
}