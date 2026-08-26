// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaAfegirProducte.kt
// Descripció:    Activity per a la creació i registre de nous productes a la carta
//                del restaurant. Permet assignar el nom, preu, descripció,
//                estat de disponibilitat i la categoria corresponent via l'API REST.
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
import com.example.servitec_frontend.data.model.CreateProdcuteDTO
import com.example.servitec_frontend.repository.ProducteRepository
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import kotlin.Int
import kotlin.String

/**
 * Activity encarregada del formulari per afegir nous productes al catàleg del restaurant.
 * Carrega les categories existents des de la base de dades i envia les noves dades al backend.
 */
class PantallaAfegirProducte : AppCompatActivity() {

    // Components visuals de la interfície d'usuari
    private lateinit var etNomProducte: TextInputEditText
    private lateinit var etPreuProducte: TextInputEditText
    private lateinit var spinnerCategoria: AutoCompleteTextView
    private lateinit var btnGuardarProducte: MaterialButton
    private lateinit var descripcioProducte: TextInputEditText
    private lateinit var btnCancelar: MaterialButton
    private lateinit var btnTornar: MaterialButton
    private var categories: List<Categoria> = emptyList()

    // Repositoris per al tractament de dades de productes i categories
    private lateinit var repositoryProducte: ProducteRepository
    private lateinit var repositoryTaula: TaulaRepository

    /**
     * Inicialitza la pantalla de creació de productes, vincula les vistes i configura les accions dels botons.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_afegir_producte)

        // Inicialització dels repositoris i enllaç de components UI
        repositoryProducte = ProducteRepository(this)
        repositoryTaula = TaulaRepository(this)
        btnTornar = findViewById(R.id.btnTornar)
        etNomProducte = findViewById(R.id.etNomProducte)
        etPreuProducte = findViewById(R.id.etPreuProducte)
        descripcioProducte = findViewById(R.id.etDescripcioProducte)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        btnGuardarProducte = findViewById(R.id.btnGuardarProducte)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnTornar = findViewById(R.id.btnTornar)
        val switchDispoible = findViewById<SwitchMaterial>(R.id.switchDisponible)

        // Carregador inicial de les categories des del servidor
        carregarCategories()

        val categorias = arrayOf(categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
        spinnerCategoria.setAdapter(adapter)

        // Escoltador d'esdeveniments per guardar el nou producte
        btnGuardarProducte.setOnClickListener {
            val nom = etNomProducte.text.toString().trim()
            val preu = etPreuProducte.text.toString().trim().toDoubleOrNull()
            val desProd = descripcioProducte.text.toString().trim()
            val nomCategoria = spinnerCategoria.text.toString().trim()
            val disponible = switchDispoible.isChecked
            val idCategoria = obtIdCategoria(nomCategoria)

            // Desactivació del botó per evitar enviaments duplicats
            btnGuardarProducte.isEnabled = false

            // Validació de dades d'entrada
            if (nom.isEmpty() || preu == null || idCategoria == null) {
                btnGuardarProducte.isEnabled = true
                Toast.makeText(this, "Si us plau, omple tots els camps correctament", Toast.LENGTH_SHORT).show()
            } else {
                val producteCrear = CreateProdcuteDTO(
                    nom = nom,
                    descripcio = desProd,
                    preu = preu,
                    actiu = disponible,
                    idCategoria = idCategoria
                )

                // Enviament asíncron del nou producte al backend
                lifecycleScope.launch {
                    repositoryProducte.crearProducte(producteCrear)
                }
                Toast.makeText(this, "Producte $nom ($desProd) creat correctament", Toast.LENGTH_SHORT).show()
                btnGuardarProducte.isEnabled = true
                finish()
            }
        }

        btnTornar.setOnClickListener {
            finish()
        }

        btnCancelar?.setOnClickListener {
            finish()
        }
    }

    /**
     * Obté les categories registrades des de la BDD i actualitza l'adaptador del menú desplegable.
     */
    private fun carregarCategories() {
        lifecycleScope.launch {
            categories = repositoryTaula.obtenirCategories() ?: emptyList()

            // Extracció dels noms de les categories per a l'adaptador visual
            val nomsCategories = categories.map { it.nom }
            val adapter = ArrayAdapter(
                this@PantallaAfegirProducte,
                android.R.layout.simple_dropdown_item_1line,
                nomsCategories
            )

            spinnerCategoria.setAdapter(adapter)
        }
    }

    /**
     * Cerca l'identificador únic d'una categoria a partir del seu nom seleccionat al desplegable.
     */
    private fun obtIdCategoria(nomCategoria: String): Int? {
        return categories.find { it.nom == nomCategoria }?.idCategoria
    }
}