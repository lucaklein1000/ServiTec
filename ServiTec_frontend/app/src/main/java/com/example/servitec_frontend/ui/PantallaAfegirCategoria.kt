package com.example.servitec_frontend.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.PostCategoriaDTO
import com.example.servitec_frontend.data.model.PutCategoriaDTO
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class PantallaAfegirCategoria : AppCompatActivity() {

    // Formulari 1: Crear Categoria
    private lateinit var etNovaCatNom: TextInputEditText
    private lateinit var etNovaCatDescripcio: TextInputEditText
    private lateinit var btnCrearCategoria: MaterialButton

    // Formulari 2: Modificar Categoria
    private lateinit var autoCompleteEditarCategoria: AutoCompleteTextView
    private lateinit var etEditCatNom: TextInputEditText
    private lateinit var etEditCatDescripcio: TextInputEditText
    private lateinit var btnGuardarCanvisCategoria: MaterialButton
    private lateinit var categoriaRepository: TaulaRepository
    // Navegació
    private lateinit var btnTornar: MaterialButton

    // Dades i Repositori
    private var llistaCategories: List<Categoria> = emptyList()
    private var categoriaSeleccionada: Categoria? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_afegir_categoria)

        categoriaRepository = TaulaRepository(this)

        // Binding Vistes
        btnTornar = findViewById(R.id.btnTornar)

        // Formulari Crear
        etNovaCatNom = findViewById(R.id.etNovaCatNom)
        etNovaCatDescripcio = findViewById(R.id.etNovaCatDescripcio)
        btnCrearCategoria = findViewById(R.id.btnCrearCategoria)

        // Formulari Editar
        autoCompleteEditarCategoria = findViewById(R.id.autoCompleteEditarCategoria)
        etEditCatNom = findViewById(R.id.etEditCatNom)
        etEditCatDescripcio = findViewById(R.id.etEditCatDescripcio)
        btnGuardarCanvisCategoria = findViewById(R.id.btnGuardarCanvisCategoria)

        carregarCategories()

        // 1. CREAR CATEGORIA
        btnCrearCategoria.setOnClickListener {
            val nom = etNovaCatNom.text.toString().trim()
            val descripcio = etNovaCatDescripcio.text.toString().trim()

            if (nom.isEmpty()) {
                Toast.makeText(this, "El nom de la categoria és obligatori", Toast.LENGTH_SHORT).show()
            } else {
                btnCrearCategoria.isEnabled = false

                val novaCategoria = PostCategoriaDTO(
                    postNom = nom,
                    postDescripcio = descripcio
                )

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

        // 2. SELECCIONAR CATEGORIA A EDITAR
        autoCompleteEditarCategoria.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position).toString()
            categoriaSeleccionada = llistaCategories.find { it.nom == nomSeleccionat }

            categoriaSeleccionada?.let { categoria ->
                etEditCatNom.setText(categoria.nom)
                etEditCatDescripcio.setText(categoria.descripcio)
            }
        }

        // 3. GUARDAR CANVIS CATEGORIA
        btnGuardarCanvisCategoria.setOnClickListener {
            val categoria = categoriaSeleccionada

            if (categoria == null) {
                Toast.makeText(this, "Selecciona una categoria de la llista primer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nom = etEditCatNom.text.toString().trim()
            val descripcio = etEditCatDescripcio.text.toString().trim()

            if (nom.isEmpty()) {
                Toast.makeText(this, "El nom no pot estar buit", Toast.LENGTH_SHORT).show()
            } else {
                btnGuardarCanvisCategoria.isEnabled = false

                val categoriaModificada = PutCategoriaDTO(
                    putNom = nom,
                    putDescripcio = descripcio
                )

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

    private fun netejarFormulariEdicio() {
        categoriaSeleccionada = null
        autoCompleteEditarCategoria.setText("")
        etEditCatNom.setText("")
        etEditCatDescripcio.setText("")
    }
}