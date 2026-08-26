package com.example.servitec_frontend.data.model

data class TaulaDTO(
    val idTaula: Int,
    var numero: Int,
    val capacitat: Int,
    val estat: Boolean,
    val estatComanda: String? = null,
    val idMenjador: Int,
    var posX: Float,
    var posY: Float,
    val bloquejada: Boolean = false,
    val usuariBloqueig: String? = null
)


data class CreateTaulaDTO(
    val numero: Int,
    val capacitat: Int,
    val estat: Boolean,
    val idMenjador: Int,
    val posX: Float,
    val posY: Float
)

data class UpdateTaulaDTO(
    val numero: Int,
    val capacitat: Int,
    val estat: Boolean,
    val posX: Float,
    val posY: Float
)

data class BloqueigRequestDTO(
    val nomCambrer: String
)