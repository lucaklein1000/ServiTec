// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        AuthInterceptor.kt
// Descripció:    Interceptor d'OkHttp encarregat d'afegir la capçalera d'autorització
//                Bearer amb el token JWT a totes les peticions sortints.
// ============================================================================

package com.example.servitec_frontend.data.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor d'OkHttp que automatitza la inclusió del token d'autenticació JWT.
 * S'encarrega d'obtenir el token des de les SharedPreferences i d'injectar-lo
 * a la capçalera "Authorization" en totes les peticions HTTP sortints.
 *
 * @param context Context de l'aplicació utilitzat per accedir a SharedPreferences.
 */
class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Recuperació del token JWT des de les preferències compartides de l'aplicació
        val sharedPreferences = context.getSharedPreferences("ServiTecPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        val requestBuilder = chain.request().newBuilder()

        // Injecta el token JWT com a capçalera Bearer si existeix una sessió activa
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}