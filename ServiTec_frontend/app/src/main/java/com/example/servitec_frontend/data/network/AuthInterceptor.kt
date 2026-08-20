// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        AuthInterceptor.kt
// Descripció:    Interceptor d OkHttp encarregat d afegir la capçalera d autorització
//                Bearer amb el token JWT a totes les peticions sortints.
// ============================================================================

package com.example.servitec_frontend.data.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = context.getSharedPreferences("ServiTecPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}