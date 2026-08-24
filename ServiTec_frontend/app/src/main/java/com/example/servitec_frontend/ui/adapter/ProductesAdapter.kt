package com.example.servitec_frontend.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.ProducteDTO

class ProductesAdapter(
    private var llista: List<ProducteDTO>,
    private val onProducteClick: (ProducteDTO) -> Unit

) : RecyclerView.Adapter<ProductesAdapter.ViewHolder>() {
    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvNombre: TextView = vista.findViewById(R.id.tvNombreProducto)
        val tvPrecio: TextView = vista.findViewById(R.id.tvPrecioProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_producte, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = llista[position]
        holder.tvNombre.text = producto.nom
        holder.tvPrecio.text = "${producto.preu}€"

        holder.itemView.setOnClickListener {
            onProducteClick(producto)
        }
    }

    override fun getItemCount() = llista.size
    fun actualitzarLlista(novaLlista: List<ProducteDTO>) {
        this.llista = novaLlista
        notifyDataSetChanged()
    }
}