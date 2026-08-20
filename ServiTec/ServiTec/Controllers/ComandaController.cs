// ============================================================================
// Projecte:      ServiTec - Sistema de Gestio de Restaurants (TFG)
// Autor:         Luca Klein
// Titulacio:     Grau en Enginyeria Informatica (4t Curs)
// Institucio:    Universitat de Girona (UdG)
// Fitxer:        ComandaController.cs
// Descripcio:    Controlador RESTful encarregat del cicle de vida de les comandes,
//                l estat dels plats a cuina i la gestio de cobraments.
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
    /// Gestiona el cicle de vida complet de les comandes, estat a cuina i cobraments.
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class ComandaController : ControllerBase
    {
        private readonly ComandaService _comandaService;

        public ComandaController(ComandaService comandaService)
        {
            _comandaService = comandaService;
        }

        /// <summary>
        /// Obte la llista completa de comandes del sistema.
        /// </summary>
        /// <returns>Coleccio de comandes registrades.</returns>
        /// <response code="200">Retorna la llista de comandes.</response>
        /// <response code="401">No autoritzat (Manca el token JWT).</response>
        [HttpGet("llistar")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<ActionResult<IEnumerable<Comanda>>> Llistarcomanda()
        {
            var comandas = await _comandaService.GetComandas();
            return Ok(comandas);
        }

        /// <summary>
        /// Cerca una comanda concreta pel seu identificador.
        /// </summary>
        /// <param name="id">Identificador de la comanda a cercar.</param>
        /// <returns>Dades de la comanda sol licitada.</returns>
        /// <response code="200">Retorna la comanda trobada.</response>
        /// <response code="404">No s ha trobat la comanda amb l ID especificat.</response>
        [HttpGet("buscar/{id}")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<Comanda>> Buscarcomanda(int id)
        {
            var comanda = await _comandaService.GetById(id);

            if (comanda == null)
                return NotFound();

            return Ok(comanda);
        }

        /// <summary>
        /// Obte la comanda activa d una taula especifica.
        /// </summary>
        /// <param name="idTaula">Identificador de la taula.</param>
        /// <returns>La comanda activa associada a la taula.</returns>
        /// <response code="200">Retorna la comanda activa.</response>
        /// <response code="404">No s ha trobat cap comanda activa per a la taula.</response>
        [HttpGet("activa/{idTaula}")]
        [Authorize(Roles = "Cambrer, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> ObtenirComandaActiva(int idTaula)
        {
            var comanda = await _comandaService.ObtenirComandaActivaSegonsTaulaAsync(idTaula);

            if (comanda == null)
            {
                return NotFound(new { message = "No s ha trobat cap comanda activa per a aquesta taula." });
            }

            return Ok(comanda);
        }

        /// <summary>
        /// Crea una nova comanda al sistema.
        /// </summary>
        /// <param name="dto">Dades per a la creacio de la comanda.</param>
        /// <returns>La comanda creada.</returns>
        /// <response code="201">Comanda creada correctament.</response>
        /// <response code="400">Si la taula esta ocupada o les dades no son valides.</response>
        /// <response code="404">Si la taula no existeix.</response>
        /// <response code="500">Error intern del servidor.</response>
        [HttpPost("crear")]
        [Authorize(Roles = "Cambrer, Admin")]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult> CrearComanda([FromBody] CreateComandaDTO dto)
        {
            try
            {
                var comanda = await _comandaService.CrearComanda(dto);
                return StatusCode(StatusCodes.Status201Created, comanda);
            }
            catch (ArgumentException ex)
            {
                return NotFound(new { error = ex.Message });
            }
            catch (InvalidOperationException ex)
            {
                return BadRequest(new { error = ex.Message });
            }
            catch (Exception ex)
            {
                return StatusCode(StatusCodes.Status500InternalServerError, new { error = "Error intern del servidor.", detall = ex.Message });
            }
        }

        /// <summary>
        /// Actualitza la informacio d una comanda existent.
        /// </summary>
        /// <param name="id">Identificador de la comanda a actualitzar.</param>
        /// <param name="dto">Noves dades de la comanda.</param>
        /// <returns>La comanda actualitzada.</returns>
        /// <response code="200">Actualitzacio realitzada amb exit.</response>
        /// <response code="404">La comanda especificada no existeix.</response>
        [HttpPut("actualitzar/{id}")]
        [Authorize(Roles = "Cambrer, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult> ActualitzarComanda(int id, [FromBody] UpdateComandaDTO dto)
        {
            var comanda = await _comandaService.UpdateComandaDTO(id, dto);

            if (comanda == null)
                return NotFound();

            return Ok(comanda);
        }

        /// <summary>
        /// Elimina una comanda del sistema.
        /// </summary>
        /// <param name="id">Identificador de la comanda a eliminar.</param>
        /// <response code="204">La comanda s ha eliminat correctament.</response>
        /// <response code="404">La comanda especificada no existeix.</response>
        [HttpDelete("eliminar/{id}")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _comandaService.DeleteComanda(id);

            if (!eliminat)
                return NotFound();

            return NoContent();
        }

        /// <summary>
        /// Obte el llistat de comandes pendents per a la pantalla de cuina.
        /// </summary>
        /// <returns>Llista de comandes destinades a preparacio.</returns>
        /// <response code="200">Retorna les comandes pendents de cuina.</response>
        [HttpGet("cuina")]
        [Authorize(Roles = "Cuina, Cambrer, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public async Task<ActionResult<List<ComandaCuinaDTO>>> GetComandesCuina()
        {
            var comandes = await _comandaService.ObtenirComandesCuinaAsync();
            return Ok(comandes);
        }

        /// <summary>
        /// Canvia l estat general d una comanda.
        /// </summary>
        /// <param name="id">Identificador de la comanda.</param>
        /// <param name="nouEstat">Nou estat a assignar.</param>
        /// <response code="200">Estat actualitzat correctament.</response>
        /// <response code="404">La comanda especificada no existeix.</response>
        [HttpPut("{id}/estat")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> CanviarEstatComanda(int id, [FromBody] string nouEstat)
        {
            var exit = await _comandaService.CanviarEstatComandaAsync(id, nouEstat);

            if (!exit)
            {
                return NotFound(new { missatge = "Comanda no trobada" });
            }

            return Ok(new { missatge = "Estat de la comanda actualitzat correctament" });
        }

        /// <summary>
        /// Canvia l estat d una linia de comanda concreta.
        /// </summary>
        /// <param name="idLinia">Identificador de la linia de comanda.</param>
        /// <param name="nouEstat">Nou estat de preparacio del plat.</param>
        /// <response code="200">Estat de la linia actualitzat correctament.</response>
        /// <response code="404">La linia especificada no existeix.</response>
        [HttpPut("linia/{idLinia}/estat")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> CanviarEstatLinia(int idLinia, [FromBody] string nouEstat)
        {
            var exit = await _comandaService.CanviarEstatLiniaAsync(idLinia, nouEstat);

            if (!exit)
            {
                return NotFound(new { missatge = "Linia de comanda no trobada" });
            }

            return Ok(new { missatge = "Estat de la linia actualitzat correctament" });
        }

        /// <summary>
        /// Processa el cobrament i tancament d una comanda.
        /// </summary>
        /// <param name="idComanda">Identificador de la comanda a cobrar.</param>
        /// <response code="200">Comanda cobrada i tancada amb exit.</response>
        /// <response code="404">La comanda especificada no existeix.</response>
        [HttpPut("{idComanda}/cobrar")]
        [Authorize(Roles = "Cambrer, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> CobrarComanda(int idComanda)
        {
            var exit = await _comandaService.CobrarComandaAsync(idComanda);
            if (!exit) return NotFound(new { missatge = "Comanda no trobada" });

            return Ok(new { missatge = "Comanda cobrada i tancada correctament" });
        }

        /// <summary>
        /// Afegeix noves linies o productes a una comanda ja oberta.
        /// </summary>
        /// <param name="idComanda">Identificador de la comanda.</param>
        /// <param name="novesLinies">Llista de productes a afegir.</param>
        /// <returns>La comanda actualitzada amb les noves linies.</returns>
        /// <response code="200">Linies afegides correctament.</response>
        /// <response code="400">Si la llista esta buida o les dades no son valides.</response>
        /// <response code="404">Si la comanda no existeix.</response>
        /// <response code="500">Error intern del servidor.</response>
        [HttpPost("{idComanda}/linies")]
        [Authorize(Roles = "Cambrer, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> AfegirLinies(int idComanda, [FromBody] List<CreateLiniaComandaDTO> novesLinies)
        {
            if (novesLinies == null || !novesLinies.Any())
            {
                return BadRequest("La llista de productes no pot estar buida.");
            }

            try
            {
                var comandaActualitzada = await _comandaService.AfegirLiniesAComanda(idComanda, novesLinies);

                if (comandaActualitzada == null)
                {
                    return NotFound($"No s ha trobat cap comanda amb l ID {idComanda}");
                }

                return Ok(comandaActualitzada);
            }
            catch (ArgumentException ex)
            {
                return BadRequest(ex.Message);
            }
            catch (Exception ex)
            {
                return StatusCode(500, $"Error intern del servidor: {ex.Message}");
            }
        }

        /// <summary>
        /// Elimina una linia especifica d una comanda i recalcula el total.
        /// </summary>
        /// <param name="idLinia">Identificador de la linia a eliminar.</param>
        /// <response code="200">Linia eliminada i total recalculat.</response>
        /// <response code="400">Si l ID de la linia no es valid.</response>
        /// <response code="404">Si la linia no existeix.</response>
        [HttpPut("linia/{idLinia}/eliminar")]
        [Authorize(Roles = "Cambrer, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<IActionResult> EliminarLiniaComanda(int idLinia)
        {
            if (idLinia <= 0)
            {
                return BadRequest(new { missatge = "ID de linia invalid." });
            }

            var exit = await _comandaService.EliminarLiniaComandaAsync(idLinia);

            if (!exit)
            {
                return NotFound(new { missatge = "No s ha trobat la linia de comanda especificada." });
            }

            return Ok(new { missatge = "Linia de comanda eliminada correctament i total actualitzat." });
        }
    }
}