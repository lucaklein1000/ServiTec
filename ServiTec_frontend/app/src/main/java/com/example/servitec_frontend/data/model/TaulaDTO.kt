// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        TaulaDTO.kt
// Descripció:    Objectes de transferència de dades (DTOs) per a la gestió,
//                disposició espacial (coordenades X/Y), bloqueig concurrent
//                i estat de les taules del restaurant.
// ============================================================================

package com.example.servitec_frontend.data.model

/**
 * DTO que representa la informació completa d'una taula del restaurant.
 * Inclou la posicionació al plànol del menjador, capacitat, estat de comanda i dades de bloqueig per concorrència.
 *
 * @property idTaula Identificador únic de la taula.
 * @property numero Número assignat a la taula.
 * @property capacitat Nombre màxim de comensals permesos.
 * @property estat Estat operatiu o disponibilitat de la taula (oberta/tancada).
 * @property estatComanda Estat actual de la comanda associada (p. ex., Pendent, En preparació, Servit).
 * @property idMenjador Identificador del menjador o sala on es troba la taula.
 * @property posX Coordenada X per a la renderització visual al plànol interactiu.
 * @property posY Coordenada Y per a la renderització visual al plànol interactiu.
 * @property bloquejada Indica si la taula està sent utilitzada actualment per un altre cambrer.
 * @property usuariBloqueig Nom del cambrer que manté el bloqueig actiu sobre la taula.
 */
data class TaulaDTO(
    val idTaula: Int,
    var numero: Int,
    val capacitat: Int,
    val estat: Boolean,
    val estatComanda: String? = null,
    val idMenjador: Int,
    var posX: Float,
    var posY: Float,
    val bloquejada: Boolean = false,
    val usuariBloqueig: String? = null
)

/**
 * DTO utilitzat per al registre i creació d'una nova taula en un menjador concret.
 *
 * @property numero Número identificatiu de la nova taula.
 * @property capacitat Nombre de seients o comensals.
 * @property estat Estat inicial de disponibilitat.
 * @property idMenjador Identificador de la sala assignada.
 * @property posX Posició horitzontal inicial al plànol.
 * @property posY Posició vertical inicial al plànol.
 */
data class CreateTaulaDTO(
    val numero: Int,
    val capacitat: Int,
    val estat: Boolean,
    val idMenjador: Int,
    val posX: Float,
    val posY: Float
)

/**
 * DTO utilitzat per actualitzar les propietats o la posició d'una taula existent al plànol.
 *
 * @property numero Número assignat a la taula.
 * @property capacitat Capacitat de comensals actualitzada.
 * @property estat Nou estat operatiu.
 * @property posX Nova coordenada X desada després de moure la taula a la interfície.
 * @property posY Nova coordenada Y desada després de moure la taula a la interfície.
 */
data class UpdateTaulaDTO(
    val numero: Int,
    val capacitat: Int,
    val estat: Boolean,
    val posX: Float,
    val posY: Float
)

/**
 * DTO per sol·licitar el bloqueig temporal d'una taula per evitar conflictes de concorrència entre cambrers.
 *
 * @property nomCambrer Nom de l'usuari/cambrer que inicia l'atenció a la taula.
 */
data class BloqueigRequestDTO(
    val nomCambrer: String
)