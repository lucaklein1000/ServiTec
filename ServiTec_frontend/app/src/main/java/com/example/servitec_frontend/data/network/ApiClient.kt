// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ApiClient.kt
// Descripció:    Singleton encarregat de la configuració de Retrofit i OkHttp.
//                Injecta l'AuthInterceptor per gestionar l'autenticació JWT
//                i alternar entre l'entorn de desenvolupament local i Azure.
// ============================================================================

package com.example.servitec_frontend.data.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Patron Singleton per centralitzar la construcció i configuració del client Retrofit.
 * S'assegura que només s'instanciï una única llista de serveis API (`ApiService`)
 * utilitzant un client OkHttpClient configurat amb l'interceptor d'autenticació JWT.
 */
object ApiClient {

    // Canvia a true per al desenvolupament en local, o false per connectar a l'API desplegada a Azure
    private const val IS_LOCAL_DEVELOPMENT = false

    private const val LOCAL_URL = "http://10.0.2.2:5206/" // IP per defecte de l'emulador d'Android Studio
    private const val AZURE_URL = "https://servitec-api-hwanepfxehgpatag.spaincentral-01.azurewebsites.net/"

    private val BASE_URL = if (IS_LOCAL_DEVELOPMENT) {
        LOCAL_URL
    } else {
        AZURE_URL
    }

    @Volatile
    private var apiService: ApiService? = null

    /**
     * Inicialitza i retorna la instància singleton d'ApiService.
     * Requireix el [context] de l'aplicació per poder llegir el token des de les SharedPreferences via AuthInterceptor.
     *
     * @param context Context d'Android necessari per accés a SharedPreferences.
     * @return La instància única d'[ApiService].
     */
    fun getApiService(context: Context): ApiService {
        return apiService ?: synchronized(this) {
            apiService ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(AuthInterceptor(context.applicationContext))
                        .build()
                )
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
                .also { apiService = it }
        }
    }
}