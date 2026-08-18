package com.example.servitec_frontend.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Menjador
import com.example.servitec_frontend.data.model.PostTaulaDTO
import com.example.servitec_frontend.data.model.PutTaulaDTO
import com.example.servitec_frontend.data.model.Taula
import com.example.servitec_frontend.repository.TaulaRepository
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class PantallaGestionarMenjadors : AppCompatActivity() {

    private lateinit var btnTornar: MaterialButton
    private lateinit var btnGuardarPlano: MaterialButton
    private lateinit var actvMenjadors: AutoCompleteTextView
    private lateinit var canvasMenjador: RelativeLayout
    private lateinit var tvHintCanvas: TextView

    private val menjadorRepository = TaulaRepository()

    // Almacenamos la lista real de comedores traída de la BDD/API
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
        btnTornar = findViewById(R.id.btnTornar)
        btnGuardarPlano = findViewById(R.id.btnGuardarPlano)
        actvMenjadors = findViewById(R.id.actvMenjadors)
        canvasMenjador = findViewById(R.id.canvasMenjador)
        tvHintCanvas = findViewById(R.id.tvHintCanvas)
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

        // LISTENERS PARA AÑADIR MESAS NUEVAS DESDE LA PALETA DE LA IZQUIERDA
        findViewById<View>(R.id.dragTaula2Pax)?.setOnClickListener { afegirNovaTaula(2) }
        findViewById<View>(R.id.dragTaula4Pax)?.setOnClickListener { afegirNovaTaula(4) }
        findViewById<View>(R.id.dragTaula6Pax)?.setOnClickListener { afegirNovaTaula(6) }
        findViewById<View>(R.id.dragTaula8Pax)?.setOnClickListener { afegirNovaTaula(8) }
    }

    /**
     * Carga los comedores desde el backend / BDD mediante Corrutinas.
     */
    private fun carregarMenjadorsDesDeBDD() {
        lifecycleScope.launch {
            try {
                // Traemos la lista real del repositorio
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
        // Mapeamos los nombres para mostrarlos en el desplegable
        val nomMenjadors = llistaMenjadors.map { it.nomMenjador }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, nomMenjadors)
        actvMenjadors.setAdapter(adapter)

        // LISTEN PARA DETECTAR LA SELECCIÓN DEL USUARIO
        actvMenjadors.setOnItemClickListener { parent, _, position, _ ->
            val nomSeleccionat = parent.getItemAtPosition(position) as String

            // Buscamos el objeto Menjador exacto correspondiente al nombre seleccionado
            menjadorSeleccionat = llistaMenjadors.find { it.nomMenjador == nomSeleccionat }

            // Cargamos el plano con las mesas del comedor seleccionado
            carregarPlanoMenjador(menjadorSeleccionat)
        }
    }

    /**
     * Carga y dibuja las mesas del comedor seleccionado en el canvas.
     */
    private fun carregarPlanoMenjador(menjador: Menjador?) {
        if (menjador == null) return

        // Ocultamos el texto de sugerencia si hay mesas o si ya seleccionó un comedor
        tvHintCanvas.visibility = View.GONE

        // Pasamos la lista de mesas del comedor actual para pintarlas
        dibuixarTaulesEnLlenç(menjador.taules)
    }

    /**
     * Crea un objeto Taula nuevo centrado en el plano y lo renderiza.
     */
    private fun afegirNovaTaula(capacitatPax: Int) {
        val menjador = menjadorSeleccionat
        if (menjador == null) {
            Toast.makeText(this, "Selecciona primer un menjador!", Toast.LENGTH_SHORT).show()
            return
        }

        val novesTaules = menjador.taules.toMutableList()
        val nouNumero = (novesTaules.maxOfOrNull { it.numero } ?: 0) + 1

        val novaTaula = Taula(
            idTaula = 0, // Id 0 indica que es una mesa nueva pendiente de guardar en BDD
            numero = nouNumero,
            capacitat = capacitatPax,
            posX = 50f, // Aparece centrada en el 50% X
            posY = 50f, // Aparece centrada en el 50% Y
            estat = false,
            estatComanda = "lliure",
            idMenjador = menjador.idMenjador
        )

        novesTaules.add(novaTaula)
        menjador.taules = novesTaules

        // Redibujamos el lienzo con la nueva mesa incorporada
        dibuixarTaulesEnLlenç(novesTaules)

        Toast.makeText(this, "Afegida Taula $nouNumero ($capacitatPax PAX)", Toast.LENGTH_SHORT).show()
    }

    /**
     * Método para pintar las mesas en el lienzo (Canvas).
     */
    private fun dibuixarTaulesEnLlenç(taules: List<Taula>) {
        canvasMenjador.removeAllViews()

        if (taules.isEmpty()) return

        canvasMenjador.post {
            val canvasWidth = canvasMenjador.width.toFloat()
            val canvasHeight = canvasMenjador.height.toFloat()

            if (canvasWidth == 0f || canvasHeight == 0f) return@post

            for (taula in taules) {
                // Convertir % a píxeles
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

                    // Tamaño fijo para la mesa
                    layoutParams = RelativeLayout.LayoutParams(sizePx, sizePx)

                    // Posicionamiento absoluto centrado (X e Y directos)
                    x = pixelX - (sizePx / 2f)
                    y = pixelY - (sizePx / 2f)

                    tag = taula
                }

                hacerMesaArrastrable(taulaView, taula)
                canvasMenjador.addView(taulaView)
            }
        }
    }

    /**
     * Lógica de arrastre que calcula y guarda de nuevo la posición en porcentaje (0 - 100%)
     */
    private fun hacerMesaArrastrable(view: View, taula: Taula) {
        var dX = 0f
        var dY = 0f

        view.setOnTouchListener { v, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> {
                    dX = v.x - event.rawX
                    dY = v.y - event.rawY
                    true
                }
                android.view.MotionEvent.ACTION_MOVE -> {
                    val newX = event.rawX + dX
                    val newY = event.rawY + dY

                    val parentWidth = canvasMenjador.width.toFloat()
                    val parentHeight = canvasMenjador.height.toFloat()

                    // Delimitar márgenes dentro del lienzo
                    val maxX = parentWidth - v.width
                    val maxY = parentHeight - v.height

                    val clampedX = newX.coerceIn(0f, maxX.coerceAtLeast(0f))
                    val clampedY = newY.coerceIn(0f, maxY.coerceAtLeast(0f))

                    v.x = clampedX
                    v.y = clampedY

                    // Convertir de nuevo los píxeles a Porcentaje (%) para guardar correctamente en la BDD
                    val centroX = clampedX + (v.width / 2f)
                    val centroY = clampedY + (v.height / 2f)

                    taula.posX = (centroX / parentWidth) * 100f
                    taula.posY = (centroY / parentHeight) * 100f

                    true
                }
                else -> false
            }
        }
    }

    private fun guardarCanvisPlano(menjador: Menjador) {
        lifecycleScope.launch {
            try {
                val taules = menjador.taules
                if (taules.isNullOrEmpty()) {
                    Toast.makeText(this@PantallaGestionarMenjadors, "No hi ha taules per guardar", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                var errors = 0

                // Recorremos cada mesa para actualizar o crear sus datos en la BDD
                for (taula in taules) {
                    val exit = if (taula.idTaula == 0) {
                        val taulaNova = PostTaulaDTO(
                            postNumero = taula.numero,
                            postEstat = taula.estat,
                            postCapacitat = taula.capacitat,
                            postPosX = taula.posX,
                            postPosY = taula.posY,
                            postIdMenjador = menjador.idMenjador
                        )
                        menjadorRepository.crearTaula(taulaNova)
                    } else {
                        // Si ya tiene un idTaula válido, actualizamos con el DTO
                        val taulaModificada = PutTaulaDTO(
                            putNumero = taula.numero,
                            putEstat = taula.estat,
                            putCapacitat = taula.capacitat,
                            putPosX = taula.posX,
                            putPosY = taula.posY
                        )
                        menjadorRepository.actualitzarTaula(taula.idTaula, taulaModificada)
                    }
                }

                if (errors == 0) {
                    Toast.makeText(
                        this@PantallaGestionarMenjadors,
                        "S'han guardat tots els canvis de ${menjador.nomMenjador}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Recargamos datos de la BDD para obtener los IDs reales generados por el backend
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

    // Función de extensión auxiliar para DP -> PX
    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()
}