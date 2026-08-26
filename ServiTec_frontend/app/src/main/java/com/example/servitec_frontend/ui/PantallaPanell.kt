// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaPanell.kt
// Descripció:    Vista interactiva del pla de menjadors (TPV). Sincronitza en
//                temps real l'estat de les taules, posicions, bloquejos d'usuaris
//                i permet la navegació cap a la gestió de comandes.
// ============================================================================

package com.example.servitec_frontend.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.TaulaDTO
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

/**
 * Activitat encarregada de la visualització gràfica del pla de menjadors del restaurant.
 * Gestiona el refresc periòdic (polling), el dibuixat de taules en posició percentual
 * respecte al canvas i el control de concurrència/bloquejos entre cambrers.
 */
class PantallaPanell : AppCompatActivity() {

    // Components principals de la interfície d'usuari
    private lateinit var btnDireccio: MaterialButton
    private lateinit var containerMenjadors: LinearLayout
    private lateinit var canvasPanell: RelativeLayout
    private lateinit var btnCerrarSesion: MaterialButton

    // Repositori de dades per a les operacions d'API REST
    private lateinit var menjadorRepository: TaulaRepository
    private var menjadorSeleccionatId: Int? = null

    // Dades de la sessió de l'usuari actiu
    private var nomCambrerActual: String = ""

    // Cache local de les vistes de les taules dibuixades (Evita parpelleig i recreacions innecessàries)
    private val taulesViewsMap = mutableMapOf<Int, androidx.appcompat.widget.AppCompatButton>()

    // Bucle de sincronització automàtica en temps real (Polling cada 1 segon)
    private val handler = Handler(Looper.getMainLooper())
    private val intervalRefresc = 1000L

    private val runnableRefresc = object : Runnable {
        override fun run() {
            carregarMenjadors()
            handler.postDelayed(this, intervalRefresc)
        }
    }

    /**
     * Inicialitza la pantalla panell, els repositoris, les vistes i comprova els permisos de l'usuari.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_panell)

        // Inicialització de repositoris i referències visuals
        menjadorRepository = TaulaRepository(this)
        containerMenjadors = findViewById(R.id.containerMenjadors)
        canvasPanell = findViewById(R.id.layoutSalon)
        btnDireccio = findViewById(R.id.btnDireccio)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        // Recuperació del perfil d'usuari emmagatzemat localment
        val prefs = getSharedPreferences("ServiTecPrefs", Context.MODE_PRIVATE)
        val userRol = prefs.getString("rolUsuari", "") ?: ""
        val esAdmin = prefs.getBoolean("esAdmin", false)
        nomCambrerActual = prefs.getString("nomUsuari", "") ?: prefs.getString("username", "Cambrer") ?: "Cambrer"

        // Restricció d'accés al botó de direcció segons el rol de l'usuari
        val esGerentOAdmin = userRol.equals("Admin", ignoreCase = true) || esAdmin
        btnDireccio.visibility = if (esGerentOAdmin) View.VISIBLE else View.GONE

        btnDireccio.setOnClickListener {
            finish()
        }

        btnCerrarSesion.setOnClickListener {
            tancarSessio()
        }
    }

    /**
     * Activa el bucle de sincronització periòdica quan la pantalla passa a primer pla.
     */
    override fun onResume() {
        super.onResume()
        // Execució del runnable per iniciar la sincronització en temps real
        handler.post(runnableRefresc)
    }

    /**
     * Atura el bucle de sincronització en pauses per estalviar recursos i bateria.
     */
    override fun onPause() {
        super.onPause()
        // Cancel·lació del refresc periòdic
        handler.removeCallbacks(runnableRefresc)
    }

