// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ProducteController.cs
// Descripció:    Controlador RESTful encarregat de la gestió del catàleg de
//                productes i cartes del restaurant.
// ============================================================================

using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.Services;
using ServiTec.Domain.Models;
using ServiTec.Infrastructure.Data;

namespace ServiTec.Controllers
{
    /// <summary>
    /// Gestiona la consulta, creacio, modificacio i eliminacio de productes de la carta.
    /// </summary>
    [Route("api/[controller]")]
    [ApiController]
    [Authorize]
    public class ProducteController : ControllerBase
    {
        private readonly ServiTecDbContext _context;
        private readonly ProducteService _producteService;

        public ProducteController(ProducteService producteService, ServiTecDbContext context)
        {
            _producteService = producteService;
            _context = context;
        }

        /// <summary>
        /// Obte la llista completa de productes disponibles al cataleg.
        /// </summary>
        /// <returns>Coleccio de productes registrats.</returns>
        /// <response code="200">Retorna la llista de productes.</response>
        /// <response code="404">No s ha trobat cap producte al cataleg.</response>
        [HttpGet("llistar")]
        [Authorize(Roles = "Cambrer, Cuina, Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public async Task<ActionResult<IEnumerable<ProducteDTO>>> LlistarProductes()
        {
            var productes = await _producteService.GetProductes();

            if (productes == null)
                return NotFound();

            return Ok(productes);
        }

        /// <summary>
        /// Crea un nou producte al cataleg de la carta.
        /// </summary>
        /// <param name="dto">Dades de creacio del producte.</param>
        /// <returns>L objecte del nou producte creat.</returns>
        /// <response code="201">Producte creat correctament.</response>
        /// <response code="400">Si la categoria indicada no existeix o les dades no son valides.</response>
        /// <response code="403">Acces prohibit només administradors.</response>
        [HttpPost("crear")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult> CrearProducte([FromBody] CreateProducteDTO dto)
        {
            var producte = await _producteService.CrearProducte(dto);

            if (producte == null)
                return BadRequest("La categoria no existe");

            return StatusCode(StatusCodes.Status201Created, producte);
        }

        /// <summary>
        /// Modifica les dades d un producte existent a la carta.
        /// </summary>
        /// <param name="id">Identificador del producte a modificar.</param>
        /// <param name="dto">Noves dades a aplicar al producte.</param>
        /// <returns>L objecte del producte actualitzat.</returns>
        /// <response code="200">Actualitzacio realitzada amb exit.</response>
        /// <response code="404">El producte especificat no existeix.</response>
        /// <response code="403">Acces prohibit només administradors.</response>
        [HttpPut("actualitzar/{id}")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<ActionResult<Producte>> ActualitzarProducte(int id, [FromBody] UpdateProducteDTO dto)
        {
            var producte = await _producteService.UpdateProducteDTO(id, dto);

            if (producte == null)
                return NotFound();

            return Ok(producte);
        }

        /// <summary>
        /// Elimina un producte del cataleg del restaurant.
        /// </summary>
        /// <param name="id">Identificador del producte a eliminar.</param>
        /// <response code="204">El producte s ha eliminat correctament.</response>
        /// <response code="404">El producte especificat no existeix.</response>
        /// <response code="403">Acces prohibit només administradors.</response>
        [HttpDelete("eliminar/{id}")]
        [Authorize(Roles = "Admin")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status403Forbidden)]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _producteService.DeleteProducte(id);

            if (!eliminat)
                return NotFound();

            return NoContent();
        }
    }
}