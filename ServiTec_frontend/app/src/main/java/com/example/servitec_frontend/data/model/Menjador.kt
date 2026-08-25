package com.example.servitec_frontend.data.model

data class Menjador (
    val idMenjador: Int,
    var nomMenjador: String,
    val actiu: Boolean,
    var taules: List<TaulaDTO>
)
data class CreateMenjadorDTO(
    val nomMenjador: String,
    val actiu: Boolean,
    val taules: List<CreateTaulaDTO>
)

data class UpdateMenjadorDTO(
    val nomMenjador: String,
)