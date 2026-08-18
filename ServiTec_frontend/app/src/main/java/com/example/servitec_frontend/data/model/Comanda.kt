package com.example.servitec_frontend.data.model

import com.google.gson.annotations.SerializedName

data class CreateLiniaComandaDTO(
    val postIdProducte: Int,
    val postQuantitat: Int,
    val postEstat: String,
    val postIdCategoria: Int
)

data class CanviarEstatDTO(
    val nouEstat: String
)

data class CreateComandaDTO(
    val postEstat: String,
    val postIdTaula: Int,
    val postIdUsuari: Int,
    val postLinies: List<CreateLiniaComandaDTO>
)

data class LiniaComandaDTO(
    @SerializedName(value = "idLinia", alternate = ["idLiniaComanda", "IdLinia", "IdLiniaComanda"])
    val idLinia: Int? = null,
    val idComanda: Int? = null,
    val idProducte: Int,
    val quantitat: Int,
    val preuUnitari: Double,
    val subtotal: Double,
    val estat: String? = "Pendent"
)

data class ComandaDTO(
    val idComanda: Int,
    val dataCreacio: String?,
    val estat: String,
    val total: Double,
    val idTaula: Int,
    val idUsuari: Int,
    val liniaComanda: List<LiniaComandaDTO>? = emptyList()
)

data class ResponseComnada(
    val idComanda: Int,
    val dataCreacio: String,
    val estat: String,
    val total: Double,
    val idTaula: Int,
    val idUsuari: Int,
    // Aquí se mapean las líneas que vienen de C#
    val liniaComanda: List<ResponseLiniaComanda> = emptyList()
)

data class ResponseLiniaComanda(
    @SerializedName(value = "idLiniaComanda", alternate = ["idLinia", "IdLinia", "IdLiniaComanda"])
    val idLiniaComanda: Int = 0,
    val quantitat: Int,
    val preuUnitari: Double,
    val subtotal: Double,
    val idProducte: Int,
    val estat: String,
    // Mapeamos el objeto producto que va dentro de la línea
    val idProducteNavigation: Producte? = null
)

data class ResponseCuina(
    @SerializedName("idComanda") val idComanda: Int,
    @SerializedName("idTaula") val idTaula: Int,
    @SerializedName("numTaula") val numTaula: String?,
    @SerializedName("dataHora") val dataHora: String,
    @SerializedName("linies") val linies: List<LiniaCuinaDTO>
)