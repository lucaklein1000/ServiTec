// ============================================================================
// Projecte:      ServiTec - Sistema de Gestio de Restaurants (TFG)
// Autor:         Luca Klein
// Titulacio:     Grau en Enginyeria Informatica (4t Curs)
// Institucio:    Universitat de Girona (UdG)
// Fitxer:        LiniaComandaController.cs
// Descripcio:    Controlador RESTful encarregat de la gestio de les linies
//                de comanda (productes individuals associats a cada comanda).
// ============================================================================

using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using ServiTec.Application.DTOs;
using ServiTec.Application.DTOs.ServiTec.DTOs;
using ServiTec.Services;

namespace ServiTec.Controllers
{
    /// <summary>
    /// Gestiona l'afegiment, modificacio i eliminacio de linies de comanda.
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class LiniaComandaController : ControllerBase
    {
        private readonly LiniaComandaService _liniaComandaService;

        public LiniaComandaController(LiniaComandaService liniaComandaService)
        {
            _liniaComandaService = liniaComandaService;
        }

        /// <summary>
        /// Obte la llista completa de linies de comanda del sistema.
        /// </summary>
        /// <returns>Coleccio de linies de comanda trobades.</returns>
        /// <response code="200">Retorna la llista de linies de comanda.</response>
        /// <response code="401">No autoritzat (Manca el token JWT).</response>
        [HttpGet("llistar")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<ActionResult<IEnumerable<LiniaComandaDTO>>> LlistarLinies()
        {
            var linies = await _liniaComandaService.GetAll();
            return Ok(linies);
        }

        /// <summary>
        /// Cerca una linia de comanda concreta pel seu identificador.
        /// </summary>
        /// <param name="id">Identificador de la linia a cercar.</param>
        /// <returns>Dades de la linia de comanda sol licitada.</returns>
        /// <response code="200">Retorna la linia trobada.</response>
        /// <response code="404">No s ha trobat la linia de comanda amb l ID especificat.</response>
        [HttpGet("buscar/{id}")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<LiniaComandaDTO>> BuscarLinia(int id)
        {
            var linia = await _liniaComandaService.GetById(id);

            if (linia == null)
                return NotFound(new { message = $"No s ha trobat la linia de comanda amb ID {id}" });

            return Ok(linia);
        }

        /// <summary>
        /// Afegeix un producte o linia de comanda a una comanda existent.
        /// </summary>
        /// <param name="dto">Dades per crear la linia de comanda.</param>
        /// <returns>Dades de la linia creada.</returns>
        /// <response code="201">Linia creada correctament.</response>
        /// <response code="400">Si el producte especificat no existeix o les dades no son valides.</response>
        [HttpPost("crear")]
        [Authorize(Roles = "Cambrer, Admin")]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        public async Task<ActionResult<LiniaComandaDTO>> CrearLinia([FromBody] CreateLiniaComandaDTO dto)
        {
            var liniaCreada = await _liniaComandaService.Create(dto);

            if (liniaCreada == null)
                return BadRequest(new { message = "No s ha pogut crear la linia. El producte especificat no existeix." });

            return StatusCode(StatusCodes.Status201Created, liniaCreada);
        }

        /// <summary>
        /// Actualitza la quantitat o estat d una linia de comanda existent.
        /// </summary>
        /// <param name="id">Identificador de la linia a actualitzar.</param>
        /// <param name="dto">Dades amb la nova quantitat.</param>
        /// <response code="200">Actualitzacio realitzada amb exit.</response>
        /// <response code="404">La linia especificada no existeix.</response>
        [HttpPut("actualitzar/{id}")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult> ActualitzarLinia(int id, [FromBody] UpdateLiniaComandaDTO dto)
        {
            var exit = await _liniaComandaService.Update(id, dto);

            if (!exit)
                return NotFound(new { message = $"No s ha pogut actualitzar. La linia amb ID {id} no existeix." });

            return Ok(new { message = "Linia de comanda actualitzada correctament." });
        }

        /// <summary>
        /// Elimina una linia de comanda del sistema.
        /// </summary>
        /// <param name="id">Identificador de la linia a eliminar.</param>
        /// <response code="204">La linia s ha eliminat correctament.</response>
        /// <response code="404">La linia especificada no existeix.</response>
        [HttpDelete("eliminar/{id}")]
        [Authorize(Roles = "Cambrer, Admin")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _liniaComandaService.Delete(id);

            if (!eliminat)
                return NotFound(new { message = $"No s ha pogut eliminar. La linia amb ID {id} no existeix." });

            return NoContent();
        }
    }
}