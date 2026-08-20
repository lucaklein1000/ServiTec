// ============================================================================
// Projecte:      ServiTec - Sistema de Gestio de Restaurants (TFG)
// Autor:         Luca Klein
// Titulacio:     Grau en Enginyeria Informatica (4t Curs)
// Institucio:    Universitat de Girona (UdG)
// Fitxer:        CategoriaController.cs
// Descripcio:    Controlador RESTful encarregat de la gestio de les categories
//                dels productes del restaurant (begudes, entrants, postres, etc).
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
    /// Gestiona la consulta, creacio, modificacio i eliminacio de categories de productes.
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class CategoriaController : ControllerBase
    {
        private readonly CategoriaService _categoriaService;

        public CategoriaController(CategoriaService categoriaService)
        {
            _categoriaService = categoriaService;
        }

        /// <summary>
        /// Obte la llista completa de categories del sistema.
        /// </summary>
        /// <returns>Coleccio de categories registrades.</returns>
        /// <response code="200">Retorna la llista de categories.</response>
        /// <response code="401">No autoritzat (Manca el token JWT).</response>
        [HttpGet("llistar")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status401Unauthorized)]
        public async Task<ActionResult<IEnumerable<CategoriaDTO>>> LlistarCategoria()
        {
            var categorias = await _categoriaService.GetCategorias();
            return Ok(categorias);
        }

        /// <summary>
        /// Cerca una categoria concreta pel seu identificador.
        /// </summary>
        /// <param name="id">Identificador de la categoria a cercar.</param>
        /// <returns>Dades de la categoria sol licitada.</returns>
        /// <response code="200">Retorna la categoria trobada.</response>
        /// <response code="404">No s ha trobat la categoria amb l ID especificat.</response>
        [HttpGet("buscar/{id}")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<Categoria>> BuscarCategoria(int id)
        {
            var categoria = await _categoriaService.GetById(id);

            if (categoria == null)
                return NotFound(new { message = $"No s ha trobat la categoria amb ID {id}" });

            return Ok(categoria);
        }

        /// <summary>
        /// Crea una nova categoria al sistema.
        /// </summary>
        /// <param name="dto">Dades per a la creacio de la categoria.</param>
        /// <returns>La categoria creada.</returns>
        /// <response code="201">Categoria creada correctament.</response>
        /// <response code="400">Si les dades del DTO no son valides.</response>
        /// <response code="403">Acces prohibit nomes administradors.</response>
        [HttpPost("crear")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult> CrearCategoria([FromBody] CreateCategoriaDTO dto)
        {
            var categoria = await _categoriaService.CrearCategoria(dto);

            return StatusCode(StatusCodes.Status201Created, categoria);
        }

        /// <summary>
        /// Actualitza la informacio d una categoria existent.
        /// </summary>
        /// <param name="id">Identificador de la categoria a actualitzar.</param>
        /// <param name="dto">Noves dades de la categoria.</param>
        /// <returns>La categoria actualitzada.</returns>
        /// <response code="200">Actualitzacio realitzada amb exit.</response>
        /// <response code="404">La categoria especificada no existeix.</response>
        /// <response code="403">Acces prohibit nomes administradors.</response>
        [HttpPut("actualitzar/{id}")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult> ActualitzarCategoria(int id, [FromBody] UpdateCategoriaDTO dto)
        {
            var categoria = await _categoriaService.UpdateCategoriaDTO(id, dto);

            if (categoria == null)
                return NotFound(new { message = $"No s ha pogut actualitzar. La categoria amb ID {id} no existeix." });

            return Ok(categoria);
        }

        /// <summary>
        /// Elimina una categoria del sistema.
        /// </summary>
        /// <param name="id">Identificador de la categoria a eliminar.</param>
        /// <response code="204">La categoria s ha eliminat correctament.</response>
        /// <response code="404">La categoria especificada no existeix.</response>
        /// <response code="403">Acces prohibit nomes administradors.</response>
        [HttpDelete("eliminar/{id}")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _categoriaService.DeleteCategoria(id);

            if (!eliminat)
                return NotFound(new { message = $"No s ha pogut eliminar. La categoria amb ID {id} no existeix." });

            return NoContent();
        }
    }
}