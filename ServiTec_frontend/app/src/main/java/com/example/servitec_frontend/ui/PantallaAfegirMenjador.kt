// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaAfegirMenjador.kt
// Descripció:    Activity per a la creació de nous menjadors i disseny del seu
//                plànol inicial. Permet afegir taules mitjançant gestos de 
//                drag-and-drop des de la paleta lateral i guardar la nova zona.
// ============================================================================

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
import com.example.servitec_frontend.data.model.CreateMenjadorDTO
import com.example.servitec_frontend.data.model.CreateTaulaDTO
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Activity encarregada del formulari de creació de nous menjadors i la disposició del seu plànol inicial.
 * Implementa esdeveniments de drag-and-drop per posicionar taules en el canvas i guardar l'estructura a la BDD.
 */
class PantallaAfegirMenjador : AppCompatActivity() {

    // Components visuals de la interfície d'usuari
    private lateinit var btnTornar: MaterialButton
    private lateinit var etNomMenjador: TextInputEditText
    private lateinit var btnGuardarMenjador: MaterialButton
    private lateinit var canvasMenjador: RelativeLayout
    private lateinit var tvHintCanvas: TextView

    // Elements de la paleta lateral de taules
    private lateinit var dragTaula2Pax: LinearLayout
    private lateinit var dragTaula4Pax: LinearLayout
    private lateinit var dragTaula6Pax: LinearLayout
    private lateinit var dragTaula8Pax: LinearLayout

    // Estat local de les taules afegides al canvas i repositori
    private val llistaTaulesCollocades = mutableListOf<CreateTaulaDTO>()
    private lateinit var taulaRepository: TaulaRepository

    /**
     * Inicialitza la pantalla de creació de menjador, vincula els components i configura els escoltadors d'arrossegament.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_afegir_menjador)

        // Enllaç dels elements visuals de la interfície
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

        // Configuració dels escoltadors d'arrossegament a la paleta lateral
        configurarLongClick(dragTaula2Pax, 2)
        configurarLongClick(dragTaula4Pax, 4)
        configurarLongClick(dragTaula6Pax, 6)
        configurarLongClick(dragTaula8Pax, 8)

        // Configuració del canvas per a la recepció d'elements arrossegats
        configurarCanvasDragListener()

        // Assignació dels esdeveniments per als botons d'acció
        btnGuardarMenjador.setOnClickListener {
            guardarMenjador()
        }

        btnTornar.setOnClickListener {
            finish()
        }
    }

    /**
     * Configura el gest de clic llarg en els elements de la paleta per iniciar l'operació d'arrossegament.
     */
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

    /**
     * Configura l'escoltador d'esdeveniments de drop al canvas per detectar quan es deixa anar una nova taula.
     */
    private fun configurarCanvasDragListener() {
        canvasMenjador.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true

                DragEvent.ACTION_DROP -> {
                    // Obtenció de la capacitat enviada des de la paleta
                    val item = event.clipData.getItemAt(0)
                    val capacitat = item.text.toString().toIntOrNull() ?: 2

                    // Càlcul de la posició percentual (0 a 100%) en el canvas
                    val canvasWidth = canvasMenjador.width.toFloat()
                    val canvasHeight = canvasMenjador.height.toFloat()

                    val touchX = event.x
                    val touchY = event.y

                    val posXPercent = (touchX / canvasWidth) * 100f
                    val posYPercent = (touchY / canvasHeight) * 100f

                    // Ocultació del text d'ajuda visual en afegir la primera taula
                    tvHintCanvas.visibility = View.GONE

                    // Renderitzat de la taula al canvas
                    afegirTaulaAlCanvas(capacitat, touchX, touchY, posXPercent, posYPercent)
                    true
                }

                else -> true
            }
        }
    }

    /**
     * Dibuixa el component visual de la taula al canvas i afegeix la seva informació al DTO de creació.
     */
    private fun afegirTaulaAlCanvas(
        capacitat: Int,
        pixelX: Float,
        pixelY: Float,
        posXPercent: Float,
        posYPercent: Float
    ) {
        val numeroTaula = llistaTaulesCollocades.size + 1

        // Creació de la vista dinàmica per representar la taula
        val taulaView = TextView(this).apply {
            text = "T$numeroTaula\n($capacitat PAX)"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1B3358"))
            gravity = android.view.Gravity.CENTER
            textSize = 11f

            // Ajust de les dimensions segons la capacitat de comensals
            val widthPx = (45 + (capacitat * 8)).toPx()
            val heightPx = 50.toPx()

            layoutParams = RelativeLayout.LayoutParams(widthPx, heightPx).apply {
                leftMargin = (pixelX - (widthPx / 2)).toInt()
                topMargin = (pixelY - (heightPx / 2)).toInt()
            }
        }

        // Permet eliminar la taula fent clic directament sobre ella
        taulaView.setOnClickListener {
            canvasMenjador.removeView(taulaView)
            llistaTaulesCollocades.removeIf { it.numero == numeroTaula }
            if (llistaTaulesCollocades.isEmpty()) {
                tvHintCanvas.visibility = View.VISIBLE
            }
        }

        // Inserció del DTO corresponent a la llista pendent d'enviar al servidor
        llistaTaulesCollocades.add(
            CreateTaulaDTO(
                numero = numeroTaula,
                capacitat = capacitat,
                estat = false,
                idMenjador = 1,
                posX = posXPercent,
                posY = posYPercent
            )
        )

        canvasMenjador.addView(taulaView)
    }

    /**
     * Valida les dades del formulari i envia la informació del nou menjador i les seves taules al backend.
     */
    private fun guardarMenjador() {
        val nom = etNomMenjador.text.toString().trim()

        // Validacions de camps obligatoris
        if (nom.isEmpty()) {
            Toast.makeText(this, "Introdueix el nom del menjador", Toast.LENGTH_SHORT).show()
            return
        }

        if (llistaTaulesCollocades.isEmpty()) {
            Toast.makeText(this, "Afegeix almenys una taula al menjador", Toast.LENGTH_SHORT).show()
            return
        }

        // Desactivació del botó de desament per evitar peticions múltiples
        btnGuardarMenjador.isEnabled = false

        val dto = CreateMenjadorDTO(
            nomMenjador = nom,
            actiu = true,
            taules = llistaTaulesCollocades
        )

        // Petició asíncrona al repositori per a la creació del menjador
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

    /**
     * Funció d'extensió per convertir valors DP a Píxels segons la densitat de la pantalla actual.
     */
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()
}