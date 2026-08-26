// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaGestionarMenjadors.kt
// Descripció:    Activity per a la gestió del plànol de menjadors. Permet afegir,
//                moure mitjançant gestos de drag-and-drop, reanomenar, eliminar
//                taules i actualitzar les seves coordenades en el canvas.
// ============================================================================

package com.example.servitec_frontend.ui

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.CreateTaulaDTO
import com.example.servitec_frontend.data.model.Menjador
import com.example.servitec_frontend.data.model.TaulaDTO
import com.example.servitec_frontend.data.model.UpdateMenjadorDTO
import com.example.servitec_frontend.data.model.UpdateTaulaDTO
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Activity encarregada de la gestió i disseny interactiu dels plànols dels menjadors.
 * Suporta el posicionament dinàmic de taules, edició de capacitat i sincronització amb el backend.
 */
class PantallaGestionarMenjadors : AppCompatActivity() {

    // Components visuals de la interfície d'usuari
    private lateinit var btnTornar: MaterialButton
    private lateinit var btnGuardarPlano: MaterialButton
    private lateinit var actvMenjadors: AutoCompleteTextView
    private lateinit var canvasMenjador: RelativeLayout
    private lateinit var tvHintCanvas: TextView
    private var zonaEsborrar: View? = null

    // Repositori de dades per a les operacions d'API REST
    private lateinit var menjadorRepository: TaulaRepository

    // Estat local dels menjadors i selecció actual
    private var llistaMenjadors: List<Menjador> = emptyList()
    private var menjadorSeleccionat: Menjador? = null

    /**
     * Inicialitza la pantalla de gestió de menjadors, vincula les vistes i carrega la informació des de la base de dades.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_gestionar_menjadors)

        initViews()
        setupListeners()
        carregarMenjadorsDesDeBDD()
    }

    /**
     * Vincula les variables locals amb els elements corresponents del layout XML i inicialitza el repositori.
     */
    private fun initViews() {
        menjadorRepository = TaulaRepository(this)
        btnTornar = findViewById(R.id.btnTornar)
        btnGuardarPlano = findViewById(R.id.btnGuardarPlano)
        actvMenjadors = findViewById(R.id.actvMenjadors)
        canvasMenjador = findViewById(R.id.canvasMenjador)
        tvHintCanvas = findViewById(R.id.tvHintCanvas)

        // Definició de la zona d'eliminació per arrossegament (paperera)
        zonaEsborrar = findViewById(R.id.areaPapelera)
            ?: (findViewById<View>(R.id.dragTaula2Pax)?.parent as? View)
    }

    /**
     * Assigna els escoltadors d'esdeveniments als botons de navegació, desament i creació de taules per capacitat.
     */
    private fun setupListeners() {
        btnTornar.setOnClickListener {
            finish()
        }

        btnGuardarPlano.setOnClickListener {
            menjadorSeleccionat?.let { menjador ->
                guardarCanvisPlano(menjador)
            } ?: run {
                Toast.makeText(this, "Selecciona primer un menjador", Toast.LENGTH_SHORT).show()
            }
        }

        // Esdeveniments de creació de taules segons la capacitat de persones
        findViewById<View>(R.id.dragTaula2Pax)?.setOnClickListener { afegirNovaTaula(2) }
        findViewById<View>(R.id.dragTaula4Pax)?.setOnClickListener { afegirNovaTaula(4) }
        findViewById<View>(R.id.dragTaula6Pax)?.setOnClickListener { afegirNovaTaula(6) }
        findViewById<View>(R.id.dragTaula8Pax)?.setOnClickListener { afegirNovaTaula(8) }
    }

