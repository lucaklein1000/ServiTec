// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaTaula.kt
// Descripció:    Vista principal de gestió de taules (TPV). Permet afegir
//                productes, canviar d'ordre els plats, teclejar quantitats,
//                enviar comandes a cuina, demanar segons i cobrar.
// ============================================================================

package com.example.servitec_frontend.ui

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import com.example.servitec_frontend.data.model.ProducteDTO
import com.example.servitec_frontend.repository.ProducteRepository
import com.example.servitec_frontend.repository.TaulaRepository
import com.example.servitec_frontend.ui.adapter.CategoriesAdapter
import com.example.servitec_frontend.ui.adapter.ProductesAdapter
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

/**
 * Activitat encarregada de la gestió operativa d'una taula del restaurant.
 * Controla el catàleg de productes, el teclat numèric, el tiquet de la comanda
 * i la sincronització en temps real amb el servidor REST.
 */
class PantallaTaula : AppCompatActivity() {

    // Components de la interfície d'usuari
    private lateinit var adapterProductes: ProductesAdapter
    private lateinit var adapterCentre: ComandaColorAdapter
    private lateinit var tvTotalPreu: TextView
    private lateinit var btnEnviar: Button
    private lateinit var btnCobrar: Button
    private lateinit var btnSumarProducte: Button
    private lateinit var btnSortir: Button
    private lateinit var btnTreureCompte: Button
    private lateinit var btnDemanarSegons: Button
    private lateinit var btnBorrar: MaterialButton
    private lateinit var btnCambiarOrde: MaterialButton
    private lateinit var mostrarNumeroTaula: TextView
    private lateinit var tvQuantitat: TextView

    // Repositoris de dades per a la comunicació amb l'API REST
    private lateinit var taulaRepository: TaulaRepository
    private lateinit var producteRepository: ProducteRepository

    // Estat local i col·leccions de dades
    private var totsElsProductes = listOf<ProducteDTO>()
    private val historialGuardat = mutableListOf<LiniaComandaTemporal>()
    private val productesSeleccionats = mutableListOf<LiniaComandaTemporal>()

    // Control del context de la taula actual
    private var idTaulaActual = -1
    private var idComandaActiva = -1
    private var estatComandaActiva = "oberta"
    private var producteBorrar: LiniaComandaTemporal? = null

    // Lògica per a la gestió de la quantitat teclejada
    private var quantitatTeclejada = "1"
    private var quantitatEditada = false