    /**
     * Consulta l'API REST per obtenir la llista actualitzada de menjadors i taules.
     */
    private fun carregarMenjadors() {
        lifecycleScope.launch {
            val llistaMenjadors = menjadorRepository.llistarMenjador() ?: emptyList()

            // Si no hi ha menjadors disponibles, netegem tot el canvas
            if (llistaMenjadors.isEmpty()) {
                canvasPanell.removeAllViews()
                taulesViewsMap.clear()
                containerMenjadors.removeAllViews()
                return@launch
            }

            // Dibuixat dinàmic dels botons dels menjadors (només es recrea si varia el nombre)
            if (containerMenjadors.childCount != llistaMenjadors.size) {
                containerMenjadors.removeAllViews()
                llistaMenjadors.forEachIndexed { index, menjador ->
                    val btnMenjador = MaterialButton(
                        this@PantallaPanell,
                        null,
                        com.google.android.material.R.attr.borderlessButtonStyle
                    ).apply {
                        text = (menjador.nomMenjador ?: "MENJADOR ${menjador.idMenjador}").uppercase()
                        textSize = 13f
                        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                        setTextColor(Color.WHITE)

                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            48.toPx()
                        )
                        params.setMargins(0, 0, 0, 4.toPx())
                        layoutParams = params

                        setOnClickListener {
                            desmarcarTotsElsBotons()
                            marcarBotoActiu(this)
                            if (menjadorSeleccionatId != menjador.idMenjador) {
                                menjadorSeleccionatId = menjador.idMenjador
                                // Netegem les taules de l'anterior menjador abans de dibuixar el nou
                                canvasPanell.removeAllViews()
                                taulesViewsMap.clear()
                                pintarTaulesAlCanvas(menjador.taules)
                            }
                        }
                    }

                    // Marcar per defecte el primer menjador o el seleccionat actualment
                    if ((menjadorSeleccionatId == null && index == 0) || menjadorSeleccionatId == menjador.idMenjador) {
                        marcarBotoActiu(btnMenjador)
                        menjadorSeleccionatId = menjador.idMenjador
                    }

                    containerMenjadors.addView(btnMenjador)
                }
            }

            // Actualitzar el canvas del menjador visible actualment
            val menjadorActual = llistaMenjadors.find { it.idMenjador == menjadorSeleccionatId }
                ?: llistaMenjadors.firstOrNull()

            if (menjadorActual != null) {
                menjadorSeleccionatId = menjadorActual.idMenjador
                pintarTaulesAlCanvas(menjadorActual.taules)
            }
        }
    }

    /**
     * Realitza el rendiment visual de les taules al canvas utilitzant un algorisme de delta-update
     * per actualitzar només aquelles vistes que hagin canviat d'estat o posició.
     */
    private fun pintarTaulesAlCanvas(taules: List<TaulaDTO>?) {
        if (taules.isNullOrEmpty()) {
            canvasPanell.removeAllViews()
            taulesViewsMap.clear()
            return
        }

        canvasPanell.post {
            val canvasWidth = canvasPanell.width.toFloat()
            val canvasHeight = canvasPanell.height.toFloat()

            if (canvasWidth == 0f || canvasHeight == 0f) return@post

            val idsNoves = taules.map { it.idTaula }.toSet()

            // Neteja de taules que han estat eliminades al servidor des de la darrera sincronització
            val iter = taulesViewsMap.iterator()
            while (iter.hasNext()) {
                val entry = iter.next()
                if (entry.key !in idsNoves) {
                    canvasPanell.removeView(entry.value)
                    iter.remove()
                }
            }

            for (taula in taules) {
                val estatComanda = taula.estatComanda ?: "lliure"
                val esOcupada = estatComanda.equals("oberta", ignoreCase = true) ||
                        estatComanda.equals("segons", ignoreCase = true) ||
                        estatComanda.equals("pendent", ignoreCase = true) ||
                        taula.estat

                val esSegons = estatComanda.equals("segons", ignoreCase = true)

                // Verificació de bloqueig per concurrència
                val esElMeuBloqueig = taula.usuariBloqueig?.trim().equals(nomCambrerActual.trim(), ignoreCase = true)
                val estaBloquejadaPerAltre = taula.bloquejada && !esElMeuBloqueig

                // Càlcul del color del boto segons l'estat operatiu i de bloqueig
                val nouTint = when {
                    estaBloquejadaPerAltre -> ContextCompat.getColorStateList(this@PantallaPanell, R.color.negre)
                    esSegons -> ContextCompat.getColorStateList(this@PantallaPanell, R.color.taula_ocupada2)
                    estatComanda.equals("oberta", ignoreCase = true) ->
                        ContextCompat.getColorStateList(this@PantallaPanell, R.color.taula_ocupada2)
                    estatComanda.equals("pendent", ignoreCase = true) ->
                        ContextCompat.getColorStateList(this@PantallaPanell, R.color.taula_pendent)
                    else -> null
                }

                // Format del text informatiu de la taula
                val textTaula = when {
                    estaBloquejadaPerAltre -> "Taula ${taula.numero}\n🔒 ${taula.usuariBloqueig ?: "Bloquejada"}"
                    esSegons -> "Taula ${taula.numero}\n⏸️ SEGONS"
                    else -> "Taula ${taula.numero}\n(${taula.capacitat} Pax)"
                }

                // Reutilització de vistes: Si la taula ja existeix, s'actualitzen les propietats
                val vistaExistent = taulesViewsMap[taula.idTaula]
                if (vistaExistent != null) {
                    vistaExistent.text = textTaula
                    vistaExistent.backgroundTintList = nouTint
                    vistaExistent.setOnClickListener {
                        obrirPantallaTaula(taula, esOcupada, estatComanda)
                    }
                    continue
                }

                // Creació de nova vista: Conversió de coordenades percentuals a píxels reals
                val pixelX = (taula.posX / 100f) * canvasWidth
                val pixelY = (taula.posY / 100f) * canvasHeight
                val sizePx = 160.toPx()

                val taulaView = androidx.appcompat.widget.AppCompatButton(this).apply {
                    text = textTaula
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    textSize = 12f
                    isAllCaps = false

                    // Fons adaptable segons la capacitat de comensals
                    val bgDrawableRes = when (taula.capacitat) {
                        1, 2 -> R.drawable.bg_taula_2pax
                        3, 4 -> R.drawable.bg_taula_4pax
                        5, 6 -> R.drawable.bg_taula_6pax
                        else -> R.drawable.bg_taula_8pax
                    }
                    setBackgroundResource(bgDrawableRes)
                    backgroundTintList = nouTint

                    layoutParams = RelativeLayout.LayoutParams(sizePx, sizePx).apply {
                        leftMargin = (pixelX - (sizePx / 2)).toInt()
                        topMargin = (pixelY - (sizePx / 2)).toInt()
                    }

                    isClickable = true
                    isFocusable = true

                    setOnClickListener {
                        obrirPantallaTaula(taula, esOcupada, estatComanda)
                    }
                }

                taulesViewsMap[taula.idTaula] = taulaView
                canvasPanell.addView(taulaView)
            }
        }
    }

    /**
     * Intent de bloqueig al backend i navegació cap a la pantalla TPV de la taula.
     */
    private fun obrirPantallaTaula(taula: TaulaDTO, esOcupada: Boolean, estatComanda: String) {
        val esElMeuBloqueig = taula.usuariBloqueig?.trim().equals(nomCambrerActual.trim(), ignoreCase = true)

        // Denega l'accés si la taula la té oberta un altre cambrer
        if (taula.bloquejada && !esElMeuBloqueig) {
            Toast.makeText(
                this,
                "La taula està sent utilitzada per ${taula.usuariBloqueig}",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Bloqueig de la taula al backend abans d'obrir la comanda
        lifecycleScope.launch {
            val result = menjadorRepository.bloquejarTaula(taula.idTaula, nomCambrerActual)

            result.onSuccess {
                val intent = Intent(this@PantallaPanell, PantallaTaula::class.java).apply {
                    putExtra("idTaula", taula.idTaula)
                    putExtra("nTaula", "Taula ${taula.numero}")
                    putExtra("taulaOcupada", esOcupada)
                    putExtra("estatComanda", estatComanda)
                }
                startActivity(intent)
            }.onFailure { ex ->
                Toast.makeText(
                    this@PantallaPanell,
                    ex.message ?: "La taula ha estat bloquejada per un altre usuari",
                    Toast.LENGTH_SHORT
                ).show()
                carregarMenjadors() // Refrescar per mostrar la taula com a bloquejada immediatament
            }
        }
    }

    /**
     * Canvia l'estat visual del botó de menjador seleccionat.
     */
    private fun marcarBotoActiu(button: MaterialButton) {
        // Canvi de color de fons del botó seleccionat
        button.setBackgroundColor(Color.parseColor("#3B82F6"))
    }

    /**
     * Restableix el fons de tots els botons de menjadors a transparent.
     */
    private fun desmarcarTotsElsBotons() {
        // Iteració sobre la llista de botons per reiniciar-ne l'estat visual
        for (i in 0 until containerMenjadors.childCount) {
            val child = containerMenjadors.getChildAt(i)
            if (child is MaterialButton) {
                child.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    /**
     * Extensió per a la conversió ràpida de DP a Píxels segons la densitat de pantalla.
     */
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

    /**
     * Neteja la sessió local i redirecciona a la pantalla d'inici de sessió.
     */
    private fun tancarSessio() {
        // Aturar tasques en segon pla i netejar preferències
        handler.removeCallbacks(runnableRefresc)
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, PantallaLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}