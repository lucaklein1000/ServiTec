// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        AuthController.cs
// Descripció:    Controlador RESTful encarregat de l'autenticació d'usuaris
//                i de la generació de tokens d'accés JWT.
// ============================================================================

using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using ServiTec.Application.DTOs;
using ServiTec.Application.Services;

namespace ServiTec.Controllers
{
    /// <summary>
    /// Gestiona l'autenticació d'usuaris i la verificació de credencials.
    /// </summary>
    [ApiController]
    [Route("api/[controller]")]
    public class AuthController : ControllerBase
    {
        private readonly AuthService _authService;

        public AuthController(AuthService authService)
        {
            _authService = authService;
        }

        /// <summary>
        /// Autentica un usuari del sistema i retorna un token JWT si les credencials són vàlides.
        /// </summary>
        /// <param name="dto">Objecte amb el nom d'usuari i la contrasenya.</param>
        /// <returns>Dades de l'usuari autenticat i el token JWT corresponent.</returns>
        /// <response code="200">Autenticació exitosa. Retorna el token JWT.</response>
        /// <response code="400">Si falten el nom d'usuari o la contrasenya.</response>
        /// <response code="401">Credencials incorrectes o usuari inactiu.</response>
        [HttpPost("login")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<IActionResult> Login([FromBody] LoginRequest dto)
        {
            if (string.IsNullOrWhiteSpace(dto.NomUsuari) || string.IsNullOrWhiteSpace(dto.Contrasenya))
            {
                return BadRequest(new { message = "El nom d'usuari i la contrasenya són obligatoris." });
            }

            var result = await _authService.AutenticarAsync(dto);

            if (result == null)
            {
                return Unauthorized(new { message = "Nom d'usuari o contrasenya incorrectes o usuari inactiu." });
            }

            return Ok(result);
        }
    }
}