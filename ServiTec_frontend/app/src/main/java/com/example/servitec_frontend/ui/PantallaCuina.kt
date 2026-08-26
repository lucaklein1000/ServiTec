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
import com.example.servitec_frontend.ui.adapter.CuinaAdapter
import kotlinx.coroutines.launch

/**
 * Activity que gestiona la vista de comandes per al personal de cuina.
 * Realitza peticions periòdiques al backend per mantenir actualitzada la llista
 * de comandes pendents i permet tancar la sessió de l'usuari.
 */
class PantallaCuina : AppCompatActivity() {

    // Components visuals de la interfície d'usuari i repositori
    private lateinit var rvComandes: RecyclerView
    private lateinit var btnCerrarSesion: TextView
    private lateinit var cuinaRepository: TaulaRepository

    // Handler i Runnable per gestionar el refresc automàtic de comandes
    private val handler = Handler(Looper.getMainLooper())
    private val intervalRefresc = 10000L // Interval definit en 10 segons

    private val runnableRefresc = object : Runnable {
        override fun run() {
            carregarComandesCuina()
            handler.postDelayed(this, intervalRefresc)
        }
    }

    /**
     * Inicialitza la pantalla de cuina, enllaça els components visuals i configura el layout de la graella.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_cuina)

        // Inicialització dels repositoris i elements visuals de la interfície
        cuinaRepository = TaulaRepository(this)
        rvComandes = findViewById(R.id.rvComandesCuina)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        // Configuració del RecyclerView en una graella de 4 columnes per a la visualització de tiquets
        rvComandes.layoutManager = GridLayoutManager(this, 4)

        // Configuració de l'escoltador per al tancament de sessió
        btnCerrarSesion.setOnClickListener {
            tancarSessio()
        }
    }

    /**
     * S'executa quan l'activitat passa a primer pla, iniciant el bucle de refresc periòdic.
     */
    override fun onResume() {
        super.onResume()
        // Inici del temporitzador de refresc automàtic
        handler.post(runnableRefresc)
    }

    /**
     * S'executa quan l'activitat entra en pausa, aturant el refresc per optimitzar els recursos del dispositiu.
     */
    override fun onPause() {
        super.onPause()
        // Aturda del temporitzador per estalviar bateria i memòria
        handler.removeCallbacks(runnableRefresc)
    }

    /**
     * Obté la llista actualitzada de comandes des de la base de dades a través de l'API REST i actualitza l'adaptador.
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
                        // Reassignació de l'adaptador amb les dades actualitzades del servidor
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
     * Esborra les dades de la sessió emmagatzemades i redirigeix l'usuari a la pantalla d'inici de sessió.
     */
    private fun tancarSessio() {
        // Neteja de les preferències compartides de l'aplicació
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Redirecció a la pantalla de login eliminant la pila d'activitats anteriors
        val intent = Intent(this, PantallaLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}