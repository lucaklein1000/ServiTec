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

class PantallaGestionarMenjadors : AppCompatActivity() {

    private lateinit var btnTornar: MaterialButton
    private lateinit var btnGuardarPlano: MaterialButton
    private lateinit var actvMenjadors: AutoCompleteTextView
    private lateinit var canvasMenjador: RelativeLayout
    private lateinit var tvHintCanvas: TextView
    private var zonaEsborrar: View? = null

    private lateinit var menjadorRepository: TaulaRepository

    private var llistaMenjadors: List<Menjador> = emptyList()
    private var menjadorSeleccionat: Menjador? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_gestionar_menjadors)

        initViews()
        setupListeners()
        carregarMenjadorsDesDeBDD()
    }

    private fun initViews() {
        menjadorRepository = TaulaRepository(this)
        btnTornar = findViewById(R.id.btnTornar)
        btnGuardarPlano = findViewById(R.id.btnGuardarPlano)
        actvMenjadors = findViewById(R.id.actvMenjadors)
        canvasMenjador = findViewById(R.id.canvasMenjador)
        tvHintCanvas = findViewById(R.id.tvHintCanvas)

        zonaEsborrar = findViewById(R.id.areaPapelera)
            ?: (findViewById<View>(R.id.dragTaula2Pax)?.parent as? View)
    }

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

        findViewById<View>(R.id.dragTaula2Pax)?.setOnClickListener { afegirNovaTaula(2) }
        findViewById<View>(R.id.dragTaula4Pax)?.setOnClickListener { afegirNovaTaula(4) }
        findViewById<View>(R.id.dragTaula6Pax)?.setOnClickListener { afegirNovaTaula(6) }
        findViewById<View>(R.id.dragTaula8Pax)?.setOnClickListener { afegirNovaTaula(8) }
    }

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

    private fun setupDropdownMenjadors() {
        val nomMenjadors = llistaMenjadors.map { it.nomMenjador }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomMenjadors)
        actvMenjadors.setAdapter(adapter)

        actvMenjadors.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position) as String
            menjadorSeleccionat = llistaMenjadors.find { it.nomMenjador == nomSeleccionat }
            carregarPlanoMenjador(menjadorSeleccionat)
        }
    }

    private fun carregarPlanoMenjador(menjador: Menjador?) {
        if (menjador == null) return
        tvHintCanvas.visibility = View.GONE
        dibuixarTaulesEnLlenç(menjador.taules)
    }

    private fun afegirNovaTaula(capacitatPax: Int) {
        val menjador = menjadorSeleccionat
        if (menjador == null) {
            Toast.makeText(this, "Selecciona primer un menjador!", Toast.LENGTH_SHORT).show()
            return
        }

        val novesTaules = menjador.taules.toMutableList()
        val nouNumero = (novesTaules.maxOfOrNull { it.numero } ?: 0) + 1

        // Usamos TaulaDTO con idTaula = 0 para representar la mesa no guardada
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

    private fun dibuixarTaulesEnLlenç(taules: List<TaulaDTO>) {
        canvasMenjador.removeAllViews()
        if (taules.isEmpty()) return

        canvasMenjador.post {
            val canvasWidth = canvasMenjador.width.toFloat()
            val canvasHeight = canvasMenjador.height.toFloat()

            if (canvasWidth == 0f || canvasHeight == 0f) return@post

            for (taula in taules) {
                val pixelX = (taula.posX / 100f) * canvasWidth
                val pixelY = (taula.posY / 100f) * canvasHeight

                val sizePx = 160.toPx()

                val taulaView = androidx.appcompat.widget.AppCompatButton(this).apply {
                    text = "Taula ${taula.numero}\n(${taula.capacitat} Pax)"
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

    private fun hacerMesaInteractiva(view: View, taulaDTO: TaulaDTO) {
        var dX = 0f
        var dY = 0f
        var startX = 0f
        var startY = 0f
        val clickThreshold = 10f

        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    startX = event.rawX
                    startY = event.rawY
                    v.bringToFront()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY

                    v.x = newX
                    v.y = newY

                    val parentWidth = canvasMenjador.width.toFloat()
                    val parentHeight = canvasMenjador.height.toFloat()

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

    private fun esDropEnPaleta(view: View): Boolean {
        val targetView = zonaEsborrar ?: return false

        val viewRect = Rect()
        view.getGlobalVisibleRect(viewRect)

        val targetRect = Rect()
        targetView.getGlobalVisibleRect(targetRect)

        return Rect.intersects(viewRect, targetRect)
    }

    private fun esDropFueraDelCanvas(view: View): Boolean {
        val viewRect = Rect()
        view.getGlobalVisibleRect(viewRect)

        val canvasRect = Rect()
        canvasMenjador.getGlobalVisibleRect(canvasRect)

        return !Rect.intersects(viewRect, canvasRect) || viewRect.centerX() < canvasRect.left
    }

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

        val centroX = clampedX + (view.width / 2f)
        val centroY = clampedY + (view.height / 2f)

        taulaDTO.posX = (centroX / parentWidth) * 100f
        taulaDTO.posY = (centroY / parentHeight) * 100f
    }

    private fun mostrarDialogoCambiarNumero(taulaDTO: TaulaDTO) {
        if (!taulaDTO.estat) {
            Toast.makeText(this, "No es pot canviar el número d'una taula ocupada", Toast.LENGTH_LONG).show()
            return
        }

        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(taulaDTO.numero.toString())
            setSelection(text.length)
        }

        AlertDialog.Builder(this)
            .setTitle("Canviar número de taula")
            .setMessage("Introdueix el nou número per a la taula:")
            .setView(input)
            .setPositiveButton("Guardar") { dialog, _ ->
                val nouNumero = input.text.toString().toIntOrNull()
                if (nouNumero != null && nouNumero > 0) {
                    taulaDTO.numero = nouNumero
                    menjadorSeleccionat?.let { dibuixarTaulesEnLlenç(it.taules) }

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

    private fun eliminarTaula(taulaDTO: TaulaDTO) {
        if (!taulaDTO.estat) {
            Toast.makeText(this, "No es pot eliminar una taula ocupada o amb comandes obertes", Toast.LENGTH_LONG).show()
            menjadorSeleccionat?.let { dibuixarTaulesEnLlenç(it.taules) }
            return
        }

        val menjador = menjadorSeleccionat ?: return

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
            val novesTaules = menjador.taules.toMutableList()
            novesTaules.remove(taulaDTO)
            menjador.taules = novesTaules

            dibuixarTaulesEnLlenç(novesTaules)
            Toast.makeText(this, "S'ha eliminat la Taula ${taulaDTO.numero}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarCanvisPlano(menjador: Menjador) {
        lifecycleScope.launch {
            try {
                val nouNom = actvMenjadors.text.toString().trim()
                if (nouNom.isEmpty()) {
                    Toast.makeText(this@PantallaGestionarMenjadors, "El nom del menjador no pot estar buit", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                var errors = 0

                // 1. Si el usuario ha cambiado el nombre del comedor en el AutoCompleteTextView
                if (nouNom != menjador.nomMenjador) {
                    val updateMenjadorDto =
                        UpdateMenjadorDTO(nomMenjador = nouNom) // O el modelo DTO que uses en tu API
                    val nomActualitzat = menjadorRepository.actualitzarMenjador(menjador.idMenjador, updateMenjadorDto)
                    if (!nomActualitzat) {
                        errors++
                    } else {
                        menjador.nomMenjador = nouNom // Actualizamos el objeto local
                    }
                }

                // 2. Guardar / Actualizar las mesas (lógica que ya teníamos)
                val taules = menjador.taules
                if (!taules.isNullOrEmpty()) {
                    val taulesActualitzades = taules.toMutableList()

                    for (i in taulesActualitzades.indices) {
                        val taula = taulesActualitzades[i]

                        if (taula.idTaula == 0) {
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

                if (errors == 0) {
                    Toast.makeText(
                        this@PantallaGestionarMenjadors,
                        "S'han guardat tots els canvis correctament",
                        Toast.LENGTH_SHORT
                    ).show()
                    carregarMenjadorsDesDeBDD() // Recarga y refresca el desplegable
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

    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()
}