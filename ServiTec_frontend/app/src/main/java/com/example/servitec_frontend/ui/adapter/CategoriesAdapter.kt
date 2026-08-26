// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        CategoriesAdapter.kt
// Descripció:    Adapter de RecyclerView per a la barra horitzontal o llista de
//                categories de la carta. Gestiona l'estat visual de la categoria
//                seleccionada i el filtrat de productes associat.
// ============================================================================

package com.example.servitec_frontend.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Categoria

/**
 * Adapter encarregat de renderitzar la llista de categories del restaurant.
 * Manté l'estat visual del selector actiu per visualitzar quina categoria s'està consultant.
 *
 * @param llista Llista de categories disponibles a la carta.
 * @param onCategoriaClick Callback executat en seleccionar una categoria per filtrar productes.
 */
class CategoriesAdapter(
    private val llista: List<Categoria>,
    private val onCategoriaClick: (Categoria) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    // Variable per controlar l'índex de la categoria actualment seleccionada (per defecte la primera)
    private var posicionSeleccionada = 0

    /**
     * ViewHolder que manté la referència al camp de text del nom de la categoria.
     */
    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val textview: TextView = vista.findViewById(R.id.tvNombreCategoria)
    }

    /**
     * Infla el disseny XML individual corresponent a un element de categoria.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_categoria, parent, false)
        return ViewHolder(v)
    }

    /**
     * Vincula les dades de la categoria i aplica l'estil ressaltat segons la selecció actual.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cat = llista[position]
        holder.textview.text = cat.nom

        // Aplicació del fons i color de text segons si l'element està seleccionat
        if (position == posicionSeleccionada) {
            holder.textview.setBackgroundResource(R.drawable.bg_button_selected)
            holder.textview.setTextColor(Color.WHITE)
        } else {
            holder.textview.setBackgroundColor(Color.TRANSPARENT)
            holder.textview.setTextColor(Color.BLACK)
        }

        // Esdeveniment de clic per actualitzar l'estat de selecció i notificar al formulari principal
        holder.itemView.setOnClickListener {
            posicionSeleccionada = position
            notifyDataSetChanged()
            onCategoriaClick(cat)
        }
    }

    /**
     * Retorna el nombre total de categories contingudes a la llista.
     */
    override fun getItemCount(): Int = llista.size
}