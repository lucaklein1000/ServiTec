// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        TaulaRepository.kt
// Descripció:    Repositori encarregat de la gestió de les operacions de taules,
//                comandes actives, menjadors, categories i productes associats.
// ============================================================================

package com.example.servitec_frontend.repository

import android.content.Context
import android.util.Log
import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.ComandaDTO
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.CreateLiniaComandaDTO
import com.example.servitec_frontend.data.model.Menjador
import com.example.servitec_frontend.data.model.PostCategoriaDTO
import com.example.servitec_frontend.data.model.PostMenjadorDTO
import com.example.servitec_frontend.data.model.PostTaulaDTO
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.PutCategoriaDTO
import com.example.servitec_frontend.data.model.PutTaulaDTO
import com.example.servitec_frontend.data.model.ResponseComnada
import com.example.servitec_frontend.data.model.ResponseCuina
import com.example.servitec_frontend.data.model.Taula
import com.example.servitec_frontend.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositori encarregat de la comunicació amb l API REST per a la gestió de taules,
 * comandes, menjadors i elements associats al panell principal.
 *
 * @param context Context de l aplicació o activitat necessari per inicialitzar el RetrofitClient amb l interceptor JWT.
 */
class TaulaRepository(private val context: Context) {

    private val apiService = RetrofitClient.getApiService(context)

