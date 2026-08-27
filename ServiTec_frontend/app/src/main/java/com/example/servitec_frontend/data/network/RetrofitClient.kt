// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        RetrofitClient.kt
// Descripció:    Factory Singleton encarregada de la instantiació de Retrofit
//                i de la configuració del client OkHttpClient amb suport per a
//                l'interceptor d'autenticació JWT.
// ============================================================================

package com.example.servitec_frontend.data.network

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Singleton per a la creació i gestió de la instància del servei Retrofit.
 * Inclou la configuració d'OkHttp amb suport per a [AuthInterceptor] (JWT)
 * i gestió d'entorns de desenvolupament local / producció.
 */
object RetrofitClient {

    // Flag per alternar entre l'entorn de desenvolupament local i l'API en producció (Azure)
    private const val IS_LOCAL_DEVELOPMENT = false

    private const val LOCAL_URL = "http://10.0.2.2:5206/"
    private const val AZURE_URL = "https://servitec-api-hwanepfxehgpatag.spaincentral-01.azurewebsites.net/"

    private val BASE_URL = if (IS_LOCAL_DEVELOPMENT) {
        LOCAL_URL
    } else {
        AZURE_URL
    }

    @Volatile
    private var apiService: ApiService? = null

    /**
     * Configura l'OkHttpClient segons l'entorn. En local o si és necessari utilitza
     * el bypass SSL, i en producció aplica un client OkHttp estàndard amb l'AuthInterceptor.
     */
    private fun getOkHttpClient(context: Context): OkHttpClient {
        val builder = if (IS_LOCAL_DEVELOPMENT) {
            getUnsafeBuilder()
        } else {
            OkHttpClient.Builder()
        }

        return builder
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(context.applicationContext))
            .build()
    }

    /**
     * TrustManager permissiu per a entorns de desenvolupament local amb certificats autofirmats.
     */
    private fun getUnsafeBuilder(): OkHttpClient.Builder {
        return try {
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
        } catch (e: Exception) {
            throw RuntimeException("Error en inicialitzar el client OkHttpClient unsafe", e)
        }
    }

    /**
     * Retorna la instància singleton d'[ApiService]. Si no està creada, la inicialitza de manera segura per a fils.
     *
     * @param context Context d'Android requerit per a l'interceptor JWT.
     * @return Instància d'[ApiService] a punt per realitzar peticions REST.
     */
    fun getApiService(context: Context): ApiService {
        return apiService ?: synchronized(this) {
            apiService ?: Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient(context))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
                .also { apiService = it }
        }
    }
}