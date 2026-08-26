// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        LoginRequest.kt
// Descripció:    Models de transferència de dades (DTOs) per a l'enviament de
//                credencials d'accés i la recepció de la resposta d'autenticació
//                amb el token JWT.
// ============================================================================

package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

/**
 * Objecte de transferència de dades per a la petició d'inici de sessió (POST api/Auth/login).
 *
 * @property nomUsuari Nom d'usuari per a l'autenticació.
 * @property contrasenya Contrasenya associada al compte.
 */
data class LoginRequest(
    val nomUsuari: String,
    val contrasenya: String
)

/**
 * Objecte de transferència de dades que conté la resposta del backend després d'un login correcte.
 * Inclou la informació del perfil de l'usuari i el token d'autenticació JWT.
 *
 * @property idUsuari Identificador únic de l'usuari autenticat.
 * @property nomUsuari Nom d'usuari del compte.
 * @property rol Rol assignat a l'usuari (p. ex., Cambrer, Cuiner, Administrador).
 * @property admin Indica si l'usuari té privilegis d'administració.
 * @property token Token JWT utilitzat per a autoritzar les peticions HTTP posteriors.
 */
data class LoginResponse(
    @SerializedName("idUsuari") val idUsuari: Int,

    @SerializedName("nomUsuari") val nomUsuari: String,

    @SerializedName("rol") val rol: String,

    @SerializedName("admin") val admin: Boolean,

    @SerializedName("token") val token: String
)