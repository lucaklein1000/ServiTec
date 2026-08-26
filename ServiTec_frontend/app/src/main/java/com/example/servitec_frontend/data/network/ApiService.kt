// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ApiService.kt
// Descripció:    Interfície REST de Retrofit amb la definició de tots els
//                endpoints del backend (Usuaris, Categories, Productes,
//                Taules, Comandes i Menjadors).
// ============================================================================

package com.example.servitec_frontend.data.network

import com.example.servitec_frontend.data.model.BloqueigRequestDTO
import com.example.servitec_frontend.data.model.Categoria
import com.example.servitec_frontend.data.model.ComandaDTO
import com.example.servitec_frontend.data.model.CreateCategoriaDTO
import com.example.servitec_frontend.data.model.CreateComandaDTO
import com.example.servitec_frontend.data.model.CreateLiniaComandaDTO
import com.example.servitec_frontend.data.model.CreateMenjadorDTO
import com.example.servitec_frontend.data.model.CreateProdcuteDTO
import com.example.servitec_frontend.data.model.CreateTaulaDTO
import com.example.servitec_frontend.data.model.CreateUsuariDTO
import com.example.servitec_frontend.data.model.Cuina
import com.example.servitec_frontend.data.model.LoginRequest
import com.example.servitec_frontend.data.model.LoginResponse
import com.example.servitec_frontend.data.model.Menjador
import com.example.servitec_frontend.data.model.Producte
import com.example.servitec_frontend.data.model.ProducteDTO
import com.example.servitec_frontend.data.model.TaulaDTO
import com.example.servitec_frontend.data.model.UpdateCategoriaDTO
import com.example.servitec_frontend.data.model.UpdateMenjadorDTO
import com.example.servitec_frontend.data.model.UpdateProdcuteDTO
import com.example.servitec_frontend.data.model.UpdateTaulaDTO
import com.example.servitec_frontend.data.model.UpdateUsuariDTO
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

/**
 * Interfície que defineix les peticions HTTP de la capa de xarxa mitjançant Retrofit.
 * Inclou mètodes asíncrons (coroutines) per a operacions CRUD i gestió en temps real.
 */
interface ApiService {

 // ========================================================================
 // USUARIS I AUTENTICACIÓ
 // ========================================================================

 /** Obté el llistat complet d'usuaris registrats. */
 @GET("api/Usuari/llistar")
 suspend fun llistarUsuari(): Response<List<UsuariDTO>>

 /** Crea un nou usuari al sistema. */
 @POST("api/Usuari/crear")
 suspend fun crearUsuari(@Body dto: CreateUsuariDTO): Response<UsuariDTO>

 /** Inicia la sessió d'un usuari i retorna les credencials JWT. */
 @POST("api/Auth/login")
 fun login(@Body request: LoginRequest): Call<LoginResponse>

 /** Elimina un usuari del sistema segons el seu identificador. */
 @DELETE("api/Usuari/eliminar/{id}")
 suspend fun eliminarUsuari(@Path("id") idUsuari: Int): Response<ResponseBody>

 /** Actualitza les dades d'un usuari existent. */
 @PUT("api/Usuari/actualitzar/{id}")
 suspend fun actualitzarUsuari(@Path("id") idUsuariDTO: Int, @Body dto: UpdateUsuariDTO): Response<UsuariDTO>

 // ========================================================================
 // CATEGORIES
 // ========================================================================

 /** Obté el llistat de categories de productes disponibles. */
 @GET("api/Categoria/llistar")
 suspend fun getCategories(): Response<List<Categoria>>

 /** Enregistra una nova categoria al sistema. */
 @POST("api/Categoria/crear")
 suspend fun crearCategoria(@Body dto: CreateCategoriaDTO): Response<Categoria>

 /** Modifica el nom o la configuració d'una categoria. */
 @PUT("api/Categoria/actualitzar/{id}")
 suspend fun actualitzarCategoria(@Path("id") idCategoria: Int, @Body dto: UpdateCategoriaDTO): Response<Categoria>

 // ========================================================================
 // PRODUCTES
 // ========================================================================

 /** Crea un nou producte al menú. */
 @POST("api/Producte/crear")
 suspend fun crearProducte(@Body dto: CreateProdcuteDTO): Response<Producte>

 /** Esborra un producte existent del catàleg. */
 @DELETE("api/Producte/eliminar/{id}")
 suspend fun eliminarProducte(@Path("id") idProducte: Int): Response<ResponseBody>

