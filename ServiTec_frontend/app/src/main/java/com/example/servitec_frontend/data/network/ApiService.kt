package com.example.servitec_frontend.data.network

import com.example.servitec_frontend.data.model.LoginRequest
import com.example.servitec_frontend.data.model.Usuari
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/Usuari/llistar")
    fun getUsuarios(): Call<List<Usuari>>

    @POST("api/Usuari/login")
    fun login(@Body request: LoginRequest): Call<Usuari>
}