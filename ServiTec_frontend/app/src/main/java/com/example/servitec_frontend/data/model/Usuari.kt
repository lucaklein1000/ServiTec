// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        UsuariDTO.kt
// Descripció:    Objecte de transferència de dades que representa la informació
//                d un usuari autenticat i el seu token JWT d accés.
// ============================================================================

package com.example.servitec_frontend.data.model

data class UsuariDTO(
    val idUsuari: Int,
    val nomUsuari: String,
    val rol: String,
    val admin: Boolean,
    val actiu: Boolean,
    val token: String? = null
)
data class UpdateUsuariDTO(
    val nomUsuari : String,
    val contrasenya : String,
    val actiu : Boolean,
    val admin : Boolean,
    val rol : String
)

data class CreateUsuariDTO(
    val nomUsuari: String,
    val contrasenya: String,
    val actiu: Boolean,
    val admin: Boolean,
    val rol: String
)