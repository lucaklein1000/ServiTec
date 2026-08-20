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

    // Componentes Eliminar Usuari
    private lateinit var autoCompleteEliminarUsuari: AutoCompleteTextView
    private lateinit var btnEliminarUsuari: MaterialButton

    // Componentes Modificar Usuari
    private lateinit var autoCompleteEditarUsuari: AutoCompleteTextView
    private lateinit var etEditNom: TextInputEditText
    private lateinit var etEditContrasenya: TextInputEditText
    private lateinit var spinnerEditRol: AutoCompleteTextView
    private lateinit var switchActiu: SwitchMaterial
    private lateinit var switchAdmin: SwitchMaterial
    private lateinit var btnGuardarCanvis: MaterialButton

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
        btnTornar = findViewById(R.id.btnTornar)

        // Inicializar vistas - Eliminar
        autoCompleteEliminarUsuari = findViewById(R.id.autoCompleteEliminarUsuari)
        btnEliminarUsuari = findViewById(R.id.btnEliminarUsuari)

        // Inicializar vistas - Modificar
        autoCompleteEditarUsuari = findViewById(R.id.autoCompleteEditarUsuari)
        etEditNom = findViewById(R.id.etEditNom)
        etEditContrasenya = findViewById(R.id.etEditContrasenya)
        spinnerEditRol = findViewById(R.id.spinnerEditRol)
        switchActiu = findViewById(R.id.switchActiu)
        switchAdmin = findViewById(R.id.switchAdmin)
        btnGuardarCanvis = findViewById(R.id.btnGuardarCanvis)

        // Configurar opciones del desplegable de Roles
        val rolsDisponibles = arrayOf("Cambrer", "Cuiner", "Gerent")
        val adapterRols = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, rolsDisponibles)
        spinnerEditRol.setAdapter(adapterRols)

        // Cargar lista de usuarios desde la API
        carregarUsuaris()

        // 1. EVENTO: Al seleccionar usuario en el desplegable, autollenar los 5 campos
        autoCompleteEditarUsuari.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position).toString()
            usuariSeleccionat = llistaUsuaris.find { it.nomUsuari == nomSeleccionat }

            usuariSeleccionat?.let { usuari ->
                etEditNom.setText(usuari.nomUsuari)
                etEditContrasenya.setText("") // Ajusta si la propiedad en tu DTO tiene otro nombre
                spinnerEditRol.setText(usuari.rol, false)
                switchActiu.isChecked = usuari.actiu
                switchAdmin.isChecked = usuari.admin
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
            val admin = switchAdmin.isChecked

            if (nom.isEmpty() || contrasenya.isEmpty() || rol.isEmpty()) {
                Toast.makeText(this, "Si us plau, omple tots els camps obligatoris", Toast.LENGTH_SHORT).show()
            } else {
                btnGuardarCanvis.isEnabled = false

                val usuariModificat = UpdateUsuariDTO(
                    nomUsuari = nom,
                    contrasenya = contrasenya,
                    rol = rol,
                    actiu = actiu,
                    admin = admin
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

        btnEliminarUsuari.setOnClickListener {
            val nomUsuari = autoCompleteEliminarUsuari.text.toString().trim()
            val idUsuari = obtIdUsuari(nomUsuari)

            if (nomUsuari.isEmpty() || idUsuari == null) {
                Toast.makeText(this, "Selecciona un usuari vàlid", Toast.LENGTH_SHORT).show()
            } else {
                btnEliminarUsuari.isEnabled = false
                lifecycleScope.launch {
                    val exito = repositoryUsuari.eliminarUsuari(idUsuari)
                    if (exito) {
                        Toast.makeText(this@PantallaGestionarPersonal, "Usuari $nomUsuari eliminat correctament", Toast.LENGTH_SHORT).show()
                        autoCompleteEliminarUsuari.setText("")
                        netejarFormulari()
                        carregarUsuaris()
                    } else {
                        Toast.makeText(this@PantallaGestionarPersonal, "Error en eliminar l'usuari", Toast.LENGTH_SHORT).show()
                    }
                    btnEliminarUsuari.isEnabled = true
                }
            }
        }


        btnTornar.setOnClickListener {
            finish()
        }
    }

    private fun carregarUsuaris() {
        lifecycleScope.launch {
            llistaUsuaris = repositoryUsuari.llistarUsuaris() ?: emptyList()
            val nomsUsuaris = llistaUsuaris.map { it.nomUsuari }

            val adapter = ArrayAdapter(this@PantallaGestionarPersonal, android.R.layout.simple_dropdown_item_1line, nomsUsuaris)
            autoCompleteEliminarUsuari.setAdapter(adapter)
            autoCompleteEditarUsuari.setAdapter(adapter)
        }
    }

    private fun obtIdUsuari(nomUsuari: String): Int? {
        return llistaUsuaris.find { it.nomUsuari == nomUsuari }?.idUsuari
    }

    private fun netejarFormulari() {
        usuariSeleccionat = null
        autoCompleteEditarUsuari.setText("")
        etEditNom.setText("")
        etEditContrasenya.setText("")
        spinnerEditRol.setText("")
        switchActiu.isChecked = false
        switchAdmin.isChecked = false
    }
}