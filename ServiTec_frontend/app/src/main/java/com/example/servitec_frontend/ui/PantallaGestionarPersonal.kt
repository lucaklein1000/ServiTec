// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaGestionarPersonal.kt
// Descripció:    Activity per a la gestió del personal del restaurant. Permet
//                consultar, editar dades d'usuaris (nom, rol, estat actiu/inactiu,
//                contrasenya) i actualitzar la informació al backend.
// ============================================================================

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

/**
 * Activity encarregada de la gestió i modificació dels usuaris/personal del restaurant.
 * Permet seleccionar un empleat, canviar el seu nom, contrasenya, rol o estat, i desar els canvis.
 */
class PantallaGestionarPersonal : AppCompatActivity() {

    // Components visuals per a la modificació d'usuaris
    private lateinit var autoCompleteEditarUsuari: AutoCompleteTextView
    private lateinit var etEditNom: TextInputEditText
    private lateinit var etEditContrasenya: TextInputEditText
    private lateinit var spinnerEditRol: AutoCompleteTextView
    private lateinit var switchActiu: SwitchMaterial
    private lateinit var btnGuardarCanvis: MaterialButton
    private lateinit var btnCancelar: MaterialButton

    // Components de navegació
    private lateinit var btnTornar: MaterialButton

    // Gestió d'estat i dades
    private var llistaUsuaris: List<UsuariDTO> = emptyList()
    private var usuariSeleccionat: UsuariDTO? = null
    private lateinit var repositoryUsuari: UsuariRepository

    /**
     * Inicialitza la pantalla de gestió de personal, enllaça les vistes, configura els adaptadors i carregar els usuaris.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_gestionar_personal)

        // Inicialització del repositori d'usuaris
        repositoryUsuari = UsuariRepository(this)

        initViews()
        setupListeners()
        setupDropdownRols()
        carregarUsuaris()
    }

    /**
     * Vincula les variables locals amb els elements corresponents del layout XML.
     */
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

    /**
     * Inicialitza les opcions del desplegable de rols d'usuari disponibles al sistema.
     */
    private fun setupDropdownRols() {
        val rolsDisponibles = arrayOf("Cambrer", "Cuina", "Admin")
        val adapterRols = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, rolsDisponibles)
        spinnerEditRol.setAdapter(adapterRols)
    }

    /**
     * Assigna els escoltadors d'esdeveniments als botons i selectors de la interfície.
     */
    private fun setupListeners() {
        btnTornar.setOnClickListener {
            finish()
        }

        btnCancelar.setOnClickListener {
            netejarFormulari()
        }

        // Esdeveniment al seleccionar un usuari del desplegable per emplenar el formulari
        autoCompleteEditarUsuari.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position).toString()
            usuariSeleccionat = llistaUsuaris.find { it.nomUsuari == nomSeleccionat }

            usuariSeleccionat?.let { usuari ->
                etEditNom.setText(usuari.nomUsuari)
                etEditContrasenya.setText("") // La contrasenya es manté buida per seguretat
                spinnerEditRol.setText(usuari.rol, false)
                switchActiu.isChecked = usuari.actiu
            }
        }

        // Esdeveniment per desar els canvis de l'usuari seleccionat
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

            // Validació dels camps obligatoris
            if (nom.isEmpty() || rol.isEmpty()) {
                Toast.makeText(this, "Si us plau, omple el nom i el rol", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnGuardarCanvis.isEnabled = false

            // Construcció del DTO d'actualització d'usuari
            val usuariModificat = UpdateUsuariDTO(
                nomUsuari = nom,
                contrasenya = contrasenya,
                rol = rol,
                actiu = actiu,
                admin = usuari.admin // Es manté el valor original de permís d'administrador
            )

            // Petició asíncrona d'actualització al repositori
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

    /**
     * Obté la llista d'usuaris des del repositori i actualitza l'adaptador del cercador AutoComplete.
     */
    private fun carregarUsuaris() {
        lifecycleScope.launch {
            llistaUsuaris = repositoryUsuari.llistarUsuaris() ?: emptyList()
            val nomsUsuaris = llistaUsuaris.map { it.nomUsuari }

            // Assignació dels noms d'usuari a l'adaptador del cercador
            val adapter = ArrayAdapter(this@PantallaGestionarPersonal, android.R.layout.simple_dropdown_item_1line, nomsUsuaris)
            autoCompleteEditarUsuari.setAdapter(adapter)
        }
    }

    /**
     * Reinicia l'estat dels camps de text i desmarca l'usuari seleccionat.
     */
    private fun netejarFormulari() {
        // Esborrat del contingut dels camps del formulari
        usuariSeleccionat = null
        autoCompleteEditarUsuari.setText("")
        etEditNom.setText("")
        etEditContrasenya.setText("")
        spinnerEditRol.setText("")
        switchActiu.isChecked = false
    }
}