    /**
     * Consulta el repositori per carregar la llista de menjadors i les seves respectives taules des de la base de dades.
     */
    private fun carregarMenjadorsDesDeBDD() {
        lifecycleScope.launch {
            try {
                llistaMenjadors = menjadorRepository.llistarMenjador() ?: emptyList()

                if (llistaMenjadors.isNotEmpty()) {
                    setupDropdownMenjadors()
                } else {
                    Toast.makeText(this@PantallaGestionarMenjadors, "No s'han trobat menjadors", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@PantallaGestionarMenjadors, "Error en carregar els menjadors: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Configura l'adaptador i l'escoltador del selector desplegable de menjadors.
     */
    private fun setupDropdownMenjadors() {
        val nomMenjadors = llistaMenjadors.map { it.nomMenjador }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomMenjadors)
        actvMenjadors.setAdapter(adapter)

        // Carrega el plànol del menjador seleccionat
        actvMenjadors.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position) as String
            menjadorSeleccionat = llistaMenjadors.find { it.nomMenjador == nomSeleccionat }
            carregarPlanoMenjador(menjadorSeleccionat)
        }
    }

    /**
     * Prepara el canvas i dibuixa la disposició de taules del menjador seleccionat.
     */
    private fun carregarPlanoMenjador(menjador: Menjador?) {
        if (menjador == null) return
        tvHintCanvas.visibility = View.GONE
        dibuixarTaulesEnLlenç(menjador.taules)
    }

    /**
     * Afegeix una nova taula temporal al menjador seleccionat amb la capacitat especificada.
     */
    private fun afegirNovaTaula(capacitatPax: Int) {
        val menjador = menjadorSeleccionat
        if (menjador == null) {
            Toast.makeText(this, "Selecciona primer un menjador!", Toast.LENGTH_SHORT).show()
            return
        }

        val novesTaules = menjador.taules.toMutableList()
        val nouNumero = (novesTaules.maxOfOrNull { it.numero } ?: 0) + 1

        // Utilització de idTaula = 0 per indicar una taula pendent de crear a la BDD
        val novaTaula = TaulaDTO(
            idTaula = 0,
            numero = nouNumero,
            capacitat = capacitatPax,
            posX = 50f,
            posY = 50f,
            estat = true,
            estatComanda = "lliure",
            idMenjador = menjador.idMenjador
        )

        novesTaules.add(novaTaula)
        menjador.taules = novesTaules
        dibuixarTaulesEnLlenç(novesTaules)

        Toast.makeText(this, "Afegida Taula $nouNumero ($capacitatPax PAX)", Toast.LENGTH_SHORT).show()
    }

    /**
     * Dibuixa totes les taules d'un menjador al canvas convertint les coordenades percentuals a píxels reals.
     */
    private fun dibuixarTaulesEnLlenç(taules: List<TaulaDTO>) {
        canvasMenjador.removeAllViews()
        if (taules.isEmpty()) return

        canvasMenjador.post {
            val canvasWidth = canvasMenjador.width.toFloat()
            val canvasHeight = canvasMenjador.height.toFloat()

            if (canvasWidth == 0f || canvasHeight == 0f) return@post

            for (taula in taules) {
                // Conversió de percentatge a posició en píxels
                val pixelX = (taula.posX / 100f) * canvasWidth
                val pixelY = (taula.posY / 100f) * canvasHeight

                val sizePx = 160.toPx()

                val taulaView = androidx.appcompat.widget.AppCompatButton(this).apply {
                    text = "Taula ${taula.numero}\n(${taula.capacitat} Pax)"
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    textSize = 12f
                    isAllCaps = false

                    // Fons segons la capacitat de comensals
                    val bgDrawableRes = when (taula.capacitat) {
                        1, 2 -> R.drawable.bg_taula_2pax
                        3, 4 -> R.drawable.bg_taula_4pax
                        5, 6 -> R.drawable.bg_taula_6pax
                        else -> R.drawable.bg_taula_8pax
                    }
                    setBackgroundResource(bgDrawableRes)

                    layoutParams = RelativeLayout.LayoutParams(sizePx, sizePx)
                    x = pixelX - (sizePx / 2f)
                    y = pixelY - (sizePx / 2f)
                    tag = taula
                }

                hacerMesaInteractiva(taulaView, taula)
                canvasMenjador.addView(taulaView)
            }
        }
    }

    /**
     * Configura els gestos d'arrossegament (drag-and-drop) i clic sobre una taula del plànol.
     */
    private fun hacerMesaInteractiva(view: View, taulaDTO: TaulaDTO) {
        var dX = 0f
        var dY = 0f
        var startX = 0f
        var startY = 0f
        val clickThreshold = 10f

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Desa la posició inicial de l'arrossegament
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    startX = event.rawX
                    startY = event.rawY
                    v.bringToFront()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    // Actualització de la posició visual en temps real
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY

                    v.x = newX
                    v.y = newY

                    val parentWidth = canvasMenjador.width.toFloat()
                    val parentHeight = canvasMenjador.height.toFloat()

                    // Re-càlcul del percentatge relatiu al canvas
                    if (parentWidth > 0f && parentHeight > 0f) {
                        val centroX = newX + (v.width / 2f)
                        val centroY = newY + (v.height / 2f)
                        taulaDTO.posX = (centroX / parentWidth) * 100f
                        taulaDTO.posY = (centroY / parentHeight) * 100f
                    }

                    true
                }
                MotionEvent.ACTION_UP -> {
                    val diffX = abs(event.rawX - startX)
                    val diffY = abs(event.rawY - startY)

                    // Distinció entre clic simple o gest d'arrossegament
                    if (diffX < clickThreshold && diffY < clickThreshold) {
                        mostrarDialogoCambiarNumero(taulaDTO)
                    } else {
                        val esBorrado = esDropEnPaleta(v) || esDropFueraDelCanvas(v)

                        if (esBorrado) {
                            eliminarTaula(taulaDTO)
                        } else {
                            ajustarLimitesCanvas(v, taulaDTO)
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    /**
     * Comprova si una taula s'ha arrossegat a sobre de la zona d'eliminació (paperera).
     */
    private fun esDropEnPaleta(view: View): Boolean {
        val targetView = zonaEsborrar ?: return false

        val viewRect = Rect()
        view.getGlobalVisibleRect(viewRect)

        val targetRect = Rect()
        targetView.getGlobalVisibleRect(targetRect)

        // Intersecció de les àrees visuals
        return Rect.intersects(viewRect, targetRect)
    }

    /**
     * Comprova si una taula s'ha arrossegat fora dels límits del canvas.
     */
    private fun esDropFueraDelCanvas(view: View): Boolean {
        val viewRect = Rect()
        view.getGlobalVisibleRect(viewRect)

        val canvasRect = Rect()
        canvasMenjador.getGlobalVisibleRect(canvasRect)

        return !Rect.intersects(viewRect, canvasRect) || viewRect.centerX() < canvasRect.left
    }

    /**
     * Ajusta la posició d'una taula per evitar que quedi parcialment o totalment fora dels límits del canvas.
     */
    private fun ajustarLimitesCanvas(view: View, taulaDTO: TaulaDTO) {
        val parentWidth = canvasMenjador.width.toFloat()
        val parentHeight = canvasMenjador.height.toFloat()

        if (parentWidth == 0f || parentHeight == 0f) return

        val maxX = parentWidth - view.width
        val maxY = parentHeight - view.height

        val clampedX = view.x.coerceIn(0f, maxX.coerceAtLeast(0f))
        val clampedY = view.y.coerceIn(0f, maxY.coerceAtLeast(0f))

        view.x = clampedX
        view.y = clampedY

        // Recàlcul final de la posició percentual des d'un punt vàlid
        val centroX = clampedX + (view.width / 2f)
        val centroY = clampedY + (view.height / 2f)

        taulaDTO.posX = (centroX / parentWidth) * 100f
        taulaDTO.posY = (centroY / parentHeight) * 100f
    }

    /**
     * Mostra un diàleg modal per permetre modificar el número d'identificació visual d'una taula.
     */
    private fun mostrarDialogoCambiarNumero(taulaDTO: TaulaDTO) {
        // Validació: No es permet editar taules ocupades
        if (!taulaDTO.estat) {
            Toast.makeText(this, "No es pot canviar el número d'una taula ocupada", Toast.LENGTH_LONG).show()
            return
        }

        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(taulaDTO.numero.toString())
            setSelection(text.length)
        }

        // Diàleg per a la introducció del nou número
        AlertDialog.Builder(this)
            .setTitle("Canviar número de taula")
            .setMessage("Introdueix el nou número per a la taula:")
            .setView(input)
            .setPositiveButton("Guardar") { dialog, _ ->
                val nouNumero = input.text.toString().toIntOrNull()
                if (nouNumero != null && nouNumero > 0) {
                    taulaDTO.numero = nouNumero
                    menjadorSeleccionat?.let { dibuixarTaulesEnLlenç(it.taules) }

                    // Actualització immediata a la BDD si la taula ja existia al backend
                    if (taulaDTO.idTaula != 0) {
                        lifecycleScope.launch {
                            try {
                                val taulaModificada = UpdateTaulaDTO(
                                    numero = taulaDTO.numero,
                                    estat = taulaDTO.estat,
                                    capacitat = taulaDTO.capacitat,
                                    posX = taulaDTO.posX,
                                    posY = taulaDTO.posY
                                )
                                val exit = menjadorRepository.actualitzarTaula(taulaDTO.idTaula, taulaModificada)
                                if (exit) {
                                    Toast.makeText(this@PantallaGestionarMenjadors, "Número actualitzat a la BDD", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@PantallaGestionarMenjadors, "Error en actualitzar a la BDD", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@PantallaGestionarMenjadors, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Número no vàlid", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel·lar") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }

    /**
     * Elimina la taula especificada tant de l'estat local com de la base de dades backend si correspon.
     */
    private fun eliminarTaula(taulaDTO: TaulaDTO) {
        // Impedir l'esborrat de taules en ús
        if (!taulaDTO.estat) {
            Toast.makeText(this, "No es pot eliminar una taula ocupada o amb comandes obertes", Toast.LENGTH_LONG).show()
            menjadorSeleccionat?.let { dibuixarTaulesEnLlenç(it.taules) }
            return
        }

        val menjador = menjadorSeleccionat ?: return

        // Si la taula està desada al servidor, es demana l'esborrat via API
        if (taulaDTO.idTaula != 0) {
            lifecycleScope.launch {
                try {
                    val exit = menjadorRepository.eliminarTaula(taulaDTO.idTaula)
                    if (exit) {
                        val novesTaules = menjador.taules.toMutableList()
                        novesTaules.remove(taulaDTO)
                        menjador.taules = novesTaules

                        dibuixarTaulesEnLlenç(novesTaules)
                        Toast.makeText(this@PantallaGestionarMenjadors, "S'ha eliminat la Taula ${taulaDTO.numero} de la BDD", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@PantallaGestionarMenjadors, "Error en eliminar la taula a la BDD", Toast.LENGTH_SHORT).show()
                        dibuixarTaulesEnLlenç(menjador.taules)
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@PantallaGestionarMenjadors, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    dibuixarTaulesEnLlenç(menjador.taules)
                }
            }
        } else {
            // Esborrat local directe per a taules noves no sincronitzades
            val novesTaules = menjador.taules.toMutableList()
            novesTaules.remove(taulaDTO)
            menjador.taules = novesTaules

            dibuixarTaulesEnLlenç(novesTaules)
            Toast.makeText(this, "S'ha eliminat la Taula ${taulaDTO.numero}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Sincronitza tots els canvis realitzats al plànol (nom del menjador, noves taules i modificacions) amb el servidor.
     */
    private fun guardarCanvisPlano(menjador: Menjador) {
        lifecycleScope.launch {
            try {
                val nouNom = actvMenjadors.text.toString().trim()
                if (nouNom.isEmpty()) {
                    Toast.makeText(this@PantallaGestionarMenjadors, "El nom del menjador no pot estar buit", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                var errors = 0

                // 1. Sincronització del nom del menjador si ha canviat
                if (nouNom != menjador.nomMenjador) {
                    val updateMenjadorDto = UpdateMenjadorDTO(nomMenjador = nouNom)
                    val nomActualitzat = menjadorRepository.actualitzarMenjador(menjador.idMenjador, updateMenjadorDto)
                    if (!nomActualitzat) {
                        errors++
                    } else {
                        menjador.nomMenjador = nouNom
                    }
                }

                // 2. Creació o actualització en massa de les taules del plànol
                val taules = menjador.taules
                if (!taules.isNullOrEmpty()) {
                    val taulesActualitzades = taules.toMutableList()

                    for (i in taulesActualitzades.indices) {
                        val taula = taulesActualitzades[i]

                        if (taula.idTaula == 0) {
                            // Creació de taula nova al backend
                            val taulaNova = CreateTaulaDTO(
                                numero = taula.numero,
                                estat = taula.estat,
                                capacitat = taula.capacitat,
                                posX = taula.posX,
                                posY = taula.posY,
                                idMenjador = menjador.idMenjador
                            )
                            val creada = menjadorRepository.crearTaula(taulaNova)
                            if (creada != null) {
                                taulesActualitzades[i] = creada
                            } else {
                                errors++
                            }
                        } else {
                            // Actualització de posició/propietats de taula existent
                            val taulaModificada = UpdateTaulaDTO(
                                numero = taula.numero,
                                estat = taula.estat,
                                capacitat = taula.capacitat,
                                posX = taula.posX,
                                posY = taula.posY
                            )
                            val actualitzada = menjadorRepository.actualitzarTaula(taula.idTaula, taulaModificada)
                            if (!actualitzada) errors++
                        }
                    }
                    menjador.taules = taulesActualitzades
                }

                // Notificació del resultat de l'operació
                if (errors == 0) {
                    Toast.makeText(
                        this@PantallaGestionarMenjadors,
                        "S'han guardat tots els canvis correctament",
                        Toast.LENGTH_SHORT
                    ).show()
                    carregarMenjadorsDesDeBDD()
                } else {
                    Toast.makeText(
                        this@PantallaGestionarMenjadors,
                        "S'han guardat els canvis amb $errors errors",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@PantallaGestionarMenjadors,
                    "Error en guardar el plànol: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Extensió per convertir valors DP a Píxels segons la densitat de la pantalla actual.
     */
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()
}