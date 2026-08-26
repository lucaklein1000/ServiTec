// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Producte.kt
// Descripció:    Model de dades i DTOs relacionats amb la gestió de productes,
//                preus i el catàleg del menú del restaurant.
// ============================================================================

package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

/**
 * Model de dades de l'entitat Producte utilitzat en les operacions de detall i selecció.
 * Inclou càlculs derivats com el preu total segons la quantitat seleccionada.
 *
 * @property idProducte Identificador únic del producte.
 * @property nom Nom comercial del producte.
 * @property descripcio Descripció detallada o ingredients.
 * @property quantitat Unitats seleccionades o en stock temporal.
 * @property preu Preu unitari del producte.
 * @property idCategoria Identificador de la categoria a la qual pertany.
 * @property actiu Estat de disponibilitat del producte al catàleg.
 */
data class Producte(
    val idProducte: Int,
    val nom: String,
    val descripcio: String,
    val quantitat: Int,
    val preu: Double,
    var idCategoria: Int,
    val actiu: Boolean
) {
    /**
     * Propietat calculada que retorna el cost total de la quantitat seleccionada d'aquest producte.
     */
    val preuTotal: Double
        get() = quantitat * preu
}

/**
 * DTO utilitzat per a la transferència de dades del catàleg de productes des del backend.
 *
 * @property idProducte Identificador del producte.
 * @property nom Nom del producte.
 * @property descripcio Informació descriptiva del producte.
 * @property preu Preu de venda unitari.
 * @property actiu Indica si el producte està visible i disponible per a comandes.
 * @property idCategoria Identificador de la categoria associada.
 */
data class ProducteDTO(
    val idProducte: Int,
    val nom: String,
    val descripcio: String,
    val preu: Double,
    val actiu: Boolean,
    val idCategoria: Int
)

/**
 * DTO utilitzat per enviar la informació necessària per crear un nou producte al menú.
 *
 * @property nom Nom del nou producte.
 * @property descripcio Descripció o detalls del nou producte.
 * @property preu Preu unitari assignat.
 * @property actiu Estat inicial de visibilitat.
 * @property idCategoria Identificador de la categoria on s'integrarà.
 */
data class CreateProdcuteDTO(
    val nom: String,
    val descripcio: String,
    val preu: Double,
    val actiu: Boolean,
    val idCategoria: Int
)

/**
 * DTO utilitzat per actualitzar la informació d'un producte existent.
 * Permet reassignar o mantenir la categoria opcionalment.
 *
 * @property nom Nom actualitzat del producte.
 * @property descripcio Nova descripció del producte.
 * @property preu Nou preu unitari.
 * @property actiu Estat de disponibilitat actualitzat.
 * @property idCategoria Identificador de la nova categoria (opcional).
 */
data class UpdateProdcuteDTO(
    val nom: String,
    val descripcio: String,
    val preu: Double,
    val actiu: Boolean,
    val idCategoria: Int?
)