/**
 * @file environment.ts
 * @description Variables d'entorn per al mode de desenvolupament.
 *              Centralitza la configuració que pot canviar entre entorns
 *              (desenvolupament, producció). Per canviar la URL de la API
 *              només cal modificar aquest fitxer.
 * 
 * @author ServiTec
 */

export const environment = {
  // Indica que estem en mode desenvolupament
  production: false,
  // URL base de la API de backend ASP.NET Core
  apiUrl: 'https://localhost:44322'
};