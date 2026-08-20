// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        UsuariController.cs
// Descripció:    Controlador RESTful encarregat de la gestió del cicle de vida
//                dels usuaris del sistema i el procés d'autenticació.
// ============================================================================

using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Domain.Models;

namespace ServiTec.Controllers
{
    /// <summary>
    /// Gestiona les operacions CRUD d'usuaris i l'autenticació en l'ecosistema ServiTec.
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    [Authorize] //  Tot el controlador requereix autenticació per defecte
    public class UsuariController : ControllerBase
    {
        private readonly UsuariService _usuariService;

        public UsuariController(UsuariService usuariService)
        {
            _usuariService = usuariService;
        }

        /// <summary>
        /// Obté la llista completa d'usuaris registrats al sistema.
        /// </summary>
        /// <returns>Col·lecció amb tots els usuaris trobats.</returns>
        /// <response code="200">Retorna la llista d'usuaris.</response>
        /// <response code="401">No autoritzat (Manca el token JWT).</response>
        /// <response code="403">Accés prohibit (L'usuari no té el rol permès).</response>
        [HttpGet("llistar")]
        [Authorize(Roles = "Cambrer, Admin")] // Permet als cambrers consultar usuaris (ex: assignar comandas)
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult<IEnumerable<Usuari>>> LlistarUsuaris()
        {
            var usuaris = await _usuariService.GetAll();
            return Ok(usuaris);
        }

        /// <summary>
        /// Cerca un usuari concret pel seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador primari de l'usuari.</param>
        /// <returns>L'objecte de l'usuari sol·licitat.</returns>
        /// <response code="200">Retorna l'usuari si existeix.</response>
        /// <response code="404">No s'ha trobat cap usuari amb l'ID especificat.</response>
        [HttpGet("buscar/{id}")]
        [Authorize(Roles = "Cambrer, Admin")] // Permet consultar informació d'un usuari específic
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<Usuari>> BuscarUsuari(int id)
        {
            var usuari = await _usuariService.GetById(id);

            if (usuari == null)
                return NotFound();

            return Ok(usuari);
        }

        /// <summary>
        /// Registra un nou usuari al sistema.
        /// </summary>
        /// <param name="dto">Dades de creació de l'usuari.</param>
        /// <returns>L'usuari creat amb el seu ID assignat.</returns>
        /// <response code="201">Usuari creat correctament.</response>
        /// <response code="400">Si les dades del DTO no són vàlides.</response>
        /// <response code="403">Accés prohibit (Només Administradors).</response>
        [HttpPost("crear")]
        [Authorize(Roles = "Admin")] //  Mètode crític: Només Administradors
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult> CrearUsuari([FromBody] CreateUsuariDTO dto)
        {
            var usuari = await _usuariService.Create(dto);

            return StatusCode(StatusCodes.Status201Created, usuari);
        }

        /// <summary>
        /// Actualitza la informació d'un usuari existent.
        /// </summary>
        /// <param name="id">Identificador de l'usuari a modificar.</param>
        /// <param name="dto">Noves dades a aplicar a l'usuari.</param>
        /// <returns>L'usuari actualitzat.</returns>
        /// <response code="200">Actualització realitzada amb èxit.</response>
        /// <response code="404">L'usuari especificat no existeix.</response>
        /// <response code="403">Accés prohibit (Només Administradors).</response>
        [HttpPut("actualitzar/{id}")]
        [Authorize(Roles = "Admin")] //  Mètode crític: Només Administradors
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult> ActualitzarUsuari(int id, [FromBody] UpdateUsuariDTO dto)
        {
            var usuari = await _usuariService.Update(id, dto);

            if (usuari == null)
                return NotFound();

            return Ok(usuari);
        }

        /// <summary>
        /// Elimina un usuari del sistema.
        /// </summary>
        /// <param name="id">Identificador de l'usuari a eliminar.</param>
        /// <response code="204">L'usuari s'ha eliminat correctament.</response>
        /// <response code="404">L'usuari especificat no existeix.</response>
        /// <response code="403">Accés prohibit (Només Administradors).</response>
        [HttpDelete("eliminar/{id}")]
        [Authorize(Roles = "Admin")] //  Mètode crític: Només Administradors
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _usuariService.Delete(id);

            if (!eliminat)
                return NotFound();

            return NoContent();
        }

        /// <summary>
        /// Autentica un usuari mitjançant les seves credencials d'accés.
        /// </summary>
        /// <param name="request">Credencials d'accés (Nom d'usuari i Contrasenya).</param>
        /// <returns>Dades de l'usuari autenticat i el seu corresponent token d'accés.</returns>
        /// <response code="200">Autenticació satisfactòria.</response>
        /// <response code="401">Credencials incorrectes o usuari inactiu.</response>
        [HttpPost("login")]
        [AllowAnonymous] // Permet l'accés públic per poder iniciar sessió sense token
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<IActionResult> Login([FromBody] LoginRequest request)
        {
            var usuari = await _usuariService.ValidarLogin(request.NomUsuari, request.Contrasenya);

            if (usuari == null)
                return Unauthorized(new { message = "Usuari o contrasenya incorrectes" });

            return Ok(usuari);
        }
    }
}