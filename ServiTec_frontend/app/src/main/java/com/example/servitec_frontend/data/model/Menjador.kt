package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class Menjador (
    val idMenjador: Int,
    val nomMenjador: String,
    val actiu: Boolean,
    var taules: List<Taula>
)
data class PostMenjadorDTO(
    @SerializedName("PostNomMenjador") val postNomMenjador: String,
    @SerializedName("PostActiu") val postActiu: Boolean,
    @SerializedName("PostTaules") val postTaules: List<PostTaulaDTO>
)