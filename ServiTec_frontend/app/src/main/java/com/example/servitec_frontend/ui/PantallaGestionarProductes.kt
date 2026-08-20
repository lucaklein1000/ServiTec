package com.example.servitec_frontend.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.ProducteDTO
import com.example.servitec_frontend.data.model.UpdateProdcuteDTO
import com.example.servitec_frontend.repository.ProducteRepository
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlin.String

class PantallaGestionarProductes : AppCompatActivity() {
    private lateinit var btnEliminarProducte: MaterialButton
    private lateinit var btnGuardarCanvisProducte: MaterialButton
    private lateinit var btnTornar: MaterialButton
    private lateinit var autoCompleteEditarProducte: AutoCompleteTextView
    private lateinit var autoCompleteEliminarProducte : AutoCompleteTextView
    private var llistaProductes: List<ProducteDTO> = emptyList()
    private var llistaCategoria: List<Categoria> = emptyList()
    private var producteSeleccionat: ProducteDTO? = null
    private lateinit var etEditNomProducte: TextInputEditText
    private lateinit var etEditDescripcio: TextInputEditText
    private lateinit var etEditPreu: TextInputEditText
    private lateinit var spinnerEditCategoria: AutoCompleteTextView
    private lateinit var switchProducteActiu: SwitchMaterial
    private lateinit var producteRepository: ProducteRepository
    private lateinit var categoriaRepository: TaulaRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_gestionar_productes)

        producteRepository = ProducteRepository(this)
        categoriaRepository = TaulaRepository(this)
        btnEliminarProducte = findViewById(R.id.btnEliminarProducte)
        btnTornar = findViewById(R.id.btnTornar)
        autoCompleteEditarProducte = findViewById(R.id.autoCompleteEditarProducte)
        autoCompleteEliminarProducte = findViewById(R.id.autoCompleteEliminarProducte)
        etEditNomProducte = findViewById(R.id.etEditNomProducte)
        etEditPreu = findViewById(R.id.etEditPreu)
        spinnerEditCategoria = findViewById(R.id.spinnerEditCategoria)
        etEditDescripcio = findViewById(R.id.etEditDescripcio)
        switchProducteActiu = findViewById(R.id.switchProducteActiu)
        btnGuardarCanvisProducte = findViewById(R.id.btnGuardarCanvisProducte)

        carregarProductes()
        carregarCategories(spinnerEditCategoria)

        btnEliminarProducte.setOnClickListener {
            val nomProducte = autoCompleteEliminarProducte.text.toString().trim()
            val idProducte = obtIdProducte(nomProducte)

            if (nomProducte.isEmpty() || idProducte == null) {
                Toast.makeText(this, "Selecciona un producte vàlid", Toast.LENGTH_SHORT).show()
            } else {
                btnEliminarProducte.isEnabled = false
                lifecycleScope.launch {
                    val exito = producteRepository.eliminarProducte(idProducte)
                    if (exito) {
                        Toast.makeText(this@PantallaGestionarProductes, "Usuari $nomProducte eliminat correctament", Toast.LENGTH_SHORT).show()
                        autoCompleteEliminarProducte.setText("")
                        netejarFormulari()
                        carregarProductes()
                    } else {
                        Toast.makeText(this@PantallaGestionarProductes, "Error en eliminar el producte", Toast.LENGTH_SHORT).show()
                    }
                    btnEliminarProducte.isEnabled = true
                }
            }
        }


        autoCompleteEditarProducte.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position).toString()
            producteSeleccionat = llistaProductes.find { it.nom == nomSeleccionat }

            producteSeleccionat?.let { ProducteDTO ->
                etEditNomProducte.setText(ProducteDTO.nom)
                etEditDescripcio.setText(ProducteDTO.descripcio)
                etEditPreu.setText(ProducteDTO.preu.toString())
                val nomCategoriaActual = llistaCategoria.find { it.idCategoria == ProducteDTO.idCategoria }?.nom ?: ""
                spinnerEditCategoria.setText(nomCategoriaActual, false)
                switchProducteActiu.isChecked = ProducteDTO.actiu
            }
        }

        btnGuardarCanvisProducte.setOnClickListener {
            val producte = producteSeleccionat

            if (producte == null) {
                Toast.makeText(this, "Selecciona un producte de la llista primer", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val nom = etEditNomProducte.text.toString().trim()
            val descripcio = etEditDescripcio.text.toString().trim()
            val preu = etEditPreu.text.toString().trim().toDoubleOrNull()
            val categoria = spinnerEditCategoria.text.toString().trim()
            val actiu = switchProducteActiu.isChecked
            val idCategoria = obtIdCategoria(categoria)

            if (nom.isEmpty() || descripcio.isEmpty() || categoria.isEmpty() || preu == null || idCategoria == null) {
                Toast.makeText(
                    this,
                    "Si us plau, omple tots els camps obligatoris",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                btnGuardarCanvisProducte.isEnabled = false

                val producteModificat = UpdateProdcuteDTO(
                    nom = nom,
                    descripcio = descripcio,
                    preu = preu,
                    actiu = actiu,
                    idCategoria = idCategoria
                )

                lifecycleScope.launch {
                    val exito = producteRepository.actualitzarProducte(producte.idProducte, producteModificat)
                    if (exito) {
                        Toast.makeText(
                            this@PantallaGestionarProductes,
                            "Producte $nom actualitzat correctament",
                            Toast.LENGTH_SHORT
                        ).show()
                        netejarFormulari()
                        carregarProductes()
                    } else {
                        Toast.makeText(
                            this@PantallaGestionarProductes,
                            "Error en actualitzar el Producte",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    btnGuardarCanvisProducte.isEnabled = true
                }
            }
        }

        btnTornar.setOnClickListener {
            finish()
        }
    }

    private fun carregarProductes() {
        lifecycleScope.launch {
            llistaProductes = producteRepository.llistarProductes() ?: emptyList()
            val nomPrdocute = llistaProductes.map { it.nom }

            val adapter = ArrayAdapter(this@PantallaGestionarProductes, android.R.layout.simple_dropdown_item_1line, nomPrdocute)
            autoCompleteEliminarProducte.setAdapter(adapter)
            autoCompleteEditarProducte.setAdapter(adapter)
        }
    }

    private fun carregarCategories(spinnerEditCategoria : AutoCompleteTextView ) {
        lifecycleScope.launch {
            llistaCategoria = categoriaRepository.obtenirCategories() ?: emptyList()
            val nomCategoria = llistaCategoria.map { it.nom }

            val adapter = ArrayAdapter(
                this@PantallaGestionarProductes,
                android.R.layout.simple_dropdown_item_1line,
                nomCategoria
            )

            spinnerEditCategoria.setAdapter(adapter)
        }
    }

    private fun obtIdProducte(nomProducte: String): Int? {
        return llistaProductes.find { it.nom == nomProducte }?.idProducte
    }

    private fun obtIdCategoria(nomCategoria: String): Int? {
        return llistaCategoria.find { it.nom == nomCategoria }?.idCategoria
    }

    private fun netejarFormulari() {
        producteSeleccionat = null
        autoCompleteEditarProducte.setText("")
        etEditNomProducte.setText("")
        etEditDescripcio.setText("")
        etEditPreu.setText("")
        spinnerEditCategoria.setText("")
        switchProducteActiu.isChecked = false
    }
}