/**
 * @file user.model.ts
 * @description Defineix l'estructura de dades d'un usuari del sistema.
 *              Aquesta interfície actua com a contracte entre el frontend i la API,
 *              garantint que les dades rebudes del backend tenen el format esperat.
 *              Els camps coincideixen exactament amb els retornats per l'endpoint
 *              /api/Usuari/llistar del backend ASP.NET Core.
 * @author ServiTec
 */

// Interfície que defineix l'estructura d'un usuari tal com el retorna la API
export interface User {
  // Identificador únic de l'usuari a la base de dades
  idUsuari: number;
  // Nom de l'usuari
  nomUsuari: string;
  // Contrasenya de l'usuari
  contrasenya: string;
  // Indica si l'usuari està actiu o no
  actiu: boolean;
  // Indica si l'usuari té permisos d'administrador
  admin: boolean;
}