// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        TaulaController.cs
// Descripció:    Controlador RESTful encarregat de la gestió de les taules de
//                la sala del restaurant i la seva disponibilitat.
// ============================================================================

using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using ServiTec.Application.DTOs;
using ServiTec.Application.DTOs.ServiTec.DTOs;
using ServiTec.Services;

namespace ServiTec.Controllers
{
    /// <summary>
    /// Gestiona l'estat, creació i manteniment de les taules de la sala.
    /// </summary>
    [ApiController]
    [Route("api/[controller]")]
    [Authorize] //  Protecció global: Requereix token JWT vàlid
    public class TaulaController : ControllerBase
    {
        private readonly TaulaService _service;

        public TaulaController(TaulaService service)
        {
            _service = service;
        }

        /// <summary>
        /// Obté el llistat complet de taules i la seva disponibilitat actual.
        /// </summary>
        /// <returns>Col·lecció amb totes les taules registrades.</returns>
        /// <response code="200">Retorna el llistat de taules.</response>
        /// <response code="401">No autoritzat (Manca el token JWT).</response>
        [HttpGet("llistar")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")] // Tots els rols poden consultar la sala
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<ActionResult<IEnumerable<TaulaDTO>>> GetAll()
        {
            return Ok(await _service.GetAll());
        }

        /// <summary>
        /// Cerca una taula específica pel seu identificador primari.
        /// </summary>
        /// <param name="id">Identificador únic de la taula.</param>
        /// <returns>Dades detallades de la taula sol·licitada.</returns>
        /// <response code="200">Retorna la informació de la taula.</response>
        /// <response code="404">No s'ha trobat cap taula amb l'ID especificat.</response>
        [HttpGet("{id}")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<TaulaDTO>> GetById(int id)
        {
            var taula = await _service.GetById(id);

            if (taula == null)
                return NotFound();

            return Ok(taula);
        }

        /// <summary>
        /// Alta d'una nova taula al plànol de la sala.
        /// </summary>
        /// <param name="dto">Dades de la nova taula (Número, Capacitat, Ubicació).</param>
        /// <returns>La taula creada amb el seu ID assignat.</returns>
        /// <response code="201">Taula registrada correctament.</response>
        /// <response code="400">Si les dades del DTO no són vàlides.</response>
        /// <response code="403">Accés prohibit (Només Administradors).</response>
        [HttpPost("crear")]
        [Authorize(Roles = "Admin")] //  Manteniment de sala: Només Administradors
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult<TaulaDTO>> Create([FromBody] CreateTaulaDTO dto)
        {
            var taula = await _service.Create(dto);

            return CreatedAtAction(nameof(GetById), new { id = taula.IdTaula }, taula);
        }

        /// <summary>
        /// Actualitza les propietats o l'estat d'ocupació d'una taula.
        /// </summary>
        /// <param name="id">Identificador de la taula a modificar.</param>
        /// <param name="dto">Noves dades a aplicar a la taula.</param>
        /// <response code="204">Actualització realitzada amb èxit.</response>
        /// <response code="404">La taula especificada no existeix.</response>
        /// <response code="403">Accés prohibit (Només Cambrers o Administradors).</response>
        [HttpPut("Actualitza{id}")]
        [Authorize(Roles = "Cambrer, Admin")] // Els cambrers poden canviar l'estat (Ocupada/Lliure)
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<IActionResult> Update(int id, [FromBody] UpdateTaulaDTO dto)
        {
            var updated = await _service.Update(id, dto);

            if (!updated)
                return NotFound();

            return NoContent();
        }

        /// <summary>
        /// Elimina una taula del sistema.
        /// </summary>
        /// <param name="id">Identificador de la taula a eliminar.</param>
        /// <response code="204">La taula s'ha eliminat correctament.</response>
        /// <response code="404">La taula especificada no existeix.</response>
        /// <response code="403">Accés prohibit (Només Administradors).</response>
        [HttpDelete("borra{id}")]
        [Authorize(Roles = "Admin")] //  Manteniment de sala: Només Administradors
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _service.Delete(id);

            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}