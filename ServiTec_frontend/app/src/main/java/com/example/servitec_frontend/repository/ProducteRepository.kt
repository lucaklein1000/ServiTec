// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ProducteRepository.kt
// Descripció:    Repositori encarregat de gestionar les operacions CRUD de
//                productes mitjançant el servei RESTful de Retrofit.
// ============================================================================

package com.example.servitec_frontend.repository

import android.content.Context
import com.example.servitec_frontend.data.model.PostProducteDTO
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.ProducteDTO
import com.example.servitec_frontend.data.model.PutProducteDTO
import com.example.servitec_frontend.data.network.RetrofitClient

/**
 * Repositori encarregat de la comunicació amb l API REST per a la gestió de productes.
 *
 * @param context Context de l aplicació o activitat necessari per inicialitzar el RetrofitClient amb l interceptor JWT.
 */
class ProducteRepository(private val context: Context) {

    private val apiService = RetrofitClient.getApiService(context)

    /**
     * Crea un nou producte al sistema.
     *
     * @param nouProducte DTO amb la informació del producte a crear.
     * @return El producte creat o `null` si s produeix un error.
     */
    suspend fun crearProducte(nouProducte: PostProducteDTO): Producte? {
        return try {
            val response = apiService.crearProducte(nouProducte)
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
     * Obté el llistat complet de productes del sistema.
     *
     * @return Llista de DTOs de productes o `null` si la petició falla.
     */
    suspend fun llistarProductes(): List<ProducteDTO>? {
        return try {
            val response = apiService.obtenirProductes()
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
     * Elimina un producte del sistema a partir del seu identificador.
     *
     * @param idProducte Identificador del producte a eliminar.
     * @return `true` si s ha eliminat correctament, `false` en cas contrari.
     */
    suspend fun eliminarProducte(idProducte: Int): Boolean {
        return try {
            val response = apiService.eliminarProducte(idProducte)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Actualitza la informació d un producte existent.
     *
     * @param idProducte Identificador del producte a actualitzar.
     * @param producte DTO amb les noves dades del producte.
     * @return `true` si s ha actualitzat correctament, `false` en cas contrari.
     */
    suspend fun actualitzarProducte(idProducte: Int, producte: PutProducteDTO): Boolean {
        return try {
            val response = apiService.actualitzarProducte(idProducte, producte)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}