 /** Obté el llistat de productes registrats. */
 @GET("api/Producte/llistar")
 suspend fun obtenirProductes(): Response<List<ProducteDTO>>

 /** Actualitza la informació o el preu d'un producte. */
 @PUT("api/Producte/actualitzar/{id}")
 suspend fun actualitzarProducte(@Path("id") idProducte: Int, @Body dto: UpdateProdcuteDTO): Response<ProducteDTO>

 // ========================================================================
 // TAULES I SALA
 // ========================================================================

 /** Obté l'estat i el llistat complet de taules. */
 @GET("api/Taula/llistar")
 suspend fun obtenirTaules(): Response<List<TaulaDTO>>

 /** Registra una nova taula dins d'un menjador. */
 @POST("api/Taula/crear")
 suspend fun crearTaula(@Body dto: CreateTaulaDTO): Response<TaulaDTO>

 /** Actualitza la configuració d'una taula. */
 @PUT("api/Taula/actualitzar/{id}")
 suspend fun actualitzarTaula(@Path("id") idTaula: Int, @Body dto: UpdateTaulaDTO): Response<TaulaDTO>

 /** Elimina una taula de la base de dades. */
 @DELETE("api/Taula/borrar/{id}")
 suspend fun eliminarTaula(@Path("id") idTaula: Int): Response<ResponseBody>

 /** Bloqueja una taula a nom d'un cambrer concret. */
 @POST("api/Taula/{id}/bloquejar")
 suspend fun bloquejarTaula(@Path("id") idTaula: Int, @Body request: BloqueigRequestDTO): Response<Unit>

 /** Allibera el bloqueig d'una taula. */
 @POST("api/Taula/{id}/desbloquejar")
 suspend fun desbloquejarTaula(@Path("id") idTaula: Int): Response<Unit>

 // ========================================================================
 // COMANDES I CUINA
 // ========================================================================

 /** Envia una nova comanda al backend. */
 @POST("api/Comanda/crear")
 suspend fun crearComanda(@Body dto: CreateComandaDTO): Response<ResponseBody>

 /** Consulta la comanda activa d'una taula concreta. */
 @GET("api/Comanda/activa/{id}")
 suspend fun obtenirComandaActiva(@Path("id") idMesa: Int): Response<ComandaDTO>

 /** Obté el llistat de comandes pendents per a la pantalla de cuina. */
 @GET("api/Comanda/cuina")
 suspend fun getComandesCuina(): Response<MutableList<Cuina>>

 /** Canvia l'estat global d'una comanda. */
 @PUT("api/Comanda/{id}/estat")
 suspend fun canviarEstatComanda(@Path("id") idComanda: Int, @Body nouEstat: String): Response<ResponseBody>

 /** Canvia l'estat d'una línia de comanda individual. */
 @PUT("api/Comanda/linia/{idLinia}/estat")
 suspend fun canviarEstatLinia(@Path("idLinia") idLinia: Int, @Body nouEstat: String): Response<ResponseBody>

 /** Marca una comanda com a cobrada i finalitzada. */
 @PUT("api/Comanda/{idComanda}/cobrar")
 suspend fun cobrarComanda(@Path("idComanda") idComanda: Int): Response<ResponseBody>

 /** Afegeix noves línies de comanda a un comanda en curs. */
 @POST("api/Comanda/{id}/linies")
 suspend fun afegirLinies(@Path("id") idComanda: Int, @Body linies: List<CreateLiniaComandaDTO>): Response<ComandaDTO>

 /** Elimina o marqui com a esborrada una línia de comanda. */
 @PUT("api/Comanda/linia/{idLinia}/eliminar")
 suspend fun eliminarLiniaComanda(@Path("idLinia") idLinia: Int): Response<ResponseBody>

 // ========================================================================
 // MENJADORS
 // ========================================================================

 /** Obté la llista de tots els menjadors o sales del restaurant. */
 @GET("api/Menjador/llistar")
 suspend fun llistarMenjador(): Response<List<Menjador>>

 /** Crea un nou menjador al sistema. */
 @POST("api/Menjador/crear")
 suspend fun crearMenjador(@Body dto: CreateMenjadorDTO): Response<Menjador>

 /** Actualitza la informació d'un menjador. */
 @PUT("api/Menjador/actualitzar/{id}")
 suspend fun actualitzarMenjador(@Path("id") idMenjador: Int, @Body dto: UpdateMenjadorDTO): Response<Menjador>
}