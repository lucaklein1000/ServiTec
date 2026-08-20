package com.example.servitec_frontend.ui

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.PostMenjadorDTO
import com.example.servitec_frontend.data.model.PostTaulaDTO
import com.example.servitec_frontend.data.model.Taula
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class PantallaAfegirMenjador : AppCompatActivity() {
    private lateinit var btnTornar: MaterialButton
    private lateinit var etNomMenjador: TextInputEditText
    private lateinit var btnGuardarMenjador: MaterialButton
    private lateinit var canvasMenjador: RelativeLayout
    private lateinit var tvHintCanvas: TextView

    // Paleta de mesas
    private lateinit var dragTaula2Pax: LinearLayout
    private lateinit var dragTaula4Pax: LinearLayout
    private lateinit var dragTaula6Pax: LinearLayout
    private lateinit var dragTaula8Pax: LinearLayout

    private val llistaTaulesCollocades = mutableListOf<PostTaulaDTO>()
    private lateinit var taulaRepository : TaulaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_afegir_menjador)

        etNomMenjador = findViewById(R.id.etNomMenjador)
        btnGuardarMenjador = findViewById(R.id.btnGuardarMenjador)
        canvasMenjador = findViewById(R.id.canvasMenjador)
        tvHintCanvas = findViewById(R.id.tvHintCanvas)

        dragTaula2Pax = findViewById(R.id.dragTaula2Pax)
        dragTaula4Pax = findViewById(R.id.dragTaula4Pax)
        dragTaula6Pax = findViewById(R.id.dragTaula6Pax)
        dragTaula8Pax = findViewById(R.id.dragTaula8Pax)

        btnTornar = findViewById(R.id.btnTornar)

        taulaRepository = TaulaRepository(this)


        // 1. Configurar listeners de arrastre en la paleta lateral
        configurarLongClick(dragTaula2Pax, 2)
        configurarLongClick(dragTaula4Pax, 4)
        configurarLongClick(dragTaula6Pax, 6)
        configurarLongClick(dragTaula8Pax, 8)
        // 2. Configurar el listener en el lienzo para recibir las mesas
        configurarCanvasDragListener()

        // 3. Guardar el comedor completo en la BD
        btnGuardarMenjador.setOnClickListener {
            guardarMenjador()
        }

        btnTornar.setOnClickListener {
            finish()
        }
    }

    private fun configurarLongClick(view: View, capacitat: Int) {
        view.setOnLongClickListener { v ->
            val item = ClipData.Item(capacitat.toString())
            val dragData = ClipData(
                "CAPACITAT_TAULA",
                arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                item
            )
            val shadow = View.DragShadowBuilder(v)
            v.startDragAndDrop(dragData, shadow, null, 0)
            true
        }
    }

    private fun configurarCanvasDragListener() {
        canvasMenjador.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true

                DragEvent.ACTION_DROP -> {
                    // Recuperar la capacidad enviada desde la paleta
                    val item = event.clipData.getItemAt(0)
                    val capacitat = item.text.toString().toIntOrNull() ?: 2

                    // Calcular la posición relativa en porcentaje (0 a 100%)
                    val canvasWidth = canvasMenjador.width.toFloat()
                    val canvasHeight = canvasMenjador.height.toFloat()

                    val touchX = event.x
                    val touchY = event.y

                    val posXPercent = (touchX / canvasWidth) * 100f
                    val posYPercent = (touchY / canvasHeight) * 100f

                    // Ocultar texto de ayuda si es la primera mesa
                    tvHintCanvas.visibility = View.GONE

                    // Dibujar la mesa en el lienzo
                    afegirTaulaAlCanvas(capacitat, touchX, touchY, posXPercent, posYPercent)
                    true
                }

                else -> true
            }
        }

    }

    private fun afegirTaulaAlCanvas(
        capacitat: Int,
        pixelX: Float,
        pixelY: Float,
        posXPercent: Float,
        posYPercent: Float
    ) {
        val numeroTaula = llistaTaulesCollocades.size + 1

        // Crear la representación visual dinámica de la mesa
        val taulaView = TextView(this).apply {
            text = "T$numeroTaula\n($capacitat PAX)"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1B3358"))
            gravity = android.view.Gravity.CENTER
            textSize = 11f

            // Ancho según capacidad
            val widthPx = (45 + (capacitat * 8)).toPx()
            val heightPx = 50.toPx()

            layoutParams = RelativeLayout.LayoutParams(widthPx, heightPx).apply {
                leftMargin = (pixelX - (widthPx / 2)).toInt()
                topMargin = (pixelY - (heightPx / 2)).toInt()
            }
        }

        // Permitir eliminar la mesa haciendo clic sobre ella en el lienzo
        taulaView.setOnClickListener {
            canvasMenjador.removeView(taulaView)
            llistaTaulesCollocades.removeIf { it.postNumero == numeroTaula }
            if (llistaTaulesCollocades.isEmpty()) {
                tvHintCanvas.visibility = View.VISIBLE
            }
        }

        // Añadir a la lista DTO para enviar al backend
        llistaTaulesCollocades.add(
            PostTaulaDTO(
                postNumero = numeroTaula,
                postCapacitat = capacitat,
                postEstat = false,
                postIdMenjador = 1,
                postPosX = posXPercent,
                postPosY = posYPercent
            )
        )

        canvasMenjador.addView(taulaView)
    }

    private fun guardarMenjador() {
        val nom = etNomMenjador.text.toString().trim()

        if (nom.isEmpty()) {
            Toast.makeText(this, "Introdueix el nom del menjador", Toast.LENGTH_SHORT).show()
            return
        }

        if (llistaTaulesCollocades.isEmpty()) {
            Toast.makeText(this, "Afegeix almenys una taula al menjador", Toast.LENGTH_SHORT).show()
            return
        }

        btnGuardarMenjador.isEnabled = false

        val dto = PostMenjadorDTO(
            postNomMenjador = nom,
            postActiu = true,
            postTaules = llistaTaulesCollocades
        )

        lifecycleScope.launch {
            val resultat = taulaRepository.crearMenjador(dto)
            if (resultat != null) {
                Toast.makeText(
                    this@PantallaAfegirMenjador,
                    "Menjador '$nom' creat correctament!",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    this@PantallaAfegirMenjador,
                    "Error en guardar el menjador",
                    Toast.LENGTH_SHORT
                ).show()
            }
            btnGuardarMenjador.isEnabled = true
        }
    }

    // Extensión para convertir DPs a Pixeles
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()
}

