// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ComandaAdapter.kt
// Descripció:    Adapter de RecyclerView per gestionar les línies de comanda
//                temporals. Controla la selecció individual d'ítems, la
//                visualització de productes eliminats i el càlcul del preu subtotal.
// ============================================================================

package com.example.servitec_frontend.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.LiniaComandaTemporal

/**
 * Adapter encarregat de renderitzar les línies de comanda temporals en un RecyclerView.
 * Gestiona el control de l'element seleccionat en memòria, el format del preu acumulat i l'estat visual.
 *
 * @param llista Llista inicial de les línies de comanda afegides.
 * @param onItemClick Callback executat en seleccionar una línia de la comanda.
 */
class ComandaAdapter(
    private var llista: List<LiniaComandaTemporal>,
    private val onItemClick: (LiniaComandaTemporal) -> Unit
) : RecyclerView.Adapter<ComandaAdapter.ViewHolder>() {

    // Variable per guardar l'índex de l'element seleccionat (-1 = cap)
    private var posicioSeleccionada: Int = RecyclerView.NO_POSITION

    /**
     * ViewHolder que manté les referències visuals als camps de quantitat, nom i preu.
     */
    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvQuantitat: TextView = vista.findViewById(R.id.tvQuantitatCentre)
        val tvNom: TextView = vista.findViewById(R.id.tvNomCentre)
        val tvPreu: TextView = vista.findViewById(R.id.tvPreuCentre)
    }

    /**
     * Infla el disseny XML de l'ítem individual de la comanda.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_comanda, parent, false)
        return ViewHolder(v)
    }

    /**
     * Enllaça les dades de la línia de comanda, calcula el subtotal i actualitza l'estat visual segons selecció o eliminació.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = llista[position]

        holder.tvQuantitat.text = "${item.quantitat}x"
        holder.tvNom.text = item.producte.nom

        // Càlcul del preu total segons la quantitat de productes
        val preuTotal = item.producte.preu * item.quantitat
        holder.tvPreu.text = "${String.format("%.2f", preuTotal)}€"

        // Format visual segons l'estat de la línia (Eliminat / Seleccionat / Normal)
        if (item.estat == "Eliminat") {
            // Fondo vermell si s'ha marcat com a eliminat
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red_deleted)
            )
        } else {
            // Canvi de color de fons segons la selecció actual
            if (position == posicioSeleccionada) {
                holder.itemView.setBackgroundColor(Color.parseColor("#DBEAFE")) // Blau clar
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        // Esdeveniment de selecció optimitzat utilitzant bindingAdapterPosition
        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition

            if (currentPosition != RecyclerView.NO_POSITION) {
                val posicioAnterior = posicioSeleccionada
                posicioSeleccionada = currentPosition

                // Refrescar únicament les dues files afectades pel canvi de selecció
                notifyItemChanged(posicioAnterior)
                notifyItemChanged(posicioSeleccionada)

                onItemClick(item)
            }
        }
    }

    /**
     * Retorna el nombre total d'elements continguts a la llista.
     */
    override fun getItemCount(): Int = llista.size

    /**
     * Actualitza la llista de línies de comanda i reseteja la selecció activa.
     */
    fun actualitzarLlista(novaLlista: List<LiniaComandaTemporal>) {
        this.llista = novaLlista.toList()
        posicioSeleccionada = RecyclerView.NO_POSITION
        notifyDataSetChanged()
    }

    /**
     * Deixa sense efecte qualsevol selecció activa i refresca la fila que ho requeria.
     */
    fun netejarSeleccio() {
        val posAnterior = posicioSeleccionada
        posicioSeleccionada = RecyclerView.NO_POSITION
        if (posAnterior != RecyclerView.NO_POSITION) {
            notifyItemChanged(posAnterior)
        }
    }
}