    /**
     * Inicialitza la pantalla, configura els adaptadors dels RecyclerViews i defineix els esdeveniments dels botons.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_taula)

        // Inicialització de les vistes i referències de la interfície
        initViews()

        // Lectura de paràmetres enviats des del selector de taules
        idTaulaActual = intent.getIntExtra("idTaula", -1)
        val nTaulaActual = intent.getStringExtra("nTaula") ?: "Taula"
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        val idUsuariActual = sharedPreferences.getInt("idUsuari", -1)
        val taulaOcupada = intent.getBooleanExtra("taulaOcupada", false)

        btnCambiarOrde = findViewById(R.id.btnCambiarOrden)
        taulaRepository = TaulaRepository(this)
        producteRepository = ProducteRepository(this)
        mostrarNumeroTaula.text = nTaulaActual

        // Inicialització del panell esquerre (Categories)
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategorias)
        rvCategories.layoutManager = LinearLayoutManager(this)

        // Inicialització del panell central (Tiquet integrat de la comanda)
        val rvCentre = findViewById<RecyclerView>(R.id.rvPedido)
        rvCentre.layoutManager = LinearLayoutManager(this)

        adapterCentre = ComandaColorAdapter(emptyList()) { itemPulsat ->
            producteBorrar = itemPulsat
            Toast.makeText(this, "Seleccionat: ${itemPulsat.producte.nom}", Toast.LENGTH_SHORT).show()
        }
        rvCentre.adapter = adapterCentre

        // Inicialització del panell dret (Catàleg de productes)
        val rvProductes = findViewById<RecyclerView>(R.id.rvSeleccionProductos)
        rvProductes.layoutManager = GridLayoutManager(this, 2)

        // Configuració dels esdeveniments del teclat numèric en pantalla
        setupTeclatNumeric()

        // Càrrega inicial de la taula: Si està ocupada, recuperem les línies registrades al servidor
        if (taulaOcupada && idTaulaActual != -1) {
            lifecycleScope.launch {
                val comandaActiva = taulaRepository.obtenirComandaActiva(idTaulaActual)

                if (comandaActiva != null) {
                    idComandaActiva = comandaActiva.idComanda
                    estatComandaActiva = comandaActiva.estat
                    productesSeleccionats.clear()
                    historialGuardat.clear()

                    // Mapeig de les línies rebudes de l'API cap al model temporal visual
                    comandaActiva.liniaComanda?.forEach { linea ->
                        val prod = linea.idProducteNavigation
                        if (prod != null) {
                            historialGuardat.add(
                                LiniaComandaTemporal(
                                    idLiniaComanda = linea.idLinia,
                                    producte = prod,
                                    quantitat = linea.quantitat,
                                    preu = linea.preuUnitari,
                                    total = linea.subtotal,
                                    estat = linea.estat ?: "Enviat",
                                    idCategoriaModificada = linea.idCategoria ?: prod.idCategoria
                                )
                            )
                        }
                    }
                    actualitzarTotalInterficie()
                }
            }
        }

        // Adapter de productes: Gestió de selecció i addició de productes al tiquet local
        adapterProductes = ProductesAdapter(emptyList()) { productoPulsado ->
            // Verificació d'estat actiu del producte
            if (productoPulsado.actiu == false) {
                Toast.makeText(this, "Aquest producte està desactivat i no es pot afegir", Toast.LENGTH_SHORT).show()
                return@ProductesAdapter
            }

            val q = quantitatTeclejada.toIntOrNull() ?: 1
            val itemExistente = productesSeleccionats.find { it.producte.idProducte == productoPulsado.idProducte }

            // Si el producte ja està a la llista pendent, incrementem la quantitat; si no, el creem
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

        // Càrrega asíncrona de les categories i el catàleg de productes
        lifecycleScope.launch {
            val categoriesBD = taulaRepository.obtenirCategories()
            val productesBD = taulaRepository.obtenirProductes() ?: emptyList()

            // Filtrem per mantenir únicament els productes actius en el TPV
            totsElsProductes = productesBD.filter { it.actiu != false }

            if (categoriesBD != null) {
                val adapterCategories = CategoriesAdapter(categoriesBD) { categoriaPulsada ->
                    val productesFiltrats = totsElsProductes.filter { it.idCategoria == categoriaPulsada.idCategoria }
                    adapterProductes.actualitzarLlista(productesFiltrats)
                }
                rvCategories.adapter = adapterCategories

                // Mostrem per defecte els productes de la primera categoria trobada
                if (categoriesBD.isNotEmpty()) {
                    val primeraCatId = categoriesBD[0].idCategoria
                    val productesInicials = totsElsProductes.filter { it.idCategoria == primeraCatId }
                    adapterProductes.actualitzarLlista(productesInicials)
                }
            }
        }

        // Acció: Envia les noves línies pendents cap al backend / cuina
        btnEnviar.setOnClickListener {
            if (productesSeleccionats.isEmpty()) {
                Toast.makeText(this, "No hi ha cap producte nou per enviar a cuina", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                btnEnviar.isEnabled = false

                val novesLiniesDto = productesSeleccionats.map { l ->
                    CreateLiniaComandaDTO(
                        idProducte = l.producte.idProducte,
                        quantitat = l.quantitat,
                        estat = l.estat,
                        idCategoria = l.idCategoriaModificada ?: l.producte.idCategoria
                    )
                }

                val comandaActiva = taulaRepository.obtenirComandaActiva(idTaulaActual)
                val idComandaObtinguda = comandaActiva?.idComanda ?: -1
                val exit: Boolean

                // Afegim línies a la comanda existent o en creem una de nova si la taula estava lliure
                if (taulaOcupada && idComandaObtinguda > 0) {
                    val resultat = taulaRepository.afegirLinies(idComandaObtinguda, novesLiniesDto)
                    exit = resultat.isSuccess
                } else {
                    val novaComandaDto = CreateComandaDTO(
                        estat = "oberta",
                        idTaula = idTaulaActual,
                        idUsuari = idUsuariActual,
                        linies = novesLiniesDto
                    )
                    exit = taulaRepository.enviarComanda(novaComandaDto)
                }

                if (exit) {
                    Toast.makeText(this@PantallaTaula, "Comanda enviada a cuina correctament!", Toast.LENGTH_LONG).show()
                    productesSeleccionats.clear()
                    actualitzarTotalInterficie()
                    desbloquejariSortir()
                } else {
                    Toast.makeText(this@PantallaTaula, "Error en connectar amb el servidor", Toast.LENGTH_LONG).show()
                    btnEnviar.isEnabled = true
                }
            }
        }

        // Acció: Incrementa la quantitat de la línia de tiquet seleccionada
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

        // Acció: Esborra o decrementa un element de la comanda
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

        // Acció: Canvia l'estat de la comanda a "segons" per notificar a la cuina
        btnDemanarSegons.setOnClickListener {
            btnDemanarSegons.isEnabled = false
            lifecycleScope.launch {
                val exit = taulaRepository.canviarEstatComanda(idComandaActiva, "segons")
                if (exit) {
                    estatComandaActiva = "segons"
                    Toast.makeText(this@PantallaTaula, "Avís enviat a cuina: Marxa els segons!", Toast.LENGTH_SHORT).show()
                    desbloquejariSortir()
                } else {
                    Toast.makeText(this@PantallaTaula, "Error en enviar l'avís a cuina", Toast.LENGTH_SHORT).show()
                    btnDemanarSegons.isEnabled = true
                }
            }
        }

        // Acció: Demana el compte i canvia l'estat a "pendent"
        btnTreureCompte.setOnClickListener {
            lifecycleScope.launch {
                btnTreureCompte.isEnabled = false
                if (idComandaActiva >= 1) {
                    taulaRepository.canviarEstatComanda(idComandaActiva, "pendent")
                }
                productesSeleccionats.clear()
                actualitzarTotalInterficie()
                desbloquejariSortir()
            }
        }

        // Acció: Processa el cobrament final de la comanda
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
                        desbloquejariSortir()
                    } else {
                        Toast.makeText(this@PantallaTaula, "Error al cobrar la comanda", Toast.LENGTH_SHORT).show()
                        btnCobrar.isEnabled = true
                    }
                }
            } else {
                Toast.makeText(this, "No es pot cobrar una comanda activa (s'ha de demanar el compte primer)", Toast.LENGTH_SHORT).show()
            }
        }

        // Acció: Alterna la categoria d'ordre del plat entre primers i segons (ID 2 <-> ID 3)
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

        // Botó de sortida manual
        btnSortir.setOnClickListener {
            desbloquejariSortir()
        }
    }

    /**
     * Allibera el bloqueig de la taula al backend i tanca l'activitat.
     */
    private fun desbloquejariSortir() {
        // Comprovació de taula vàlida per alliberar la reserva
        if (idTaulaActual != -1) {
            lifecycleScope.launch {
                taulaRepository.desbloquejarTaula(idTaulaActual)
                finish()
            }
        } else {
            finish()
        }
    }

