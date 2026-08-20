// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        JwtService.cs
// Descripció:    Servei encarregat de la generació de tokens JSON Web Token (JWT)
//                per a l'autenticació i autorització basada en rols d'usuaris.
// ============================================================================

using Microsoft.IdentityModel.Tokens;
using ServiTec.Application.Interfaces;
using ServiTec.Domain.Models;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace ServiTec.Application.Services
{
    /// <summary>
    /// Servei encarregat de la creació i signatura de tokens JWT per al procés de login.
    /// </summary>
    public class JwtService : IJwtService
    {
        private readonly IConfiguration _configuration;

        public JwtService(IConfiguration configuration)
        {
            _configuration = configuration;
        }

        /// <summary>
        /// Genera un token JWT vàlid signat amb la clau secreta de la configuració i hi inclou els claims de l'usuari.
        /// </summary>
        /// <param name="usuari">Instància de l'usuari autenticat.</param>
        /// <returns>Cadena de text amb el token JWT xifrat i codificat en format Base64.</returns>
        public string GenerarToken(Usuari usuari)
        {
            var jwtSettings = _configuration.GetSection("Jwt");
            var secretKey = jwtSettings["Key"]!;
            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secretKey));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            // Definim les dades d'identitat (claims) que viatjaran xifrades dins del token
            var claims = new[]
            {
                new Claim(ClaimTypes.NameIdentifier, usuari.IdUsuari.ToString()),
                new Claim(ClaimTypes.Name, usuari.NomUsuari),
                new Claim(ClaimTypes.Role, usuari.Rol)
            };

            var expirationMinutes = double.Parse(jwtSettings["DurationInMinutes"] ?? "480");

            var token = new JwtSecurityToken(
                issuer: jwtSettings["Issuer"],
                audience: jwtSettings["Audience"],
                claims: claims,
                expires: DateTime.UtcNow.AddMinutes(expirationMinutes),
                signingCredentials: creds
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }
    }
}