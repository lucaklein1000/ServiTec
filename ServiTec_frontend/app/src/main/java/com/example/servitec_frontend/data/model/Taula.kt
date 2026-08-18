package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class Taula(
    @SerializedName("idTaula") val idTaula: Int,
    @SerializedName("numero") val numero: Int,
    @SerializedName("capacitat") val capacitat: Int,
    @SerializedName("estat") val estat: Boolean,
    @SerializedName("estatComanda") val estatComanda: String? = null,
    @SerializedName("idMenjador") val idMenjador: Int,
    @SerializedName("posX") var posX: Float,
    @SerializedName("posY") var posY: Float
)


data class PostTaulaDTO(
    @SerializedName("PostNumero") val postNumero: Int,
    @SerializedName("PostCapacitat") val postCapacitat: Int,
    @SerializedName("PostEstat") val postEstat: Boolean,
    @SerializedName("PostIdMenjador") val postIdMenjador: Int,
    @SerializedName("PostPosX") val postPosX: Float,
    @SerializedName("PostPosY") val postPosY: Float
)

data class PutTaulaDTO(
    @SerializedName("PutNumero") val putNumero: Int,
    @SerializedName("PutCapacitat") val putCapacitat: Int,
    @SerializedName("PutEstat") val putEstat: Boolean,
    @SerializedName("PutPosX") val putPosX: Float,
    @SerializedName("PutPosY") val putPosY: Float
)