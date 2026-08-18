package com.example.servitec_frontend.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.PostProducteDTO
import com.example.servitec_frontend.repository.ProducteRepository
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlin.Int
import kotlin.String

class PantallaAfegirProducte : AppCompatActivity() {
    private lateinit var etNomProducte: TextInputEditText
    private lateinit var etPreuProducte: TextInputEditText
    private lateinit var spinnerCategoria: AutoCompleteTextView
    private lateinit var btnGuardarProducte: MaterialButton
    private lateinit var descripcioProducte: TextInputEditText
    private lateinit var btnCancelar: MaterialButton
    private lateinit var btnTornar: MaterialButton
    private var categories: List<Categoria> = emptyList()

    private val repositoryProducte = ProducteRepository()
    private val repositoryTaula = TaulaRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_afegir_producte)
        btnTornar = findViewById(R.id.btnTornar)
        etNomProducte = findViewById(R.id.etNomProducte)
        etPreuProducte = findViewById(R.id.etPreuProducte)
        descripcioProducte = findViewById(R.id.etDescripcioProducte)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        btnGuardarProducte = findViewById(R.id.btnGuardarProducte)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnTornar = findViewById(R.id.btnTornar)
        val switchDispoible = findViewById<SwitchMaterial>(R.id.switchDisponible)

        carregarCategories()

        val categorias = arrayOf(categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        spinnerCategoria.setAdapter(adapter)

        btnGuardarProducte.setOnClickListener {
            val nom = etNomProducte.text.toString().trim()
            val preu = etPreuProducte.text.toString().trim().toDouble()
            val desProd = descripcioProducte.text.toString().trim()
            val nomCategoria = spinnerCategoria.text.toString().trim()
            val disponible = switchDispoible.isChecked
            val idCategoria = obtIdCategoria(nomCategoria)

            btnGuardarProducte.isEnabled = false

            if (nom.isEmpty() || preu == null || idCategoria == null) {
                btnGuardarProducte.isEnabled = true
                Toast.makeText(this, "Si us plau, omple tots els camps", Toast.LENGTH_SHORT).show()
            } else {
                val producteCrear = PostProducteDTO(
                    postNom = nom,
                    postDescripcio = desProd,
                    postPreu = preu,
                    postActiu = disponible,
                    postIdCategoria = idCategoria
                )
                lifecycleScope.launch {
                    repositoryProducte.crearProdcute(producteCrear)
                }
                Toast.makeText(this, "Prodcute $nom ($desProd) creat correctament", Toast.LENGTH_SHORT)
                    .show()
                btnGuardarProducte.isEnabled = true
                finish()
            }
        }

        btnTornar.setOnClickListener {
            finish()
        }
    }

    private fun carregarCategories() {
        lifecycleScope.launch {
            categories = repositoryTaula.obtenirCategories() ?: emptyList()

            val nomsCategories = categories.map { it.nom }
            val adapter = ArrayAdapter(
                this@PantallaAfegirProducte,
                android.R.layout.simple_dropdown_item_1line,
                nomsCategories
            )

            spinnerCategoria.setAdapter(adapter)
        }
    }

    private fun obtIdCategoria(nomCategoria: String): Int? {
        return categories.find { it.nom == nomCategoria }?.idCategoria
    }
}