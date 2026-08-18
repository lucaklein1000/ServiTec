package com.example.servitec_frontend.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Taula
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class PantallaPanell : AppCompatActivity() {

    private lateinit var btnDireccio: MaterialButton
    private lateinit var containerMenjadors: LinearLayout
    private lateinit var canvasPanell: RelativeLayout
    private lateinit var btnCerrarSesion: MaterialButton

    private val menjadorRepository = TaulaRepository()
    private var menjadorSeleccionatId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_panell)

        // 1. INICIALIZAR TODAS LAS VISTAS
        containerMenjadors = findViewById(R.id.containerMenjadors)
        canvasPanell = findViewById(R.id.layoutSalon)
        btnDireccio = findViewById(R.id.btnDireccio)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        val prefs = getSharedPreferences("ServiTecPrefs", Context.MODE_PRIVATE)
        val userRol = prefs.getString("rolUsuari", "") ?: ""
        val esAdmin = prefs.getBoolean("esAdmin", false)

        val esGerentOAdmin = userRol.equals("Gerent", ignoreCase = true) || esAdmin

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
        carregarMenjadors()
    }

    private fun carregarMenjadors() {
        lifecycleScope.launch {
            val llistaMenjadors = menjadorRepository.llistarMenjador() ?: emptyList()

            containerMenjadors.removeAllViews()

            if (llistaMenjadors.isEmpty()) {
                canvasPanell.removeAllViews()
                return@launch
            }

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

                    // Selecciona el primero por defecto o el activo
                    if ((menjadorSeleccionatId == null && index == 0) || menjadorSeleccionatId == menjador.idMenjador) {
                        marcarBotoActiu(this)
                        menjadorSeleccionatId = menjador.idMenjador
                        pintarTaulesAlCanvas(menjador.taules)
                    }

                    setOnClickListener {
                        desmarcarTotsElsBotons()
                        marcarBotoActiu(this)
                        menjadorSeleccionatId = menjador.idMenjador
                        pintarTaulesAlCanvas(menjador.taules)
                    }
                }

                containerMenjadors.addView(btnMenjador)
            }
        }
    }

    private fun pintarTaulesAlCanvas(taules: List<Taula>?) {
        canvasPanell.removeAllViews()

        if (taules.isNullOrEmpty()) return

        canvasPanell.post {
            val canvasWidth = canvasPanell.width.toFloat()
            val canvasHeight = canvasPanell.height.toFloat()

            if (canvasWidth == 0f || canvasHeight == 0f) return@post

            for (taula in taules) {
                val pixelX = (taula.posX / 100f) * canvasWidth
                val pixelY = (taula.posY / 100f) * canvasHeight

                // Tamaño estándar según el diseño de la maqueta
                val sizePx = 160.toPx()

                // Instanciamos un AppCompatButton dinámico para mantener la interactividad
                val taulaView = androidx.appcompat.widget.AppCompatButton(this).apply {
                    text = "Taula ${taula.numero}\n(${taula.capacitat} Pax)"
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    textSize = 12f
                    isAllCaps = false

                    // 1. ASIGNAR EL DRAWABLE SEGÚN CAPACIDAD (PAX)
                    val bgDrawableRes = when (taula.capacitat) {
                        1, 2 -> R.drawable.bg_taula_2pax
                        3, 4 -> R.drawable.bg_taula_4pax
                        5, 6 -> R.drawable.bg_taula_6pax
                        else -> R.drawable.bg_taula_8pax
                    }
                    setBackgroundResource(bgDrawableRes)

                    // 2. TINTAR O APLICAR ESTADO (Opcional si usas tint según estado)
                    if (taula.estat) {
                        // Si está ocupada puedes aplicarle un tinte sutil o alfa si tus drawables no lo incluyen
                        backgroundTintList = ContextCompat.getColorStateList(this@PantallaPanell, R.color.taula_ocupada2)
                    } else {
                        backgroundTintList = null
                    }

                    when (taula.estatComanda) {
                        "oberta" -> {
                            backgroundTintList = ContextCompat.getColorStateList(this@PantallaPanell, R.color.taula_ocupada2)
                        }
                        "pendent" -> {
                            backgroundTintList = ContextCompat.getColorStateList(this@PantallaPanell, R.color.taula_pendent)
                        }
                        else -> {
                            backgroundTintList = null
                        }
                    }

                    // Posicionamiento exacto centrado en las coordenadas (X, Y)
                    layoutParams = RelativeLayout.LayoutParams(sizePx, sizePx).apply {
                        leftMargin = (pixelX - (sizePx / 2)).toInt()
                        topMargin = (pixelY - (sizePx / 2)).toInt()
                    }

                    // Interacción asegurada
                    isClickable = true
                    isFocusable = true

                    setOnClickListener {
                        // 1. Calculamos si la mesa tiene comanda activa igual que se hacía antes
                        val estatComanda = taula.estatComanda ?: "lliure"
                        val esOcupada = estatComanda.equals("oberta", ignoreCase = true) ||
                                estatComanda.equals("pendent", ignoreCase = true) ||
                                taula.estat // Mantiene compatibilidad si taula.estat indica ocupación

                        val intent = Intent(this@PantallaPanell, PantallaTaula::class.java).apply {
                            putExtra("idTaula", taula.idTaula)
                            // 2. Convertimos el número a String ("Taula X") para que intent.getStringExtra("nTaula") no devuelva null
                            putExtra("nTaula", "Taula ${taula.numero}")
                            putExtra("taulaOcupada", esOcupada)
                            putExtra("estatComanda", estatComanda)
                        }
                        startActivity(intent)
                    }
                }

                canvasPanell.addView(taulaView)
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
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, PantallaLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}