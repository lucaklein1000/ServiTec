// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaAfegirCategoria.kt
// Descripció:    Activity per a la gestió de les categories de productes del restaurant.
//                Permet realitzar tant la creació de noves categories com la
//                modificació i actualització de les existents mitjançant l'API REST.
// ============================================================================

package com.example.servitec_frontend.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.CreateCategoriaDTO
import com.example.servitec_frontend.data.model.UpdateCategoriaDTO
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Activity encarregada dels formularis de creació i edició de categories de productes.
 * Permet afegir noves categories i modificar els noms o descripcions de les existents.
 */
class PantallaAfegirCategoria : AppCompatActivity() {

    // Components visuals del formulari 1: Crear Categoria
    private lateinit var etNovaCatNom: TextInputEditText
    private lateinit var etNovaCatDescripcio: TextInputEditText
    private lateinit var btnCrearCategoria: MaterialButton

    // Components visuals del formulari 2: Modificar Categoria
    private lateinit var autoCompleteEditarCategoria: AutoCompleteTextView
    private lateinit var etEditCatNom: TextInputEditText
    private lateinit var etEditCatDescripcio: TextInputEditText
    private lateinit var btnGuardarCanvisCategoria: MaterialButton

    // Navegació i repositori
    private lateinit var btnTornar: MaterialButton
    private lateinit var categoriaRepository: TaulaRepository

    // Estat local de dades i categoria seleccionada
    private var llistaCategories: List<Categoria> = emptyList()
    private var categoriaSeleccionada: Categoria? = null

    /**
     * Inicialitza la pantalla de categories, enllaça els formularis i configura els escoltadors d'esdeveniments.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_afegir_categoria)

        // Inicialització del repositori de dades
        categoriaRepository = TaulaRepository(this)

        // Vinculació dels elements de la interfície d'usuari
        btnTornar = findViewById(R.id.btnTornar)

        // Formulari per a la creació
        etNovaCatNom = findViewById(R.id.etNovaCatNom)
        etNovaCatDescripcio = findViewById(R.id.etNovaCatDescripcio)
        btnCrearCategoria = findViewById(R.id.btnCrearCategoria)

        // Formulari per a l'edició
        autoCompleteEditarCategoria = findViewById(R.id.autoCompleteEditarCategoria)
        etEditCatNom = findViewById(R.id.etEditCatNom)
        etEditCatDescripcio = findViewById(R.id.etEditCatDescripcio)
        btnGuardarCanvisCategoria = findViewById(R.id.btnGuardarCanvisCategoria)

        // Carregador inicial de categories des del servidor
        carregarCategories()

        // Configuració de l'esdeveniment de creació de nova categoria
        btnCrearCategoria.setOnClickListener {
            val nom = etNovaCatNom.text.toString().trim()
            val descripcio = etNovaCatDescripcio.text.toString().trim()

            // Validació de camps obligatoris
            if (nom.isEmpty()) {
                Toast.makeText(this, "El nom de la categoria és obligatori", Toast.LENGTH_SHORT).show()
            } else {
                // Desactivació del botó per evitar peticions duplicades
                btnCrearCategoria.isEnabled = false

                val novaCategoria = CreateCategoriaDTO(
                    nom = nom,
                    descripcio = descripcio
                )

                // Petició asíncrona per a la inserció de la categoria
                lifecycleScope.launch {
                    val exit = categoriaRepository.crearCategoria(novaCategoria)
                    if (exit) {
                        Toast.makeText(
                            this@PantallaAfegirCategoria,
                            "Categoria $nom creada correctament",
                            Toast.LENGTH_SHORT
                        ).show()
                        etNovaCatNom.setText("")
                        etNovaCatDescripcio.setText("")
                        carregarCategories()
                    } else {
                        Toast.makeText(
                            this@PantallaAfegirCategoria,
                            "Error en crear la categoria",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    btnCrearCategoria.isEnabled = true
                }
            }
        }

        // Selecció d'una categoria existent a través del menú desplegable per a la seva edició
        autoCompleteEditarCategoria.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position).toString()
            categoriaSeleccionada = llistaCategories.find { it.nom == nomSeleccionat }

            // Carregament de les dades de la categoria als camps del formulari d'edició
            categoriaSeleccionada?.let { categoria ->
                etEditCatNom.setText(categoria.nom)
                etEditCatDescripcio.setText(categoria.descripcio)
            }
        }

        // Configuració de l'esdeveniment per guardar els canvis d'una categoria existent
        btnGuardarCanvisCategoria.setOnClickListener {
            val categoria = categoriaSeleccionada

            if (categoria == null) {
                Toast.makeText(this, "Selecciona una categoria de la llista primer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nom = etEditCatNom.text.toString().trim()
            val descripcio = etEditCatDescripcio.text.toString().trim()

            // Validació de dades buides
            if (nom.isEmpty()) {
                Toast.makeText(this, "El nom no pot estar buit", Toast.LENGTH_SHORT).show()
            } else {
                // Desactivació del botó durant el procés d'actualització
                btnGuardarCanvisCategoria.isEnabled = false

                val categoriaModificada = UpdateCategoriaDTO(
                    nom = nom,
                    descripcio = descripcio
                )

                // Petició asíncrona per actualitzar la categoria a la BDD
                lifecycleScope.launch {
                    val exito = categoriaRepository.actualitzarCategoria(categoria.idCategoria, categoriaModificada)
                    if (exito) {
                        Toast.makeText(
                            this@PantallaAfegirCategoria,
                            "Categoria $nom actualitzada correctament",
                            Toast.LENGTH_SHORT
                        ).show()
                        netejarFormulariEdicio()
                        carregarCategories()
                    } else {
                        Toast.makeText(
                            this@PantallaAfegirCategoria,
                            "Error en actualitzar la categoria",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    btnGuardarCanvisCategoria.isEnabled = true
                }
            }
        }

        btnTornar.setOnClickListener {
            finish()
        }
    }

    /**
     * Carrega la llista de categories des del servidor i actualitza l'adaptador del desplegable d'edició.
     */
    private fun carregarCategories() {
        lifecycleScope.launch {
            llistaCategories = categoriaRepository.obtenirCategories() ?: emptyList()
            val nomsCategories = llistaCategories.map { it.nom }

            val adapter = ArrayAdapter(
                this@PantallaAfegirCategoria,
                android.R.layout.simple_dropdown_item_1line,
                nomsCategories
            )

            autoCompleteEditarCategoria.setAdapter(adapter)
        }
    }

    /**
     * Neteja la selecció actual i buida els camps del formulari d'edició de categories.
     */
    private fun netejarFormulariEdicio() {
        categoriaSeleccionada = null
        autoCompleteEditarCategoria.setText("")
        etEditCatNom.setText("")
        etEditCatDescripcio.setText("")
    }
}