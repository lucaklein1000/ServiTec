// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        LoginRequest.cs
// Descripció:    DTO per a la recepció de les credencials d'accés (usuari i
//                contrasenya) enviades des del client en el procés de login.
// ============================================================================

namespace ServiTec.Application.DTOs
{
    /// <summary>
    /// Objecte de transferència de dades que conté les credencials d'entrada per a l'autenticació.
    /// </summary>
    public class LoginRequest
    {
        public string NomUsuari { get; set; } = string.Empty;
        public string Contrasenya { get; set; } = string.Empty;
    }
}