    /**
     * Obté la comanda activa associada a una taula concreta.
     *
     * @param idMesa Identificador de la taula a consultar.
     * @return La comanda activa (`ResponseComnada`) o `null` si la taula està lliure o s produeix un error.
     */
    suspend fun obtenirComandaActiva(idMesa: Int): ResponseComnada? {
        return try {
            val response = apiService.obtenirComandaActiva(idMesa)
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

    /**
     * Obté el llistat complet de categories de productes.
     *
     * @return Llista d objectes `Categoria` o `null` si falla la petició.
     */
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

    /**
     * Crea una nova categoria de productes al sistema.
     *
     * @param nomCategoria DTO amb les dades de la nova categoria.
     * @return `true` si s ha creat correctament, `false` en cas contrari.
     */
    suspend fun crearCategoria(nomCategoria: PostCategoriaDTO): Boolean {
        return try {
            val response = apiService.crearCategoria(nomCategoria)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Actualitza el nom o la informació d una categoria existent.
     *
     * @param idCategoria Identificador de la categoria a modificar.
     * @param categoria DTO amb les noves dades de la categoria.
     * @return `true` si l actualització és correcta, `false` en cas contrari.
     */
    suspend fun actualitzarCategoria(idCategoria: Int, categoria: PutCategoriaDTO): Boolean {
        return try {
            val response = apiService.actualitzarCategoria(idCategoria, categoria)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obté la llista completa de productes disponibles al menú.
     *
     * @return Llista d objectes `Producte` o `null` si la petició falla.
     */
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

    /**
     * Envia una nova comanda inicial al backend.
     *
     * @param dto DTO amb la informació de la comanda a crear.
     * @return `true` si la comanda s ha creat correctament, `false` en cas contrari.
     */
    suspend fun enviarComanda(dto: CreateComandaDTO): Boolean {
        return try {
            val response = apiService.crearComanda(dto)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obté el llistat complet de taules del restaurant.
     *
     * @return Llista d objectes `Taula` o `null` si es produeix un error.
     */
    suspend fun obtenirTaules(): List<Taula>? {
        return try {
            val response = apiService.obtenirTaules()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Finalitza i cobra una comanda activa.
     *
     * @param idComanda Identificador de la comanda a cobrar.
     * @return `true` si el pagament s ha processat correctament, `false` en cas contrari.
     */
    suspend fun cobrarComanda(idComanda: Int): Boolean {
        return try {
            val response = apiService.cobrarComanda(idComanda)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Afagueix noves línies de comanda (productes) a una comanda ja existent.
     *
     * @param idComanda Identificador de la comanda objectiu.
     * @param linies Llista de DTOs de les noves línies a afegir.
     * @return Un objecte `Result` que conté el `ComandaDTO` actualitzat o una excepció en cas d error.
     */
    suspend fun afegirLinies(idComanda: Int, linies: List<CreateLiniaComandaDTO>): Result<ComandaDTO> {
        return try {
            val response = apiService.afegirLinies(idComanda, linies)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una línia de comanda concreta d una comanda en curs.
     *
     * @param idLinia Identificador de la línia de comanda a eliminar.
     * @return `true` si s ha eliminat correctament, `false` en cas contrari.
     */
    suspend fun eliminarLiniaComanda(idLinia: Int): Boolean {
        return try {
            val response = apiService.eliminarLiniaComanda(idLinia)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("TaulaRepository", "Error en enliminar la lina de comanda: ${e.message}")
            false
        }
    }

    /**
     * Canvia l estat d una comanda (per exemple: pendent, en preparació, servida).
     *
     * @param idComanda Identificador de la comanda a modificar.
     * @param nouEstat Text que representa el nou estat de la comanda.
     * @return `true` si l estat s ha actualitzat correctament, `false` en cas contrari.
     */
    suspend fun cambiarEstatComanda(idComanda: Int, nouEstat: String): Boolean {
        return try {
            val response = apiService.canviarEstatComanda(idComanda, nouEstat)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("TaulaRepository", "Error canviant estat comanda", e)
            false
        }
    }

    /**
     * Canvia l estat d una linia de comanda (Pendent, Servit, Eliminat).
     *
     * @param idLinia Identificador de la linia de comanda a modificar.
     * @param nouEstat Text que representa el nou estat de la comanda.
     * @return `true` si l estat s ha actualitzat correctament, `false` en cas contrari.
     */
    suspend fun canviarEstatLinia(idLinia: Int, nouEstat: String): Boolean {
        return try {
            val response = apiService.canviarEstatLinia(idLinia, nouEstat)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("TaulaRepository", "Error canviant estat linia comanda", e)
            false
        }
    }

    /**
     * Crea un nou menjador o sala al restaurant.
     *
     * @param nouMenjador DTO amb les dades del nou menjador.
     * @return L objecte `Menjador` creat o `null` si falla la petició.
     */
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

    /**
     * Obté la llista completa de menjadors registrats.
     *
     * @return Llista d objectes `Menjador` o `null` si s produeix un error.
     */
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

    /**
     * Actualitza la configuració o estat d una taula.
     *
     * @param idTaula Identificador de la taula a modificar.
     * @param taula DTO amb les noves dades de la taula.
     * @return `true` si l actualització és correcta, `false` en cas contrari.
     */
    suspend fun actualitzarTaula(idTaula: Int, taula: PutTaulaDTO): Boolean {
        return try {
            val response = apiService.actualitzarTaula(idTaula, taula)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Registra una nova taula dins d un menjador del restaurant.
     *
     * @param novaTaula DTO amb la informació de la nova taula.
     * @return L objecte `Taula` creat o `null` si s produeix un error.
     */
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

    /**
     * Obté la llista de comandes actives destinades a la pantalla de cuina.
     *
     * Envia una petició GET a la API utilitzant el servei autenticat. Les línies de comanda
     * retornades ja vénen filtrades pel backend segons l estat corresponent.
     *
     * @return Una llista mutable de [ResponseCuina] si la petició té èxit, o `null` en cas d'error o excepció.
     */
    /**
     * Obté la llista de comandes actives destinades a la pantalla de cuina.
     *
     * @return Llista mutable d objectes `ResponseCuina` o `null` si la petició falla.
     */
    suspend fun getComandesCuina(): MutableList<ResponseCuina>? {
        return try {
            val response = apiService.getComandesCuina()
            if (response.isSuccessful) {
                response.body()?.toMutableList()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}