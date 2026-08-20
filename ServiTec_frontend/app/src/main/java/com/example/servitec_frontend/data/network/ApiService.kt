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
import com.example.servitec_frontend.data.model.CrearUsuariDTO
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.CreateLiniaComandaDTO
import com.example.servitec_frontend.data.model.LoginRequest
import com.example.servitec_frontend.data.model.Menjador
import com.example.servitec_frontend.data.model.PostCategoriaDTO
import com.example.servitec_frontend.data.model.PostMenjadorDTO
import com.example.servitec_frontend.data.model.PostProducteDTO
import com.example.servitec_frontend.data.model.PostTaulaDTO
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.ProducteDTO
import com.example.servitec_frontend.data.model.PutCategoriaDTO
import com.example.servitec_frontend.data.model.PutProducteDTO
import com.example.servitec_frontend.data.model.PutTaulaDTO
import com.example.servitec_frontend.data.model.PutUsuariDTO
import com.example.servitec_frontend.data.model.ResponseComnada
import com.example.servitec_frontend.data.model.ResponseCuina
import com.example.servitec_frontend.data.model.Taula
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
 suspend fun crearUsuari(@Body dto: CrearUsuariDTO): Response<UsuariDTO>

 @POST("api/Auth/login")
 fun login(@Body request: LoginRequest): Call<UsuariDTO>

 @DELETE("api/Usuari/eliminar/{id}")
 suspend fun eliminarUsuari(@Path("id") idUsuari: Int): Response<ResponseBody>

 @PUT("api/Usuari/actualitzar/{id}")
 suspend fun actualitzarUsuari(@Path("id") idUsuariDTO: Int, @Body dto: PutUsuariDTO): Response<UsuariDTO>

 // CATEGORIES
 @GET("api/Categoria/llistar")
 suspend fun getCategories(): Response<List<Categoria>>

 @POST("api/Categoria/crear")
 suspend fun crearCategoria(@Body dto: PostCategoriaDTO): Response<Categoria>

 @PUT("api/Categoria/actualitzar/{id}")
 suspend fun actualitzarCategoria(@Path("id") idCategoria: Int, @Body dto: PutCategoriaDTO): Response<Categoria>

 // PRODUCTES
 @POST("api/Producte/crear")
 suspend fun crearProducte(@Body dto: PostProducteDTO): Response<Producte>

 @DELETE("api/Producte/eliminar/{id}")
 suspend fun eliminarProducte(@Path("id") idProducte: Int): Response<ResponseBody>

 @GET("api/Producte/llistar")
 suspend fun obtenirProductes(): Response<List<ProducteDTO>>

 @GET("api/Producte/llistar")
 suspend fun getProducts(): Response<List<Producte>>

 @PUT("api/Producte/actualitzar/{id}")
 suspend fun actualitzarProducte(@Path("id") idProducte: Int, @Body dto: PutProducteDTO): Response<ProducteDTO>

 // TAULES
 @GET("api/Taula/llistar")
 suspend fun obtenirTaules(): Response<List<Taula>>

 @POST("api/Taula/crear")
 suspend fun crearTaula(@Body dto: PostTaulaDTO): Response<Taula>

 @PUT("api/Taula/actualitzar/{id}") // 🛑 CORREGIDO: falta de barra inclinada y minúsculas
 suspend fun actualitzarTaula(@Path("id") idTaula: Int, @Body dto: PutTaulaDTO): Response<Taula>

 // COMANDES
 @POST("api/Comanda/crear")
 suspend fun crearComanda(@Body dto: CreateComandaDTO): Response<ResponseBody>

 @GET("api/Comanda/activa/{id}")
 suspend fun obtenirComandaActiva(@Path("id") idMesa: Int): Response<ResponseComnada>

 @GET("api/Comanda/cuina")
 suspend fun getComandesCuina(): Response<MutableList<ResponseCuina>>

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
 suspend fun crearMenjador(@Body dto: PostMenjadorDTO): Response<Menjador>
}