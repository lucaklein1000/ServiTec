// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        UsuariRepository.kt
// Descripció:    Repositori encarregat de la gestió de les operacions d usuaris
//                i de l autenticació al sistema mitjançant el servei RESTful.
// ============================================================================

package com.example.servitec_frontend.repository

import android.content.Context
import com.example.servitec_frontend.data.model.CreateUsuariDTO
import com.example.servitec_frontend.data.model.LoginRequest
import com.example.servitec_frontend.data.model.UpdateUsuariDTO
import com.example.servitec_frontend.data.model.UsuariDTO
import com.example.servitec_frontend.data.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/** * Repositori encarregat de la comunicació amb l API REST per a la gestió d usuaris
 * i el procés d inici de sessió.
 *
 * @param context Context de l aplicació o activitat necessari per inicialitzar el RetrofitClient amb l interceptor JWT.
 */
class UsuariRepository(private val context: Context) {

    private val apiService = RetrofitClient.getApiService(context)

    /**
     * Inicia la sessió d un usuari enviant les credencials al backend.
     *
     * @param user Nom d usuari o credencial d accés.
     * @param pass Contrasenya de l usuari.
     * @param onResult Callback que retorna l objecte `UsuariDTO` amb el token en cas d èxit, o un missatge d error.
     */
    fun loginUser(user: String, pass: String, onResult: (UsuariDTO?, String?) -> Unit) {
        val loginData = LoginRequest(user, pass)

        apiService.login(loginData).enqueue(object : Callback<UsuariDTO> {
            override fun onResponse(call: Call<UsuariDTO>, response: Response<UsuariDTO>) {
                if (response.isSuccessful) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, "Error: Credencials invàlides")
                }
            }

            override fun onFailure(call: Call<UsuariDTO>, t: Throwable) {
                onResult(null, "Error de xarxa: ${t.message}")
            }
        })
    }

    /**
     * Obté el llistat complet d usuaris registrats al sistema.
     *
     * @return Llista de DTOs d usuaris (`List<UsuariDTO>`) o `null` si la petició falla.
     */
    suspend fun llistarUsuaris(): List<UsuariDTO>? {
        return try {
            val response = apiService.llistarUsuari()
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
     * Enregistra un nou usuari al sistema.
     *
     * @param nouUsuari DTO amb les dades de creació del nou usuari.
     * @return L objecte `UsuariDTO` creat o `null` si es produeix un error.
     */
    suspend fun crearUsuari(nouUsuari: CreateUsuariDTO): UsuariDTO? {
        return try {
            val response = apiService.crearUsuari(nouUsuari)
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
     * Elimina un usuari del sistema segons el seu identificador.
     *
     * @param idUsuari Identificador de l usuari a eliminar.
     * @return `true` si s ha eliminat correctament, `false` en cas contrari.
     */
    suspend fun eliminarUsuari(idUsuari: Int): Boolean {
        return try {
            val response = apiService.eliminarUsuari(idUsuari)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Actualitza la informació o permisos d un usuari existent.
     *
     * @param idUsuari Identificador de l usuari a modificar.
     * @param usuari DTO amb les noves dades de l usuari.
     * @return `true` si l actualització és correcta, `false` en cas contrari.
     */
    suspend fun actualitzarUsuari(idUsuari: Int, usuari: UpdateUsuariDTO): Boolean {
        return try {
            val response = apiService.actualitzarUsuari(idUsuari, usuari)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}