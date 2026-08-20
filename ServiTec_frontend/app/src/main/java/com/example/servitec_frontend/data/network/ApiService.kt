// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ApiService.kt
// Descripció:    Interfície REST de Retrofit amb tots els endpoints del backend.
// ============================================================================

package com.example.servitec_frontend.data.network

import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.ComandaDTO
import com.example.servitec_frontend.data.model.CreateUsuariDTO
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.CreateLiniaComandaDTO
import com.example.servitec_frontend.data.model.LoginRequest
import com.example.servitec_frontend.data.model.Menjador
import com.example.servitec_frontend.data.model.CreateCategoriaDTO
import com.example.servitec_frontend.data.model.CreateMenjadorDTO
import com.example.servitec_frontend.data.model.CreateProdcuteDTO
import com.example.servitec_frontend.data.model.CreateTaulaDTO
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.ProducteDTO
import com.example.servitec_frontend.data.model.UpdateCategoriaDTO
import com.example.servitec_frontend.data.model.UpdateProdcuteDTO
import com.example.servitec_frontend.data.model.UpdateTaulaDTO
import com.example.servitec_frontend.data.model.UpdateUsuariDTO
import com.example.servitec_frontend.data.model.Cuina
import com.example.servitec_frontend.data.model.TaulaDTO
import com.example.servitec_frontend.data.model.UsuariDTO
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

 // USUARIS I AUTENTICACIÓ
 @GET("api/Usuari/llistar")
 suspend fun llistarUsuari(): Response<List<UsuariDTO>>

 @POST("api/Usuari/crear")
 suspend fun crearUsuari(@Body dto: CreateUsuariDTO): Response<UsuariDTO>

 @POST("api/Auth/login")
 fun login(@Body request: LoginRequest): Call<UsuariDTO>

 @DELETE("api/Usuari/eliminar/{id}")
 suspend fun eliminarUsuari(@Path("id") idUsuari: Int): Response<ResponseBody>

 @PUT("api/Usuari/actualitzar/{id}")
 suspend fun actualitzarUsuari(@Path("id") idUsuariDTO: Int, @Body dto: UpdateUsuariDTO): Response<UsuariDTO>

 // CATEGORIES
 @GET("api/Categoria/llistar")
 suspend fun getCategories(): Response<List<Categoria>>

 @POST("api/Categoria/crear")
 suspend fun crearCategoria(@Body dto: CreateCategoriaDTO): Response<Categoria>

 @PUT("api/Categoria/actualitzar/{id}")
 suspend fun actualitzarCategoria(@Path("id") idCategoria: Int, @Body dto: UpdateCategoriaDTO): Response<Categoria>

 // PRODUCTES
 @POST("api/Producte/crear")
 suspend fun crearProducte(@Body dto: CreateProdcuteDTO): Response<Producte>

 @DELETE("api/Producte/eliminar/{id}")
 suspend fun eliminarProducte(@Path("id") idProducte: Int): Response<ResponseBody>

 @GET("api/Producte/llistar")
 suspend fun obtenirProductes(): Response<List<ProducteDTO>>

 @GET("api/Producte/llistar")
 suspend fun getProducts(): Response<List<Producte>>

 @PUT("api/Producte/actualitzar/{id}")
 suspend fun actualitzarProducte(@Path("id") idProducte: Int, @Body dto: UpdateProdcuteDTO): Response<ProducteDTO>

 // TAULES
 @GET("api/Taula/llistar")
 suspend fun obtenirTaules(): Response<List<TaulaDTO>>

 @POST("api/Taula/crear")
 suspend fun crearTaula(@Body dto: CreateTaulaDTO): Response<TaulaDTO>

 @PUT("api/Taula/actualitzar/{id}") // 🛑 CORREGIDO: falta de barra inclinada y minúsculas
 suspend fun actualitzarTaula(@Path("id") idTaula: Int, @Body dto: UpdateTaulaDTO): Response<TaulaDTO>

 // COMANDES
 @POST("api/Comanda/crear")
 suspend fun crearComanda(@Body dto: CreateComandaDTO): Response<ResponseBody>

 @GET("api/Comanda/activa/{id}")
 suspend fun obtenirComandaActiva(@Path("id") idMesa: Int): Response<ComandaDTO>

 @GET("api/Comanda/cuina")
 suspend fun getComandesCuina(): Response<MutableList<Cuina>>

 @PUT("api/Comanda/{id}/estat")
 suspend fun canviarEstatComanda(@Path("id") idComanda: Int, @Body nouEstat: String): Response<ResponseBody>

 @PUT("api/Comanda/linia/{idLinia}/estat")
 suspend fun canviarEstatLinia(@Path("idLinia") idLinia: Int, @Body nouEstat: String): Response<ResponseBody>

 @PUT("api/Comanda/{idComanda}/cobrar")
 suspend fun cobrarComanda(@Path("idComanda") idComanda: Int): Response<ResponseBody>

 @POST("api/Comanda/{id}/linies")
 suspend fun afegirLinies(@Path("id") idComanda: Int, @Body linies: List<CreateLiniaComandaDTO>): Response<ComandaDTO>

 @PUT("api/Comanda/linia/{idLinia}/eliminar")
 suspend fun eliminarLiniaComanda(@Path("idLinia") idLinia: Int): Response<ResponseBody>

 // MENJADORS
 @GET("api/Menjador/llistar")
 suspend fun llistarMenjador(): Response<List<Menjador>>

 @POST("api/Menjador/crear")
 suspend fun crearMenjador(@Body dto: CreateMenjadorDTO): Response<Menjador>
}