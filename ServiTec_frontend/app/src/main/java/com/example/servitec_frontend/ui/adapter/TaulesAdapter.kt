// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        TaulesAdapter.kt
// Descripció:    Adapter de RecyclerView per a la gestió de la llista de taules.
//                S'encarrega d'enllaçar la informació de cada taula (número i estat)
//                amb la seva representació visual i gestionar l'obertura del detall.
// ============================================================================

package com.example.servitec_frontend.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.TaulaDTO
import com.example.servitec_frontend.ui.PantallaTaula

/**
 * Adapter encarregat de renderitzar les meces/taules individuals dins d'un RecyclerView.
 * Gestiona el format de visualització segons l'estat d'ocupació i obre la pantalla de detall en fer-hi clic.
 */
class TaulesAdapter(private val llista: List<TaulaDTO>) : RecyclerView.Adapter<TaulesAdapter.ViewHolder>() {

    /**
     * ViewHolder que manté les referències als elements de la interfície visual de cada taula.
     */
    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvNumero: TextView = vista.findViewById(R.id.tvNumeroTaula)
    }

    /**
     * Infla el disseny XML per a cada element de la llista de taules.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.taula, parent, false)
        return ViewHolder(v)
    }

    /**
     * Vincula les dades d'una taula concreta amb la seva cel·la corresponent i configura els escoltadors d'esdeveniments.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taulaAcutal = llista[position]
        val context = holder.itemView.context

        // Assignació del número de taula formatat
        holder.tvNumero.text = "T.${taulaAcutal.numero}"

        // Canvi d'estil visual si la taula es troba ocupada
        if (!taulaAcutal.estat) {
            holder.itemView.setBackgroundResource(R.color.taula_ocupada2)
        }

        // Navegació cap a la pantalla de detall de la taula
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PantallaTaula::class.java).apply {
                putExtra("idTaula", taulaAcutal.idTaula)
                putExtra("nTaula", "Taula ${taulaAcutal.numero}")
                putExtra("taulaOcupada", true)
            }
            context.startActivity(intent)
        }
    }

    /**
     * Retorna el nombre total de taules contingudes a la llista.
     */
    override fun getItemCount(): Int = llista.size
}