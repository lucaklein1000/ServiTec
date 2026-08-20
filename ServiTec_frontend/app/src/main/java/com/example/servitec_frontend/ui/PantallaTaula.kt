package com.example.servitec_frontend.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.CreateLiniaComandaDTO
import com.example.servitec_frontend.data.model.LiniaComandaTemporal
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.repository.ProducteRepository
import com.example.servitec_frontend.repository.TaulaRepository
import com.example.servitec_frontend.ui.adapter.CategoriesAdapter
import com.example.servitec_frontend.ui.adapter.ProductesAdapter
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class PantallaTaula : AppCompatActivity() {

    private lateinit var adapterProductes: ProductesAdapter
    private lateinit var adapterCentre: ComandaColorAdapter
    private lateinit var tvTotalPreu: TextView
    private lateinit var btnEnviar: Button
    private lateinit var btnCobrar: Button
    private lateinit var btnSumarProducte: Button
    private lateinit var btnSortir: Button
    private lateinit var btnTreureCompte: Button
    private lateinit var btnDemanarSegons: Button
    private lateinit var btnBorrar: com.google.android.material.button.MaterialButton
    private lateinit var btnCambiarOrde : MaterialButton
    private lateinit var mostrarNumeroTaula: TextView

    private var totsElsProductes = listOf<Producte>()
    private lateinit var taulaRepository: TaulaRepository
    private lateinit var producteRepository: ProducteRepository
    private val historialGuardat = mutableListOf<LiniaComandaTemporal>()
    private val productesSeleccionats = mutableListOf<LiniaComandaTemporal>()

    private var idComandaActiva = -1
    private var estatComandaActiva = "oberta"
    private var producteBorrar: LiniaComandaTemporal? = null

    private lateinit var tvQuantitat: TextView
    private var quantitatTeclejada = "1"
    private var quantitatEditada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_taula)

        initViews()

        val idTaulaActual = intent.getIntExtra("idTaula", -1)
        val nTaulaActual = intent.getStringExtra("nTaula") ?: "Taula"
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        val idUsuariActual = sharedPreferences.getInt("idUsuari", -1)
        val taulaOcupada = intent.getBooleanExtra("taulaOcupada", false)
        btnCambiarOrde = findViewById(R.id.btnCambiarOrden)
        taulaRepository = TaulaRepository(this)
        producteRepository = ProducteRepository(this)
        mostrarNumeroTaula.text = nTaulaActual

        // 1. Categorías (Izquierda)
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategorias)
        rvCategories.layoutManager = LinearLayoutManager(this)

        // 2. Ticket Combinado (Centro)
        val rvCentre = findViewById<RecyclerView>(R.id.rvPedido)
        rvCentre.layoutManager = LinearLayoutManager(this)

        adapterCentre = ComandaColorAdapter(emptyList()) { itemPulsat ->
            producteBorrar = itemPulsat
            Toast.makeText(this, "Seleccionat: ${itemPulsat.producte.nom}", Toast.LENGTH_SHORT).show()
        }
        rvCentre.adapter = adapterCentre

        // 3. Catálogo de Productos (Derecha)
        val rvProductes = findViewById<RecyclerView>(R.id.rvSeleccionProductos)
        rvProductes.layoutManager = GridLayoutManager(this, 2)

        setupTeclatNumeric()

        // Carga inicial de datos de la mesa
        if (taulaOcupada && idTaulaActual != -1) {
            lifecycleScope.launch {
                val comandaActiva = taulaRepository.obtenirComandaActiva(idTaulaActual)

                if (comandaActiva != null) {
                    idComandaActiva = comandaActiva.idComanda
                    estatComandaActiva = comandaActiva.estat
                    productesSeleccionats.clear()
                    historialGuardat.clear()

                    comandaActiva.liniaComanda?.forEach { linea ->
                        val prod = linea.idProducteNavigation
                        if (prod != null) {
                            historialGuardat.add(
                                LiniaComandaTemporal(
                                    idLiniaComanda = linea.idLiniaComanda,
                                    producte = prod,
                                    quantitat = linea.quantitat,
                                    preu = linea.preuUnitari,
                                    total = linea.subtotal,
                                    estat = linea.estat ?: "Enviat"
                                )
                            )
                        }
                    }
                    actualitzarTotalInterficie()
                }
            }
        }

        // Selección de productos de la carta
        adapterProductes = ProductesAdapter(emptyList()) { productoPulsado ->
            val q = quantitatTeclejada.toIntOrNull() ?: 1
            val itemExistente = productesSeleccionats.find { it.producte.idProducte == productoPulsado.idProducte }

            if (itemExistente != null) {
                itemExistente.quantitat += q
                itemExistente.total = itemExistente.producte.preu * itemExistente.quantitat
            } else {
                productesSeleccionats.add(
                    LiniaComandaTemporal(
                        producte = productoPulsado,
                        quantitat = q,
                        preu = productoPulsado.preu,
                        total = q * productoPulsado.preu,
                        estat = "pendentEnviar"
                    )
                )
            }

            borrarNumeroTeclat()
            actualitzarTotalInterficie()
        }
        rvProductes.adapter = adapterProductes

        // Carga inicial de categorías y catálogo
        lifecycleScope.launch {
            val categoriesBD = taulaRepository.obtenirCategories()
            val productesBD = taulaRepository.obtenerProductos() ?: emptyList()
            totsElsProductes = productesBD

            if (categoriesBD != null) {
                val adapterCategories = CategoriesAdapter(categoriesBD) { categoriaPulsada ->
                    val productesFiltrats = totsElsProductes.filter { it.idCategoria == categoriaPulsada.idCategoria }
                    adapterProductes.actualitzarLlista(productesFiltrats)
                }
                rvCategories.adapter = adapterCategories

                if (categoriesBD.isNotEmpty()) {
                    val primeraCatId = categoriesBD[0].idCategoria
                    val productesInicials = totsElsProductes.filter { it.idCategoria == primeraCatId }
                    adapterProductes.actualitzarLlista(productesInicials)
                }
            }
        }

        // Acciones de Botones
        btnEnviar.setOnClickListener {
            if (productesSeleccionats.isEmpty()) {
                Toast.makeText(this, "No hi ha cap producte nou per enviar a cuina", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                btnEnviar.isEnabled = false

                // Enviamos la categoría modificada si existe, de lo contrario la original del producto
                val novesLiniesDto = productesSeleccionats.map { l ->
                    CreateLiniaComandaDTO(
                        postIdProducte = l.producte.idProducte,
                        postQuantitat = l.quantitat,
                        postEstat = l.estat,
                        postIdCategoria = l.idCategoriaModificada ?: l.producte.idCategoria
                    )
                }

                val comandaActiva = taulaRepository.obtenirComandaActiva(idTaulaActual)
                val idComandaObtinguda = comandaActiva?.idComanda ?: -1
                val exit: Boolean

                if (taulaOcupada && idComandaObtinguda > 0) {
                    val resultat = taulaRepository.afegirLinies(idComandaObtinguda, novesLiniesDto)
                    exit = resultat.isSuccess
                } else {
                    val novaComandaDto = CreateComandaDTO(
                        postEstat = "oberta",
                        postIdTaula = idTaulaActual,
                        postIdUsuari = idUsuariActual,
                        postLinies = novesLiniesDto
                    )
                    exit = taulaRepository.enviarComanda(novaComandaDto)
                }

                if (exit) {
                    Toast.makeText(this@PantallaTaula, "Comanda enviada a cuina correctament!", Toast.LENGTH_LONG).show()
                    productesSeleccionats.clear()
                    actualitzarTotalInterficie()
                    finish()
                } else {
                    Toast.makeText(this@PantallaTaula, "Error en connectar amb el servidor", Toast.LENGTH_LONG).show()
                }
                btnEnviar.isEnabled = true
            }
        }

        btnSumarProducte.setOnClickListener {
            val elemento = producteBorrar
            if (elemento != null) {
                if (elemento.idLiniaComanda == 0) {
                    elemento.quantitat += 1
                    elemento.total = elemento.preu * elemento.quantitat
                } else {
                    productesSeleccionats.add(
                        LiniaComandaTemporal(
                            producte = elemento.producte,
                            quantitat = 1,
                            preu = elemento.preu,
                            total = elemento.preu,
                            estat = "pendentEnviar"
                        )
                    )
                }
                actualitzarTotalInterficie()
            } else {
                Toast.makeText(this, "Selecciona un producte primer", Toast.LENGTH_SHORT).show()
            }
        }

        btnBorrar.setOnClickListener {
            val elemento = producteBorrar
            if (elemento != null) {
                if (elemento.idLiniaComanda == 0) {
                    productesSeleccionats.remove(elemento)
                    producteBorrar = null
                    adapterCentre.netejarSeleccio()
                    actualitzarTotalInterficie()
                    Toast.makeText(this, "Producte eliminat", Toast.LENGTH_SHORT).show()
                } else {
                    lifecycleScope.launch {
                        btnBorrar.isEnabled = false
                        val exit = taulaRepository.eliminarLiniaComanda(elemento.idLiniaComanda)
                        if (exit) {
                            if (elemento.quantitat == 1) {
                                elemento.quantitat -= 1
                                elemento.estat = "Eliminat"
                                elemento.total = 0.0
                            } else {
                                elemento.quantitat -= 1
                                elemento.total -= elemento.preu
                            }
                            producteBorrar = null
                            adapterCentre.netejarSeleccio()
                            actualitzarTotalInterficie()
                            Toast.makeText(this@PantallaTaula, "Producte marcat com a eliminat", Toast.LENGTH_LONG).show()
                        }
                        btnBorrar.isEnabled = true
                    }
                }
            } else {
                Toast.makeText(this, "Selecciona un producte de la comanda per esborrar", Toast.LENGTH_SHORT).show()
            }
        }

        btnDemanarSegons.setOnClickListener {
            Toast.makeText(this, "Avís enviat a cuina: Marxa els segons!", Toast.LENGTH_SHORT).show()
        }

        btnTreureCompte.setOnClickListener {
            lifecycleScope.launch {
                btnTreureCompte.isEnabled = false
                if (idComandaActiva >= 1) {
                    taulaRepository.cambiarEstatComanda(idComandaActiva, "pendent")
                }
                btnTreureCompte.isEnabled = true
                productesSeleccionats.clear()
                actualitzarTotalInterficie()
                finish()
            }
        }

        btnCobrar.setOnClickListener {
            if (idComandaActiva == -1) {
                Toast.makeText(this, "No hi ha cap comanda activa per cobrar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (estatComandaActiva == "pendent") {
                lifecycleScope.launch {
                    btnCobrar.isEnabled = false
                    val cobrado = taulaRepository.cobrarComanda(idComandaActiva)

                    if (cobrado) {
                        Toast.makeText(this@PantallaTaula, "Mesa cobrada correctament!", Toast.LENGTH_LONG).show()
                        productesSeleccionats.clear()
                        actualitzarTotalInterficie()
                        finish()
                    } else {
                        Toast.makeText(this@PantallaTaula, "Error al cobrar la comanda", Toast.LENGTH_SHORT).show()
                    }
                    btnCobrar.isEnabled = true
                }
            } else {
                Toast.makeText(this, "No es pot cobrar una comanda activa (s'ha de demanar el compte primer)", Toast.LENGTH_SHORT).show()
            }
        }

        btnCambiarOrde.setOnClickListener {
            val element = producteBorrar
            if (element != null) {
                val categoriaActual = element.idCategoriaModificada ?: element.producte.idCategoria
                val novaCategoria = if (categoriaActual == 2) 3 else 2

                element.idCategoriaModificada = novaCategoria
                actualitzarTotalInterficie()

                val nomTipo = if (novaCategoria == 2) "Primer plat" else "Segon plat"
                Toast.makeText(this, "Canviat a: $nomTipo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Selecciona una línia primer", Toast.LENGTH_SHORT).show()
            }
        }

        btnSortir.setOnClickListener { finish() }
    }

    private fun initViews() {
        tvTotalPreu = findViewById(R.id.tvTotalPrecio)
        btnEnviar = findViewById(R.id.btnEnviar)
        btnSumarProducte = findViewById(R.id.btnSumarProducte)
        btnCobrar = findViewById(R.id.btnCobrar)
        btnSortir = findViewById(R.id.btnVolver)
        btnTreureCompte = findViewById(R.id.btnTreureCompte)
        btnDemanarSegons = findViewById(R.id.btnDemanarSegons)
        btnBorrar = findViewById(R.id.btnBorrarProductos)
        mostrarNumeroTaula = findViewById(R.id.tvTituloMesa)
        tvQuantitat = findViewById(R.id.tvQuantitatTeclejada)
    }

    private fun setupTeclatNumeric() {
        val botonsNumeros = mapOf(
            R.id.btnNum0 to "0", R.id.btnNum1 to "1", R.id.btnNum2 to "2", R.id.btnNum3 to "3",
            R.id.btnNum4 to "4", R.id.btnNum5 to "5", R.id.btnNum6 to "6", R.id.btnNum7 to "7",
            R.id.btnNum8 to "8", R.id.btnNum9 to "9"
        )

        botonsNumeros.forEach { (id, digit) ->
            findViewById<com.google.android.material.button.MaterialButton>(id).setOnClickListener {
                quantitatTeclejada = if (!quantitatEditada) digit else (quantitatTeclejada + digit).take(3)
                quantitatEditada = true
                tvQuantitat.text = "${quantitatTeclejada}x"
            }
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnNumC).setOnClickListener {
            borrarNumeroTeclat()
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnNumBorrarUn).setOnClickListener {
            if (quantitatEditada) {
                quantitatTeclejada = quantitatTeclejada.dropLast(1)
                if (quantitatTeclejada.isEmpty()) {
                    quantitatTeclejada = "1"
                    quantitatEditada = false
                }
            }
            tvQuantitat.text = "${quantitatTeclejada}x"
        }
    }

    private fun borrarNumeroTeclat() {
        quantitatTeclejada = "1"
        quantitatEditada = false
        tvQuantitat.text = "${quantitatTeclejada}x"
    }

    private fun actualitzarTotalInterficie() {
        val totsElsItems = mutableListOf<LiniaComandaTemporal>()
        totsElsItems.addAll(historialGuardat.filter { it.estat != "Eliminat" })
        totsElsItems.addAll(productesSeleccionats)

        totsElsItems.sortBy { it.idCategoriaModificada ?: it.producte.idCategoria }

        adapterCentre.actualitzarLlista(totsElsItems)

        val granTotal = totsElsItems.sumOf { it.total }
        tvTotalPreu.text = "${String.format("%.2f", granTotal)}€"
    }

    // Adaptador interno con control de selección y colores correctos según la categoría modificada
    inner class ComandaColorAdapter(
        private var llista: List<LiniaComandaTemporal>,
        private val onItemClick: (LiniaComandaTemporal) -> Unit
    ) : RecyclerView.Adapter<ComandaColorAdapter.ComandaViewHolder>() {

        private var posicioSeleccionada: Int = RecyclerView.NO_POSITION

        inner class ComandaViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComandaViewHolder {
            val tv = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_producte_ticket, parent, false) as TextView
            return ComandaViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ComandaViewHolder, position: Int) {
            val item = llista[position]
            val context = holder.itemView.context

            // Usamos la categoría modificada o la del producto por defecto
            val catEfectiva = item.idCategoriaModificada ?: item.producte.idCategoria

            val resIdColor = when (catEfectiva) {
                1 -> R.color.blauMenu
                2 -> R.color.primerPlat
                3 -> R.color.negre
                4 -> R.color.postres
                else -> R.color.blauMenu
            }

            // Aplicar fondo resaltado si coincide con la selección actual
            if (posicioSeleccionada == holder.bindingAdapterPosition) {
                holder.textView.setBackgroundColor(Color.parseColor("#E2E8F0"))
            } else {
                holder.textView.setBackgroundColor(Color.TRANSPARENT)
            }

            holder.textView.apply {
                setTextColor(ContextCompat.getColor(context, resIdColor))
                text = "${item.quantitat}x ${item.producte.nom}"
                setOnClickListener {
                    val posAnterior = posicioSeleccionada
                    posicioSeleccionada = holder.bindingAdapterPosition

                    notifyItemChanged(posAnterior)
                    notifyItemChanged(posicioSeleccionada)

                    onItemClick(item)
                }
            }
        }

        override fun getItemCount(): Int = llista.size

        fun actualitzarLlista(novaLlista: List<LiniaComandaTemporal>) {
            this.llista = novaLlista
            notifyDataSetChanged()
        }

        fun netejarSeleccio() {
            val posAnterior = posicioSeleccionada
            posicioSeleccionada = RecyclerView.NO_POSITION
            if (posAnterior != RecyclerView.NO_POSITION) {
                notifyItemChanged(posAnterior)
            }
        }
    }
}