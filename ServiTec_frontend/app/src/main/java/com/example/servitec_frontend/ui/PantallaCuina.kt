// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaCuina.kt
// Descripció:    Activity per a la pantalla de cuina. Mostra les comandes en
//                curs en format de tiquets mitjançant un refresc periòdic.
// ============================================================================

package com.example.servitec_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.repository.TaulaRepository
import com.example.servitec_frontend.ui.adapters.CuinaAdapter
import kotlinx.coroutines.launch

/**
 * Activity que gestiona la vista de comandes per al personal de cuina.
 *
 * Realitza peticions periòdiques al backend per mantenir actualitzada la llista
 * de comandes pendents i permet tancar la sessió de l usuari.
 */
class PantallaCuina : AppCompatActivity() {

    private lateinit var rvComandes: RecyclerView
    private lateinit var btnCerrarSesion: TextView
    private lateinit var cuinaRepository: TaulaRepository

    // Handler i Runnable per gestionar el refresc automàtic de comandes
    private val handler = Handler(Looper.getMainLooper())
    private val intervalRefresc = 10000L // 10 segons

    private val runnableRefresc = object : Runnable {
        override fun run() {
            carregarComandesCuina()
            handler.postDelayed(this, intervalRefresc)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_cuina)

        // Inicialitzar elements de la interfície de usuari
        cuinaRepository = TaulaRepository(this)
        rvComandes = findViewById(R.id.rvComandesCuina)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        // Configurar el RecyclerView en graella de 4 columnes per veure les comandes com a tiquets
        rvComandes.layoutManager = GridLayoutManager(this, 4)

        // Configurar el botó de tancar sessió de la barra lateral
        btnCerrarSesion.setOnClickListener {
            tancarSessio()
        }
    }

    override fun onResume() {
        super.onResume()
        // Iniciar el bucle de refresc automàtic en tornar a la pantalla
        handler.post(runnableRefresc)
    }

    override fun onPause() {
        super.onPause()
        // Aturar el bucle quan la pantalla no estigui activa per estalviar bateria i memòria
        handler.removeCallbacks(runnableRefresc)
    }

    /**
     * Mètode encarregat de cridar la API REST a través del repositori i actualitzar el RecyclerView.
     */
    private fun carregarComandesCuina() {
        lifecycleScope.launch {
            try {
                val llistaComandes = cuinaRepository.getComandesCuina()

                if (llistaComandes != null) {
                    val adapter = rvComandes.adapter as? CuinaAdapter
                    if (adapter == null) {
                        rvComandes.adapter = CuinaAdapter(llistaComandes)
                    } else {
                        // Si l adaptador ja existeix, es pot afegir un mètode d actualització per evitar parpelleigs
                        rvComandes.adapter = CuinaAdapter(llistaComandes)
                    }
                } else {
                    Toast.makeText(
                        this@PantallaCuina,
                        "Error en carregar les comandes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@PantallaCuina,
                    "Error de connexió: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Neteja la sessió de l usuari i el retorna a la pantalla de login.
     */
    private fun tancarSessio() {
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, PantallaLogin::class.java)
        // Netejar l historial de pantalles per evitar que l usuari pugui tornar enrere
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}