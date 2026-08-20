// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        LoginResponseDTO.cs
// Descripció:    DTO utilitzat per retornar la informació d'autenticació
//                d'un usuari, incloent el token JWT de sessió i el seu rol.
// ============================================================================

namespace ServiTec.Application.DTOs
{
    /// <summary>
    /// Objecte de transferència de dades que conté la resposta de l'endpoint d'autenticació.
    /// </summary>
    public class LoginResponseDTO
    {
        public string Token { get; set; } = string.Empty;
        public string NomUsuari { get; set; } = string.Empty;
        public string Rol { get; set; } = string.Empty;
    }
}