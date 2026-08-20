// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        IJwtService.cs
// Descripció:    Interfície del servei encarregat de la generació i gestió
//                de tokens d'autenticació JWT (JSON Web Tokens).
// ============================================================================

using ServiTec.Domain.Models;

namespace ServiTec.Application.Interfaces
{
    /// <summary>
    /// Contracte per al servei encarregat de la gestió i generació de tokens JWT.
    /// </summary>
    public interface IJwtService
    {
        /// <summary>
        /// Genera un token JWT signat amb les informacions (claims) de l'usuari passat per paràmetre.
        /// </summary>
        /// <param name="usuari">Instància de l'usuari autenticat.</param>
        /// <returns>Cadena de text amb el token JWT generat.</returns>
        string GenerarToken(Usuari usuari);
    }
}