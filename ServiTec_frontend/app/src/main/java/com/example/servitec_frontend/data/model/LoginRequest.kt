// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        LoginRequest.kt
// Descripció:    Model de petició per a l'enviament de credencials d'accés
//                (usuari i contrasenya) cap a l'endpoint d'autenticació.
// ============================================================================

package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

/**
 * Objecte de transferència de dades per a la petició d'inici de sessió (POST /login).
 *
 * @property nomUsuari Nom d'usuari per a l'autenticació.
 * @property contrasenya Contrasenya associada a l'usuari.
 */
data class LoginRequest(
    @SerializedName("nomUsuari") val nomUsuari: String,
    @SerializedName("contrasenya") val contrasenya: String
)