// ============================================================================
// Projecte:      ServiTec - Sistema de Gestio de Restaurants (TFG)
// Autor:         Luca Klein
// Titulacio:     Grau en Enginyeria Informatica (4t Curs)
// Institucio:    Universitat de Girona (UdG)
// Fitxer:        MenjadorController.cs
// Descripcio:    Controlador RESTful encarregat de la gestio de les zones o
//                menjadors del restaurant (terrassa, menjador principal, etc).
// ============================================================================

using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.Services;
using ServiTec.Domain.Models;

namespace ServiTec.Controllers
{
    /// <summary>
    /// Gestiona les zones o menjadors de la sala del restaurant.
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class MenjadorController : ControllerBase
    {
        private readonly MenjadorService _menjadorService;

        public MenjadorController(MenjadorService menjadorService)
        {
            _menjadorService = menjadorService;
        }

        /// <summary>
        /// Obte la llista completa de menjadors o zones registrades.
        /// </summary>
        /// <returns>Coleccio de zones o menjadors trobats.</returns>
        /// <response code="200">Retorna la llista de menjadors.</response>
        /// <response code="401">No autoritzat (Manca el token JWT).</response>
        [HttpGet("llistar")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<ActionResult<IEnumerable<Menjador>>> LlistarMenjador()
        {
            var menjadors = await _menjadorService.GetMenjadors();
            return Ok(menjadors);
        }

        /// <summary>
        /// Crea una nova zona o menjador al restaurant.
        /// </summary>
        /// <param name="dto">Dades de creacio del menjador.</param>
        /// <returns>El nou menjador creat amb el seu ID assignat.</returns>
        /// <response code="201">Menjador creat correctament.</response>
        /// <response code="400">Si les dades del DTO no son valides.</response>
        /// <response code="403">Acces prohibit nomes administradors.</response>
        [HttpPost("crear")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult> CrearMenjador([FromBody] CreateMenjadorDTO dto)
        {
            var menjador = await _menjadorService.Create(dto);

            return StatusCode(StatusCodes.Status201Created, menjador);
        }

        /// <summary>
        /// Actualitza la informacio d un menjador o zona existent.
        /// </summary>
        /// <param name="id">Identificador del menjador a modificar.</param>
        /// <param name="dto">Noves dades a aplicar al menjador.</param>
        /// <returns>El menjador actualitzat.</returns>
        /// <response code="200">Actualitzacio realitzada amb exit.</response>
        /// <response code="404">El menjador especificat no existeix.</response>
        /// <response code="403">Acces prohibit nomes administradors.</response>
        [HttpPut("actualitzar/{id}")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult> AcualitzarMenjador(int id, [FromBody] UpdateMenjadorDTO dto)
        {
            var menjador = await _menjadorService.Update(id, dto);

            if (menjador == null)
                return NotFound();

            return Ok(menjador);
        }

        /// <summary>
        /// Elimina una zona o menjador del sistema.
        /// </summary>
        /// <param name="id">Identificador del menjador a eliminar.</param>
        /// <response code="204">El menjador s ha eliminat correctament.</response>
        /// <response code="404">El menjador especificat no existeix.</response>
        /// <response code="403">Acces prohibit nomes administradors.</response>
        [HttpDelete("eliminar/{id}")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _menjadorService.Delete(id);

            if (!eliminat)
                return NotFound();

            return NoContent();
        }
    }
}