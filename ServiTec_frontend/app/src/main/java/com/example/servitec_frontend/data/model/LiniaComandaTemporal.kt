// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        LiniaComandaTemporal.kt
// Descripció:    Models de dades temporals per a la gestió local de línies
//                de comanda en memòria i DTOs per a la vista de cuina.
// ============================================================================

package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model temporal utilitzat en memòria local (Front-end) per gestionar les noves línies
 * d'una comanda en procés de creació o edició abans de ser enviades a la base de dades.
 *
 * @property idLiniaComanda Identificador temporal o assignat (0 per defecte en noves línies).
 * @property producte Objecte complet del producte seleccionat.
 * @property quantitat Nombre d'unitats seleccionades.
 * @property preu Preu unitari del producte en el moment de la selecció.
 * @property total Import subtotal calculat (quantitat * preu).
 * @property estat Estat actual de la línia en el flux de treball (ex. "Pendent").
 * @property idCategoriaModificada Categoria associada en cas de modificació especial.
 */
data class LiniaComandaTemporal(
    val idLiniaComanda: Int = 0,
    val producte: Producte,
    var quantitat: Int,
    var preu: Double,
    var total: Double,
    var estat: String,
    var idCategoriaModificada: Int? = null
)

/**
 * Objecte de transferència de dades simplificat per a la representació
 * individual de línies de comanda a la pantalla de cuina.
 *
 * @property idLiniaComanda Identificador únic de la línia de comanda.
 * @property nomProducte Nom visible del producte a preparar.
 * @property quantitat Nombre d'unitats a preparar.
 * @property idCategoria Identificador de la categoria per a filtratge a cuina.
 */
data class LiniaCuinaDTO(
    @SerializedName("idLiniaComanda") val idLiniaComanda: Int,
    @SerializedName("nomProducte") val nomProducte: String,
    @SerializedName("quantitat") val quantitat: Int,
    @SerializedName("idCategoria") val idCategoria: Int
)