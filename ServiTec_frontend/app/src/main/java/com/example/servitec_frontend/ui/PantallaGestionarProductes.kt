// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaGestionarProductes.kt
// Descripció:    Activity per a la gestió i modificació de productes del catàleg.
//                Permet cercar, editar propietats (nom, descripció, preu, categoria,
//                estat actiu/inactiu) i sincronitzar els canvis amb la base de dades.
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
import com.example.servitec_frontend.data.model.ProducteDTO
import com.example.servitec_frontend.data.model.UpdateProdcuteDTO
import com.example.servitec_frontend.repository.ProducteRepository
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Activity encarregada de la gestió i edició de productes del menú del restaurant.
 * Permet seleccionar un producte existent, modificar-ne les dades i actualitzar-lo al backend.
 */
class PantallaGestionarProductes : AppCompatActivity() {

    // Components de la interfície d'usuari
    private lateinit var btnTornar: MaterialButton
    private lateinit var btnCancelar: MaterialButton
    private lateinit var btnGuardarCanvisProducte: MaterialButton
    private lateinit var autoCompleteEditarProducte: AutoCompleteTextView
    private lateinit var etEditNomProducte: TextInputEditText
    private lateinit var etEditDescripcio: TextInputEditText
    private lateinit var etEditPreu: TextInputEditText
    private lateinit var spinnerEditCategoria: AutoCompleteTextView
    private lateinit var switchProducteActiu: SwitchMaterial

    // Col·leccions i estat local
    private var llistaProductes: List<ProducteDTO> = emptyList()
    private var llistaCategoria: List<Categoria> = emptyList()
    private var producteSeleccionat: ProducteDTO? = null

    // Repositoris de dades per a la comunicació amb l'API REST
    private lateinit var producteRepository: ProducteRepository
    private lateinit var categoriaRepository: TaulaRepository

    /**
     * Inicialitza la pantalla de gestió de productes, enllaça els components visuals i defineix la lògica dels botons i del selector.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_gestionar_productes)

        // Inicialització dels repositoris
        producteRepository = ProducteRepository(this)
        categoriaRepository = TaulaRepository(this)

        // Referències de les vistes de la interfície
        btnTornar = findViewById(R.id.btnTornar)
        btnCancelar = findViewById(R.id.btnCancelar)
        btnGuardarCanvisProducte = findViewById(R.id.btnGuardarCanvisProducte)
        autoCompleteEditarProducte = findViewById(R.id.autoCompleteEditarProducte)
        etEditNomProducte = findViewById(R.id.etEditNomProducte)
        etEditDescripcio = findViewById(R.id.etEditDescripcio)
        etEditPreu = findViewById(R.id.etEditPreu)
        spinnerEditCategoria = findViewById(R.id.spinnerEditCategoria)
        switchProducteActiu = findViewById(R.id.switchProducteActiu)

        // Càrrega inicial de productes i categories disponibles
        carregarProductes()
        carregarCategories(spinnerEditCategoria)

        // Esdeveniment al seleccionar un producte del desplegable
        autoCompleteEditarProducte.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position).toString()
            producteSeleccionat = llistaProductes.find { it.nom == nomSeleccionat }

            // Emplenat automàtic del formulari amb les dades del producte seleccionat
            producteSeleccionat?.let { producte ->
                etEditNomProducte.setText(producte.nom)
                etEditDescripcio.setText(producte.descripcio)
                etEditPreu.setText(producte.preu.toString())
                val nomCategoriaActual = llistaCategoria.find { it.idCategoria == producte.idCategoria }?.nom ?: ""
                spinnerEditCategoria.setText(nomCategoriaActual, false)
                switchProducteActiu.isChecked = producte.actiu
            }
        }

        // Acció: Desar els canvis del producte editat
        btnGuardarCanvisProducte.setOnClickListener {
            val producte = producteSeleccionat

            if (producte == null) {
                Toast.makeText(this, "Selecciona un producte de la llista primer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nom = etEditNomProducte.text.toString().trim()
            val descripcio = etEditDescripcio.text.toString().trim()
            val preu = etEditPreu.text.toString().trim().toDoubleOrNull()
            val categoria = spinnerEditCategoria.text.toString().trim()
            val actiu = switchProducteActiu.isChecked
            val idCategoria = obtIdCategoria(categoria)

            // Validació de camps requerits
            if (nom.isEmpty() || descripcio.isEmpty() || categoria.isEmpty() || preu == null || idCategoria == null) {
                Toast.makeText(this, "Si us plau, omple tots els camps obligatoris", Toast.LENGTH_SHORT).show()
            } else {
                btnGuardarCanvisProducte.isEnabled = false

                val producteModificat = UpdateProdcuteDTO(
                    nom = nom,
                    descripcio = descripcio,
                    preu = preu,
                    actiu = actiu,
                    idCategoria = idCategoria
                )

                // Petició asíncrona d'actualització al servidor
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

        // Acció: Cancel·lar l'edició i netejar el formulari
        btnCancelar.setOnClickListener {
            netejarFormulari()
        }

        // Acció: Tornar a la pantalla anterior
        btnTornar.setOnClickListener {
            finish()
        }
    }

    /**
     * Obte la llista actualitzada de productes des del repositori i configura l'adaptador del cercador.
     */
    private fun carregarProductes() {
        lifecycleScope.launch {
            llistaProductes = producteRepository.llistarProductes() ?: emptyList()
            val nomsProductes = llistaProductes.map { it.nom }

            // Assignació de l'adaptador amb els noms dels productes al cercador AutoComplete
            val adapter = ArrayAdapter(
                this@PantallaGestionarProductes,
                android.R.layout.simple_dropdown_item_1line,
                nomsProductes
            )
            autoCompleteEditarProducte.setAdapter(adapter)
        }
    }

    /**
     * Carrega les categories disponibles des del repositori i configura el desplegable de selecció de categoria.
     */
    private fun carregarCategories(spinnerEditCategoria: AutoCompleteTextView) {
        lifecycleScope.launch {
            llistaCategoria = categoriaRepository.obtenirCategories() ?: emptyList()
            val nomCategoria = llistaCategoria.map { it.nom }

            // Assignació de les categories a l'adaptador del desplegable
            val adapter = ArrayAdapter(
                this@PantallaGestionarProductes,
                android.R.layout.simple_dropdown_item_1line,
                nomCategoria
            )
            spinnerEditCategoria.setAdapter(adapter)
        }
    }

    /**
     * Cerca i retorna l'identificador numèric de la categoria a partir del seu nom.
     */
    private fun obtIdCategoria(nomCategoria: String): Int? {
        // Cerca per nom a la llista local de categories
        return llistaCategoria.find { it.nom == nomCategoria }?.idCategoria
    }

    /**
     * Restableix tots els camps del formulari i desmarca la selecció del producte actual.
     */
    private fun netejarFormulari() {
        // Reinici de l'estat local i esborrat dels camps de text
        producteSeleccionat = null
        autoCompleteEditarProducte.setText("")
        etEditNomProducte.setText("")
        etEditDescripcio.setText("")
        etEditPreu.setText("")
        spinnerEditCategoria.setText("")
        switchProducteActiu.isChecked = false
    }
}