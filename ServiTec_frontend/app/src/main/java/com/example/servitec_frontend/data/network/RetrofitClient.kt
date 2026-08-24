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

object RetrofitClient {
    // Cambia a true cuando estés desarrollando en local, o false para usar Azure
    private const val IS_LOCAL_DEVELOPMENT = true

    private const val LOCAL_URL = "http://10.0.2.2:5206/" // O la teva IP local http://10.45.94.221:5206/
    private const val AZURE_URL = "https://servitec-api-hwanepfxehgpatag.spaincentral-01.azurewebsites.net/"

    private val BASE_URL = if (IS_LOCAL_DEVELOPMENT) {
        LOCAL_URL
    } else {
        AZURE_URL
    }
    private var apiService: ApiService? = null

    /**
     * Crea un OkHttpClient que ignora las validaciones SSL de desarrollo.
     */
    private fun getUnsafeOkHttpClient(context: Context): OkHttpClient {
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
                .addInterceptor(AuthInterceptor(context)) // Tu interceptor del token JWT
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getApiService(context: Context): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient(context))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}