// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        UsuariDTO.kt
// Descripció:    Objectes de transferència de dades (DTOs) per a la gestió,
//                creació i actualització d'usuaris del sistema.
// ============================================================================

package com.example.servitec_frontend.data.model

/**
 * DTO que representa la informació pública d'un usuari registrat al sistema,
 * incloent-hi el seu rol, estat d'activitat i el token JWT d'accés en cas d'autenticació.
 *
 * @property idUsuari Identificador únic de l'usuari a la base de dades.
 * @property nomUsuari Nom d'usuari utilitzat per accedir a la plataforma.
 * @property rol Rol o perfil assignat (p. ex., Cambrer, Cuiner, Administrador).
 * @property admin Indica si l'usuari disposa de privilegis d'administració.
 * @property actiu Estat del compte d'usuari (actiu o desactivat).
 * @property token Token de sessió JWT proporcionat pel backend.
 */
data class UsuariDTO(
    val idUsuari: Int,
    val nomUsuari: String,
    val rol: String,
    val admin: Boolean,
    val actiu: Boolean,
    val token: String? = null
)

/**
 * DTO utilitzat per enviar les dades d'actualització d'un usuari existent.
 *
 * @property nomUsuari Nou nom d'usuari assignat.
 * @property contrasenya Nova contrasenya o la mateixa reescrita.
 * @property actiu Nou estat d'activació del compte.
 * @property admin Permisos d'administració assignats.
 * @property rol Rol funcional actualitzat.
 */
data class UpdateUsuariDTO(
    val nomUsuari: String,
    val contrasenya: String,
    val actiu: Boolean,
    val admin: Boolean,
    val rol: String
)

/**
 * DTO utilitzat per a la creació i registre d'un nou usuari al sistema.
 *
 * @property nomUsuari Nom d'usuari requerit per a l'accés.
 * @property contrasenya Contrasenya d'accés inicial.
 * @property actiu Estat inicial del compte (per defecte actiu).
 * @property admin Defineix si el nou usuari tindrà rols administratius.
 * @property rol Rol o funció assignada dins del restaurant.
 */
data class CreateUsuariDTO(
    val nomUsuari: String,
    val contrasenya: String,
    val actiu: Boolean,
    val admin: Boolean,
    val rol: String
)