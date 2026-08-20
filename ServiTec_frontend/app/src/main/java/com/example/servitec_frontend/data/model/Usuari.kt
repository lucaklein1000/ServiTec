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
import com.google.android.material.slider.BaseOnChangeListener
import com.google.gson.annotations.SerializedName
data class UsuariDTO(
    val idUsuari: Int,
    val nomUsuari: String,
    val rol: String,
    val admin: Boolean,
    val actiu: Boolean,
    val token: String? = null
)
data class PutUsuariDTO(
    @SerializedName("PutNomUsuari") val putNomUsuari : String,
    @SerializedName("PutContrasenya") val putContrasenya : String,
    @SerializedName("PutActiu") val putActiu : Boolean,
    @SerializedName("PutAdmin") val putAdmin : Boolean,
    @SerializedName("PutRol") val putRol : String
)

data class CrearUsuariDTO(
    @SerializedName("PostNomUsuari") val postNomUsuari: String,
    @SerializedName("PostContrasenya") val postContrasenya: String,
    @SerializedName("PostActiu") val postActiu: Boolean,
    @SerializedName("PostAdmin") val postAdmin: Boolean,
    @SerializedName("PostRol") val postRol: String
)