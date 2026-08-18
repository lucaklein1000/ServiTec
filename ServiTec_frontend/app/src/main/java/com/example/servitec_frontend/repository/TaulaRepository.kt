package com.example.servitec_frontend.repository

import android.util.Log
import android.util.Log.e
import com.example.servitec_frontend.data.model.CanviarEstatDTO
import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.ComandaDTO
import com.example.servitec_frontend.data.model.CrearUsuariDTO
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.CreateLiniaComandaDTO
import com.example.servitec_frontend.data.model.Menjador
import com.example.servitec_frontend.data.model.PostCategoriaDTO
import com.example.servitec_frontend.data.model.PostMenjadorDTO
import com.example.servitec_frontend.data.model.PostTaulaDTO
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.PutCategoriaDTO
import com.example.servitec_frontend.data.model.PutTaulaDTO
import com.example.servitec_frontend.data.model.PutUsuariDTO
import com.example.servitec_frontend.data.model.ResponseComnada
import com.example.servitec_frontend.data.model.Taula
import com.example.servitec_frontend.data.model.UsuariDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class TaulaRepository {

    private val apiService = RetrofitClient.instance

    suspend fun obtenirComandaActiva(idMesa: Int): ResponseComnada? {
        return try {
            val response = apiService.obtenirComandaActiva(idMesa)
            if (response.isSuccessful) {
                response.body() // Devuelve la comanda si encuentra un 200 OK
            } else {
                null // Devuelve null si es un 404 (Mesa libre)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun obtenirCategories(): List<Categoria>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCategories()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun crearCategoria(nomCategoria: PostCategoriaDTO): Boolean {
        return try {
            val response = apiService.crearCategoria(nomCategoria)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun actualitzarCategoria(idCategoria: Int, categoria: PutCategoriaDTO): Boolean {
        return try {
            val response = apiService.actualitzarCategoria(idCategoria, categoria)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun obtenerProductos(): List<Producte>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProducts()
                if (response.isSuccessful) response.body() else null
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun enviarComanda(dto: CreateComandaDTO): Boolean {
        return try {
            val response = apiService.crearComanda(dto)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun obtenirTaules(): List<Taula>? {
        return try {
            val response = apiService.obtenirTaules() // Ajusta a cómo se llame tu instancia de API
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun cobrarComanda(idComanda: Int): Boolean {
        return try {
            val response = RetrofitClient.instance.cobrarComanda(idComanda)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun afegirLinies(idComanda: Int, linies: List<CreateLiniaComandaDTO>): Result<ComandaDTO> {
        return try {
            val response = apiService.afegirLinies(idComanda, linies)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error en el servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarLiniaComanda(idLinia: Int): Boolean {
        return try {
            val response = apiService.eliminarLiniaComanda(idLinia)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("TaulaRepository", "Error en eliminar línia de comanda: ${e.message}")
            false
        }
    }

    suspend fun cambiarEstatComanda(idComanda: Int, nouEstat: String): Boolean {
        return try {
            val response = apiService.canviarEstatComanda(idComanda, nouEstat)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("TaulaRepository", "Error canviant estat comanda", e)
            false
        }
    }

    suspend fun crearMenjador(nouMenjador: PostMenjadorDTO): Menjador? {
        return try {
            val response = apiService.crearMenjador(nouMenjador)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun llistarMenjador(): List<Menjador>? {
        return try {
            val response = apiService.llistarMenjador()
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun actualitzarTaula(idTaula: Int, taula: PutTaulaDTO): Boolean {
        return try {
            val response = apiService.actualitzarTaula(idTaula, taula)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun crearTaula(novaTaula: PostTaulaDTO): Taula? {
        return try {
            val response = apiService.crearTaula(novaTaula)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}