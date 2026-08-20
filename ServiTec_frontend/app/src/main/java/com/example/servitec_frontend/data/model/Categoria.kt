// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Categoria.kt
// Descripció:    Model de dades i DTOs associats a la gestió de categories
//                de la carta del restaurant.
// ============================================================================

package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

/**
 * Representa una categoria de productes de la carta del restaurant.
 *
 * @property idCategoria Identificador únic de la categoria.
 * @property nom Nom de la categoria (ex. "Begudes", "Postres").
 * @property descripcio Descripció opcional de la categoria.
 */
data class Categoria(
    @SerializedName("idCategoria") val idCategoria: Int,
    @SerializedName("nom") val nom: String,
    @SerializedName("descripcio") val descripcio: String?
)

/**
 * Objecte de transferència de dades per a l'actualització d'una categoria (PUT).
 *
 * @property nom Nou nom per a la categoria.
 * @property descripcio Nova descripció per a la categoria.
 */
data class UpdateCategoriaDTO(
    @SerializedName("nom") val nom: String,
    @SerializedName("descripcio") val descripcio: String?
)

/**
 * Objecte de transferència de dades per a la creació d'una nova categoria (POST).
 *
 * @property nom Nom de la nova categoria.
 * @property descripcio Descripció opcional de la nova categoria.
 */
data class CreateCategoriaDTO(
    @SerializedName("nom") val nom: String,
    @SerializedName("descripcio") val descripcio: String?
)