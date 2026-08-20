// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        AuthService.cs
// Descripció:    Servei d'autenticació encarregat de coordinar la validació
//                de les credencials dels usuaris i delegar la generació del
//                token JWT corresponent.
// ============================================================================

using ServiTec.Application.DTOs;
using ServiTec.Application.Interfaces;

namespace ServiTec.Application.Services
{
    /// <summary>
    /// Servei de la capa d'aplicació encarregat del procés de login i autenticació d'usuaris.
    /// </summary>
    public class AuthService
    {
        private readonly UsuariService _usuariService;
        private readonly IJwtService _jwtService;

        public AuthService(UsuariService usuariService, IJwtService jwtService)
        {
            _usuariService = usuariService;
            _jwtService = jwtService;
        }

        /// <summary>
        /// Valida les credencials d'un usuari i, si és actiu i vàlid, genera un token JWT d'accés.
        /// </summary>
        /// <param name="dto">DTO amb les credencials d'accés (nom d'usuari i contrasenya).</param>
        /// <returns>DTO amb el token generat i les dades bàsiques de l'usuari, o null si l'autenticació falla.</returns>
        public async Task<LoginResponseDTO?> AutenticarAsync(LoginRequest dto)
        {
            var usuari = await _usuariService.ValidarLogin(dto.NomUsuari, dto.Contrasenya);
            if (usuari == null || !usuari.Actiu)
            {
                return null;
            }

            var token = _jwtService.GenerarToken(usuari);

            return new LoginResponseDTO
            {
                Token = token,
                NomUsuari = usuari.NomUsuari,
                Rol = usuari.Rol
            };
        }
    }
}