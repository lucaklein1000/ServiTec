// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Menjador.kt
// Descripció:    Model de dades i DTOs per a la gestió dels menjadors/sales
//                del restaurant i les taules que en formen part.
// ============================================================================

package com.example.servitec_frontend.data.model

/**
 * Model de dades que representa un menjador o sala del restaurant.
 * Inclou la llista de taules assignades a l'espai.
 *
 * @property idMenjador Identificador únic del menjador.
 * @property nomMenjador Nom de la sala o zona del restaurant (p. ex., Terrassa, Sala Principal).
 * @property actiu Estat de disponibilitat de la sala.
 * @property taules Llista de taules ([TaulaDTO]) que conté aquesta sala.
 */
data class Menjador(
    val idMenjador: Int,
    var nomMenjador: String,
    val actiu: Boolean,
    var taules: List<TaulaDTO>
)

/**
 * DTO utilitzat per crear un nou menjador i assignar-li les taules inicials.
 *
 * @property nomMenjador Nom del nou menjador.
 * @property actiu Estat inicial d'activació del menjador.
 * @property taules Llista de DTOs ([CreateTaulaDTO]) de les taules que s'ubicaran a la sala.
 */
data class CreateMenjadorDTO(
    val nomMenjador: String,
    val actiu: Boolean,
    val taules: List<CreateTaulaDTO>
)

/**
 * DTO utilitzat per actualitzar la informació general d'un menjador existent.
 *
 * @property nomMenjador Nou nom assignat al menjador.
 */
data class UpdateMenjadorDTO(
    val nomMenjador: String
)