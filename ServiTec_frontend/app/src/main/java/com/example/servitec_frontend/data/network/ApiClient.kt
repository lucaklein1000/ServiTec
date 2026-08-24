// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ApiClient.kt
// Descripció:    Singleton encarregat de la configuració de Retrofit i OkHttp.
//                Injecta l AuthInterceptor per gestionar l autenticació JWT.
// ============================================================================

package com.example.servitec_frontend.data.network

import android.content.Context
import com.example.servitec_frontend.data.network.ApiClient.apiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // Cambia a true cuando estés desarrollando en local, o false para usar Azure
    private const val IS_LOCAL_DEVELOPMENT = false

    private const val LOCAL_URL = "http://10.0.2.2:5206/" // O la teva IP local http://10.45.94.221:5206/
    private const val AZURE_URL = "https://servitec-api-hwanepfxehgpatag.spaincentral-01.azurewebsites.net/"

    private val BASE_URL = if (IS_LOCAL_DEVELOPMENT) {
        LOCAL_URL
    } else {
        AZURE_URL
    }
    private var apiService: ApiService? = null

    /**
     * Inicialitza i retorna la instància singleton d ApiService.
     * Requerix el [context] per poder llegir les SharedPreferences des de l AuthInterceptor.
     */
    fun getApiService(context: Context): ApiService {
        if (apiService == null) {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context.applicationContext))
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService!!
    }
}