package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class Producte(
    @SerializedName("idProducte") val idProducte: Int,
    @SerializedName("nom") val nom: String,
    @SerializedName("descripcio") val descripcio: String,
    @SerializedName("quantitat") val quantitat: Int,
    @SerializedName("preu") val preu: Double,
    @SerializedName("idCategoria") var idCategoria: Int,
    @SerializedName("actiu") val actiu: Boolean

){
    val preuTotal: Double
        get() = quantitat * preu
}

data class ProducteDTO(
    val idProducte: Int,
    val nom: String,
    val descripcio: String,
    val preu: Double,
    val actiu: Boolean,
    val idCategoria: Int
)
data class CreateProdcuteDTO(
    val nom: String,
    val descripcio: String,
    val preu: Double,
    val actiu: Boolean,
    val idCategoria: Int
)

data class UpdateProdcuteDTO(
    val nom: String,
    val descripcio: String,
    val preu: Double,
    val actiu: Boolean,
    val idCategoria: Int ?
)
