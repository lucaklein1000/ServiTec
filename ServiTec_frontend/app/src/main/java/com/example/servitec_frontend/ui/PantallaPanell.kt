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
import kotlin.jvm.java

class PantallaPanell : AppCompatActivity() {

    private lateinit var btnDireccio: MaterialButton
    private lateinit var containerMenjadors: LinearLayout
    private lateinit var canvasPanell: RelativeLayout
    private lateinit var btnCerrarSesion: MaterialButton

    private lateinit var menjadorRepository: TaulaRepository
    private var menjadorSeleccionatId: Int? = null

    // Usuari actual obtingut de SharedPreferences
    private var nomCambrerActual: String = ""

    // Mapa per emmagatzemar i reutilitzar les vistes de les taules actualment dibuixades
    private val taulesViewsMap = mutableMapOf<Int, androidx.appcompat.widget.AppCompatButton>()

    // Handler i Runnable per gestionar el refresc automàtic en temps real
    private val handler = Handler(Looper.getMainLooper())
    private val intervalRefresc = 1000L // 1 segon

    private val runnableRefresc = object : Runnable {
        override fun run() {
            carregarMenjadors()
            handler.postDelayed(this, intervalRefresc)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_panell)

        // 1. INICIALITZAR VISTES I DADES D'USUARI
        menjadorRepository = TaulaRepository(this)
        containerMenjadors = findViewById(R.id.containerMenjadors)
        canvasPanell = findViewById(R.id.layoutSalon)
        btnDireccio = findViewById(R.id.btnDireccio)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        val prefs = getSharedPreferences("ServiTecPrefs", Context.MODE_PRIVATE)
        val userRol = prefs.getString("rolUsuari", "") ?: ""
        val esAdmin = prefs.getBoolean("esAdmin", false)
        nomCambrerActual = prefs.getString("nomUsuari", "") ?: prefs.getString("username", "Cambrer") ?: "Cambrer"

        val esGerentOAdmin = userRol.equals("Admin", ignoreCase = true) || esAdmin

        btnDireccio.visibility = if (esGerentOAdmin) View.VISIBLE else View.GONE

        btnDireccio.setOnClickListener {
            finish()
        }

        btnCerrarSesion.setOnClickListener {
            tancarSessio()
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(runnableRefresc)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnableRefresc)
    }

    private fun carregarMenjadors() {
        lifecycleScope.launch {
            val llistaMenjadors = menjadorRepository.llistarMenjador() ?: emptyList()

            if (llistaMenjadors.isEmpty()) {
                canvasPanell.removeAllViews()
                taulesViewsMap.clear()
                containerMenjadors.removeAllViews()
                return@launch
            }

            // Actualitzem els botons dels menjadors només si el nombre de botons difereix
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
                                // En canviar de menjador, netegem el canvas per dibuixar el nou salon
                                canvasPanell.removeAllViews()
                                taulesViewsMap.clear()
                                pintarTaulesAlCanvas(menjador.taules)
                            }
                        }
                    }

                    if ((menjadorSeleccionatId == null && index == 0) || menjadorSeleccionatId == menjador.idMenjador) {
                        marcarBotoActiu(btnMenjador)
                        menjadorSeleccionatId = menjador.idMenjador
                    }

                    containerMenjadors.addView(btnMenjador)
                }
            }

            // Actualitzar el canvas del menjador actualment seleccionat
            val menjadorActual = llistaMenjadors.find { it.idMenjador == menjadorSeleccionatId }
                ?: llistaMenjadors.firstOrNull()

            if (menjadorActual != null) {
                menjadorSeleccionatId = menjadorActual.idMenjador
                pintarTaulesAlCanvas(menjadorActual.taules)
            }
        }
    }

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

            // Eliminar vistes de taules que ja no existeixin en la resposta del servidor
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

                // Normalitzar la comparació de noms d'usuari per evitar fallos d'espais o majúscules
                val esElMeuBloqueig = taula.usuariBloqueig?.trim().equals(nomCambrerActual.trim(), ignoreCase = true)
                val estaBloquejadaPerAltre = taula.bloquejada && !esElMeuBloqueig

                // Determinar el tint de color segons l'estat
                val nouTint = when {
                    estaBloquejadaPerAltre -> ContextCompat.getColorStateList(this@PantallaPanell, R.color.negre)
                    esSegons -> ContextCompat.getColorStateList(this@PantallaPanell, R.color.taula_ocupada2)
                    estatComanda.equals("oberta", ignoreCase = true) ->
                        ContextCompat.getColorStateList(this@PantallaPanell, R.color.taula_ocupada2)
                    estatComanda.equals("pendent", ignoreCase = true) ->
                        ContextCompat.getColorStateList(this@PantallaPanell, R.color.taula_pendent)
                    else -> null
                }

                // Text segons l'estat visual
                val textTaula = when {
                    estaBloquejadaPerAltre -> "Taula ${taula.numero}\n🔒 ${taula.usuariBloqueig ?: "Bloquejada"}"
                    esSegons -> "Taula ${taula.numero}\n⏸️ SEGONS"
                    else -> "Taula ${taula.numero}\n(${taula.capacitat} Pax)"
                }

                // SI LA TAULA JA EXISTEIX AL CANVAS, NOMÉS N'ACTUALITZEM LES PROPIETATS
                val vistaExistent = taulesViewsMap[taula.idTaula]
                if (vistaExistent != null) {
                    vistaExistent.text = textTaula
                    vistaExistent.backgroundTintList = nouTint
                    vistaExistent.setOnClickListener {
                        obrirPantallaTaula(taula, esOcupada, estatComanda)
                    }
                    continue
                }

                // SI ÉS UNA TAULA NOVA, LA CREEM I AFEGIM AL MAPA
                val pixelX = (taula.posX / 100f) * canvasWidth
                val pixelY = (taula.posY / 100f) * canvasHeight
                val sizePx = 160.toPx()

                val taulaView = androidx.appcompat.widget.AppCompatButton(this).apply {
                    text = textTaula
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    textSize = 12f
                    isAllCaps = false

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

    private fun obrirPantallaTaula(taula: TaulaDTO, esOcupada: Boolean, estatComanda: String) {
        val esElMeuBloqueig = taula.usuariBloqueig?.trim().equals(nomCambrerActual.trim(), ignoreCase = true)

        // Denegar l'accés directament si la taula està bloquejada per un altre usuari
        if (taula.bloquejada && !esElMeuBloqueig) {
            Toast.makeText(
                this,
                "La taula està sent utilitzada per ${taula.usuariBloqueig}",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Executem el bloqueig al servidor abans de navegar
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
                carregarMenjadors() // Refrescar si hi ha hagut un conflicte 409
            }
        }
    }

    private fun marcarBotoActiu(button: MaterialButton) {
        button.setBackgroundColor(Color.parseColor("#3B82F6"))
    }

    private fun desmarcarTotsElsBotons() {
        for (i in 0 until containerMenjadors.childCount) {
            val child = containerMenjadors.getChildAt(i)
            if (child is MaterialButton) {
                child.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }

    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun tancarSessio() {
        handler.removeCallbacks(runnableRefresc)
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, PantallaLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}