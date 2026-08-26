// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        RetrofitClient.kt
// Descripció:    Factory Singleton encarregada de la instantiació de Retrofit
//                i de la configuració del client OkHttpClient amb suport per a
//                l'interceptor d'autenticació JWT i bypass de certificats SSL.
// ============================================================================

package com.example.servitec_frontend.data.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Singleton per a la creació i gestió de la instància del servei Retrofit.
 * Inclou la configuració d'un client OkHttp personalitzat per ometre la validació
 * de certificats SSL (útil en entorns de desenvolupament/proves) i l'addició
 * de la capçalera JWT via [AuthInterceptor].
 */
object RetrofitClient {

    // Flag per alternar entre l'entorn de desenvolupament local i l'API en producció (Azure)
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
     * Configura un [OkHttpClient] permissiu que ignora les validacions de certificats SSL i del nom de host.
     * Injecta l'[AuthInterceptor] per afegir el token d'autenticació a les capçaleres de les peticions.
     *
     * @param context Context necessari per llegir les SharedPreferences des de l'interceptor.
     * @return Instància de [OkHttpClient] configurada.
     */
    private fun getUnsafeOkHttpClient(context: Context): OkHttpClient {
        return try {
            // TrustManager que ignora totes les verificacions de la cadena de certificats SSL
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(AuthInterceptor(context.applicationContext))
                .build()
        } catch (e: Exception) {
            throw RuntimeException("Error en inicialitzar el client OkHttpClient unsafe", e)
        }
    }

    /**
     * Retorna la instància singleton d'[ApiService]. Si no està creada, la inicialitza de manera segura per a fils (thread-safe).
     *
     * @param context Context d'Android requerit per a l'interceptor JWT.
     * @return Instància d'[ApiService] a punt per realitzar peticions REST.
     */
    fun getApiService(context: Context): ApiService {
        return apiService ?: synchronized(this) {
            apiService ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getUnsafeOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
                .also { apiService = it }
        }
    }
}