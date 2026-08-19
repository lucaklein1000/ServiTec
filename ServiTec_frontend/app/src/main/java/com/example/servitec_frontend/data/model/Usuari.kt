package com.example.servitec_frontend.data.model
import com.google.gson.annotations.SerializedName

data class UsuariDTO(
    val idUsuari: Int,
    val nomUsuari: String,
    val actiu: Boolean,
    val admin: Boolean,
    val rol: String
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