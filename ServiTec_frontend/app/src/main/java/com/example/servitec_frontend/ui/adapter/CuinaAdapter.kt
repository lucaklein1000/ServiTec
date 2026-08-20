// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        CuinaAdapter.kt
// Descripció:    Adaptador de RecyclerView per gestionar la visualització i
//                marcatge de plats pendents a la pantalla de cuina.
// ============================================================================

package com.example.servitec_frontend.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.servitec_frontend.R
import com.example.servitec_frontend.data.model.Cuina
import com.example.servitec_frontend.repository.TaulaRepository
import kotlinx.coroutines.launch

/**
 * Adaptador encarregat de renderitzar els tiquets de comandes de cuina
 * organitzats per categories i gestionar l actualització d estat dels plats a "Servit".
 *
 * @param comandes Llista de comandes actives destinades a la cuina.
 */
class CuinaAdapter(
    private val comandes: MutableList<Cuina>
) : RecyclerView.Adapter<CuinaAdapter.CuinaViewHolder>() {

    class CuinaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumTaula: TextView = view.findViewById(R.id.tvNumTaula)
        val tvHoraComanda: TextView = view.findViewById(R.id.tvHoraComanda)
        val containerBebidas: LinearLayout = view.findViewById(R.id.containerBebidas)
        val containerPrimeros: LinearLayout = view.findViewById(R.id.containerPrimeros)
        val containerSegundos: LinearLayout = view.findViewById(R.id.containerSegundos)
        val containerPostres: LinearLayout = view.findViewById(R.id.containerPostres)
        val containerCafes: LinearLayout = view.findViewById(R.id.containerCafes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuinaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comanda_cuina, parent, false)
        return CuinaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CuinaViewHolder, position: Int) {
        val comanda = comandes[position]

        // 1. Mostrar nom de la taula
        holder.tvNumTaula.text = "Taula ${comanda.numTaula}"

        // 2. Extreure i formatar l hora (de "2026-07-23T14:30:00" extreu "14:30")
        holder.tvHoraComanda.text = if (comanda.dataHora?.contains("T") == true) {
            comanda.dataHora.substringAfter("T").take(5)
        } else {
            comanda.dataHora ?: ""
        }

        // 3. Netejar els contenidors per si el ViewHolder es reutilitza
        holder.containerBebidas.removeAllViews()
        holder.containerPrimeros.removeAllViews()
        holder.containerSegundos.removeAllViews()
        holder.containerPostres.removeAllViews()
        holder.containerCafes.removeAllViews()

        // Contador per dur el control local de plats actius en aquest tiquet
        var platsPendentsInTicket = comanda.linies.size

        // 4. Recórrer les línies de comanda
        for (linia in comanda.linies) {
            val nombreValido = linia.nomProducte ?: "Producte sense nom"

            val tvPlato = TextView(holder.itemView.context).apply {
                text = "${linia.quantitat}x  $nombreValido"
                textSize = 14f
                setTextColor(Color.BLACK)
                setPadding(0, 8, 0, 8)
            }

            tvPlato.setOnClickListener {
                val idLinia = linia.idLiniaComanda ?: return@setOnClickListener
                val context = holder.itemView.context

                // Ocultar el plat visualment
                tvPlato.visibility = View.GONE
                platsPendentsInTicket--

                // Si era l últim plat de la comanda, traiem el tiquet sencer de la llista
                if (platsPendentsInTicket <= 0) {
                    val posActual = holder.bindingAdapterPosition
                    if (posActual != RecyclerView.NO_POSITION && posActual in comandes.indices) {
                        comandes.removeAt(posActual)
                        notifyItemRemoved(posActual)
                        notifyItemRangeChanged(posActual, comandes.size)
                    }
                }

                // Instanciem el repositori amb el Context per enviar el token JWT
                val taulaRepository = TaulaRepository(context)

                // Executem la petició associada al cicle de vida de la vista
                holder.itemView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch {
                    try {
                        val exit = taulaRepository.canviarEstatLinia(idLinia, "Servit")
                        if (!exit) {
                            tvPlato.visibility = View.VISIBLE
                            Toast.makeText(context, "Error en actualitzar l'estat", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        tvPlato.visibility = View.VISIBLE
                        Toast.makeText(context, "Error de connexió: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Classificació de productes per categoria
            when (linia.idCategoria) {
                1 -> holder.containerBebidas.addView(tvPlato)
                2 -> holder.containerPrimeros.addView(tvPlato)
                3 -> holder.containerSegundos.addView(tvPlato)
                4 -> holder.containerPostres.addView(tvPlato)
                5 -> holder.containerCafes.addView(tvPlato)
                else -> holder.containerPrimeros.addView(tvPlato)
            }
        }
    }

    override fun getItemCount(): Int = comandes.size
}