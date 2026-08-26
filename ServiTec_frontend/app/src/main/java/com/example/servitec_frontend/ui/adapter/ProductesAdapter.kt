// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ProductesAdapter.kt
// Descripció:    Adapter de RecyclerView per a la llista de productes del catàleg.
//                Gestiona el renderitzat del nom i preu de cada article, la
//                interacció mitjançant la lambda d'esdeveniment i l'actualització
//                dinàmica del conjunt de dades.
// ============================================================================

package com.example.servitec_frontend.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.ProducteDTO

/**
 * Adapter encarregat de renderitzar els productes individuals dins d'un RecyclerView.
 * Permet seleccionar un producte mitjançant un callback i refrescar dinàmicament la llista mostrada.
 */
class ProductesAdapter(
    private var llista: List<ProducteDTO>,
    private val onProducteClick: (ProducteDTO) -> Unit
) : RecyclerView.Adapter<ProductesAdapter.ViewHolder>() {

    /**
     * ViewHolder que manté les referències visuals al nom i preu del producte.
     */
    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvNombre: TextView = vista.findViewById(R.id.tvNombreProducto)
        val tvPrecio: TextView = vista.findViewById(R.id.tvPrecioProducto)
    }

    /**
     * Infla el layout XML d'un ítem individual de la llista de productes.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_producte, parent, false)
        return ViewHolder(v)
    }

    /**
     * Enllaça les dades d'un producte amb els camps de text de la vista i assigna el listener de selecció.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = llista[position]
        holder.tvNombre.text = producto.nom
        holder.tvPrecio.text = "${producto.preu}€"

        // Esdeveniment de clic sobre el producte per executar la funció d'ordre superior rebuda
        holder.itemView.setOnClickListener {
            onProducteClick(producto)
        }
    }

    /**
     * Retorna el nombre total de productes continguts a la llista actual.
     */
    override fun getItemCount(): Int = llista.size

    /**
     * Actualitza el conjunt de dades de l'adaptador i notifica el canvi per refrescar la vista visualment.
     */
    fun actualitzarLlista(novaLlista: List<ProducteDTO>) {
        // Substitució de la llista actual i notificació al RecyclerView
        this.llista = novaLlista
        notifyDataSetChanged()
    }
}