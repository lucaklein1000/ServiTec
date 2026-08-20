// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Comanda.kt
// Descripció:    Models de dades i DTOs associats a la gestió de comandes,
//                línies de detall i integració amb la pantalla de cuina.
// ============================================================================

package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

/**
 * Objecte de transferència de dades per a la creació d'una nova línia de comanda (POST).
 *
 * @property idProducte Identificador del producte afegit.
 * @property quantitat Quantitat de unitats sol·licitades.
 * @property estat Estat inicial de la línia.
 * @property idCategoria Identificador de la categoria del producte.
 */
data class CreateLiniaComandaDTO(
    @SerializedName("idProducte") val idProducte: Int,
    @SerializedName("quantitat") val quantitat: Int,
    @SerializedName("estat") val estat: String,
    @SerializedName("idCategoria") val idCategoria: Int
)

/**
 * Objecte de transferència de dades per al canvi d'estat d'una comanda o línia.
 *
 * @property nouEstat Nou estat que s'assignarà a l'entitat.
 */
data class CanviarEstatDTO(
    @SerializedName("nouEstat") val nouEstat: String
)

/**
 * Objecte de transferència de dades per a la creació d'una nova comanda (POST).
 *
 * @property estat Estat inicial de la comanda.
 * @property idTaula Identificador de la taula assignada.
 * @property idUsuari Identificador del cambrer o usuari que crea la comanda.
 * @property linies Llista de línies de detall que formen la comanda.
 */
data class CreateComandaDTO(
    @SerializedName("estat") val estat: String,
    @SerializedName("idTaula") val idTaula: Int,
    @SerializedName("idUsuari") val idUsuari: Int,
    @SerializedName("linies") val linies: List<CreateLiniaComandaDTO>
)

/**
 * Objecte de transferència de dades per a la lectura d'una línia de comanda.
 *
 * @property idLinia Identificador únic de la línia.
 * @property idComanda Identificador de la comanda a la qual pertany.
 * @property idProducte Identificador del producte associat.
 * @property quantitat Unitats de producte.
 * @property preuUnitari Preu individual del producte.
 * @property subtotal Import total de la línia.
 * @property estat Estat actual de la línia en cuina/sala.
 * @property idCategoria Identificador opcional de la categoria del producte.
 */
data class LiniaComandaDTO(
    @SerializedName("idLinia") val idLinia: Int,
    @SerializedName("idComanda") val idComanda: Int? = null,
    @SerializedName("idProducte") val idProducte: Int,
    @SerializedName("quantitat") val quantitat: Int,
    @SerializedName("preuUnitari") val preuUnitari: Double,
    @SerializedName("subtotal") val subtotal: Double,
    @SerializedName("estat") val estat: String? = "Pendent",
    @SerializedName("idCategoria") val idCategoria: Int? = null,
    val idProducteNavigation: Producte? = null
)

/**
 * Objecte de transferència de dades per a la lectura general d'una comanda.
 *
 * @property idComanda Identificador únic de la comanda.
 * @property dataCreacio Data i hora de registre de la comanda.
 * @property estat Estat general de la comanda.
 * @property total Import total acumulat.
 * @property idTaula Identificador de la taula associada.
 * @property idUsuari Identificador de l'usuari que la gestiona.
 * @property liniaComanda Llista amb el detall de les línies de la comanda.
 */
data class ComandaDTO(
    @SerializedName("idComanda") val idComanda: Int,
    @SerializedName("dataCreacio") val dataCreacio: String?,
    @SerializedName("estat") val estat: String,
    @SerializedName("total") val total: Double,
    @SerializedName("idTaula") val idTaula: Int,
    @SerializedName("idUsuari") val idUsuari: Int,
    @SerializedName("liniaComanda") val liniaComanda: List<LiniaComandaDTO>? = emptyList()
)

/**
 * Model de dades per a la visualització de comandes a la interfície de cuina.
 *
 * @property idComanda Identificador únic de la comanda.
 * @property idTaula Identificador de la taula.
 * @property numTaula Número o nom visible de la taula.
 * @property dataHora Hora de recepció de la comanda.
 * @property linies Llista de línies de producte pendents de preparació.
 */
data class Cuina(
    @SerializedName("idComanda") val idComanda: Int,
    @SerializedName("idTaula") val idTaula: Int,
    @SerializedName("numTaula") val numTaula: String?,
    @SerializedName("dataHora") val dataHora: String,
    @SerializedName("linies") val linies: List<LiniaCuinaDTO>
)