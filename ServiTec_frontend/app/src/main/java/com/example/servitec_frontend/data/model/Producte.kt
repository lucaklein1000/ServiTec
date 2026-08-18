package com.example.servitec_frontend.data.model

import android.text.BoringLayout
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
data class PostProducteDTO(
    val postNom: String,
    val postDescripcio: String,
    val postPreu: Double,
    val postActiu: Boolean,
    val postIdCategoria: Int
)

data class PutProducteDTO(
    @SerializedName("PutNom") val putNom: String,
    @SerializedName("PutDescripcio") val putDescripcio: String,
    @SerializedName("PutPreu") val putPreu: Double,
    @SerializedName("PutActiu") val putActiu: Boolean,
    @SerializedName("PutIdCategoria") val putIdCategoria: Int ?
)