    /**
     * S'assegura d'alliberar el bloqueig de la taula al servidor si l'activitat es destrueix.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Garantir el desbloqueig al finalitzar el cicle de vida
        if (idTaulaActual != -1) {
            lifecycleScope.launch {
                taulaRepository.desbloquejarTaula(idTaulaActual)
            }
        }
    }

    /**
     * Vincula les vistes de la interfície amb les variables locals.
     */
    private fun initViews() {
        // Mapeig de vistes de la interfície
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

    /**
     * Configura la lògica del teclat numèric en pantalla per introduir quantitats.
     */
    private fun setupTeclatNumeric() {
        // Diccionari de botons numèrics
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

        // Neteja tot el buffer del teclat numèric
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnNumC).setOnClickListener {
            borrarNumeroTeclat()
        }

        // Esborra el darrer dígit introduït
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

    /**
     * Restableix la quantitat teclejada al valor per defecte de 1x.
     */
    private fun borrarNumeroTeclat() {
        // Reset del buffer numèric
        quantitatTeclejada = "1"
        quantitatEditada = false
        tvQuantitat.text = "${quantitatTeclejada}x"
    }

    /**
     * Recalcula l'import total de la comanda i actualitza el tiquet central.
     */
    private fun actualitzarTotalInterficie() {
        val totsElsItems = mutableListOf<LiniaComandaTemporal>()
        totsElsItems.addAll(historialGuardat.filter { it.estat != "Eliminat" })
        totsElsItems.addAll(productesSeleccionats)

        // Ordenació dels productes per categoria per a una presentació estructurada
        totsElsItems.sortBy { it.idCategoriaModificada ?: it.producte.idCategoria }

        adapterCentre.actualitzarLlista(totsElsItems)

        val granTotal = totsElsItems.sumOf { it.total }
        tvTotalPreu.text = "${String.format("%.2f", granTotal)}€"
    }

    /**
     * Adaptador del RecyclerView encarregat de dibuixar les línies del tiquet,
     * aplicant colors i fons dinàmics segons l'estat i la selecció activa.
     */
    inner class ComandaColorAdapter(
        private var llista: List<LiniaComandaTemporal>,
        private val onItemClick: (LiniaComandaTemporal) -> Unit
    ) : RecyclerView.Adapter<ComandaColorAdapter.ComandaViewHolder>() {

        private var posicioSeleccionada: Int = RecyclerView.NO_POSITION

        /**
         * ViewHolder que conté la vista de text per a cada línia de producte del tiquet.
         */
        inner class ComandaViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

        /**
         * Infla la vista de la línia del tiquet.
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComandaViewHolder {
            val tv = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_producte_ticket, parent, false) as TextView
            return ComandaViewHolder(tv)
        }

        /**
         * Enllaça les dades de la línia de la comanda amb la vista i aplica els colors i estils visual corresponents.
         */
        override fun onBindViewHolder(holder: ComandaViewHolder, position: Int) {
            val item = llista[position]
            val context = holder.itemView.context

            val catEfectiva = item.idCategoriaModificada ?: item.producte.idCategoria
            val esSegonsDemanat = estatComandaActiva.equals("segons", ignoreCase = true)
            val esCategoriaSegons = catEfectiva == 3

            // Assignació de colors del text segons la categoria de plat
            val resIdColor = when (catEfectiva) {
                1 -> R.color.blauMenu
                2 -> R.color.primerPlat
                3 -> R.color.negre
                4 -> R.color.postres
                else -> R.color.blauMenu
            }

            // Aplicació de fons dinàmics segons selecció o avís de segons plats a cuina
            if (posicioSeleccionada == holder.bindingAdapterPosition) {
                val shapeSeleccionat = GradientDrawable().apply {
                    setColor(ContextCompat.getColor(context, R.color.blauMenu))
                    cornerRadius = 8f
                }
                holder.textView.background = shapeSeleccionat
                holder.textView.setTextColor(Color.WHITE)
            } else if (esSegonsDemanat && esCategoriaSegons) {
                val shapeSegonsDemanats = GradientDrawable().apply {
                    setColor(ContextCompat.getColor(context, R.color.selecio_segons))
                    cornerRadius = 8f
                }
                holder.textView.background = shapeSegonsDemanats
                holder.textView.setTextColor(ContextCompat.getColor(context, resIdColor))
            } else {
                holder.textView.setBackgroundColor(Color.TRANSPARENT)
                holder.textView.setTextColor(ContextCompat.getColor(context, resIdColor))
            }

            holder.textView.apply {
                text = "${item.quantitat}x ${item.producte.nom}"
                setOnClickListener {
                    val posAnterior = posicioSeleccionada
                    posicioSeleccionada = holder.bindingAdapterPosition

                    // Notifiquem els canvis per refrescar la línia seleccionada anterior i la nova
                    notifyItemChanged(posAnterior)
                    notifyItemChanged(posicioSeleccionada)

                    onItemClick(item)
                }
            }
        }

        /**
         * Retorna el nombre total d'elements al tiquet.
         */
        override fun getItemCount(): Int = llista.size

        /**
         * Reemplaça el conjunt de dades i actualitza la vista.
         */
        fun actualitzarLlista(novaLlista: List<LiniaComandaTemporal>) {
            // Actualització de la llista del tiquet
            this.llista = novaLlista
            notifyDataSetChanged()
        }

        /**
         * Desmarca qualsevol element seleccionat al tiquet.
         */
        fun netejarSeleccio() {
            // Neteja de l'estat de selecció
            val posAnterior = posicioSeleccionada
            posicioSeleccionada = RecyclerView.NO_POSITION
            if (posAnterior != RecyclerView.NO_POSITION) {
                notifyItemChanged(posAnterior)
            }
        }
    }
}