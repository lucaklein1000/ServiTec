using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.Services;
using ServiTec.Domain.Models;
using ServiTec.Infrastructure.Data;

namespace ServiTec.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProducteController : ControllerBase
    {
        private readonly ServiTecDbContext _context;
        private readonly ProducteService _producteService;

        public ProducteController(ProducteService producteService, ServiTecDbContext context)
        {
            _producteService = producteService;
            _context = context;
        }

        /// <brief>
        /// Recupera todos los productos de la base de datos y devuelve una colección
        /// de objetos DTO con la información básica de cada producto.
        /// </brief>
        /// <pre>
        /// - La base de datos debe estar accesible.
        /// - La entidad Productes debe existir en el contexto.
        /// </pre>
        /// <post>
        /// - Se devuelve una lista de productos existentes en el sistema.
        /// </post>
        /// <returns>
        /// 200 OK con la lista de productos.
        /// </returns>
        [HttpGet("llistar")]
        public async Task<ActionResult<IEnumerable<ProducteDTO>>> LlistarProductes()
        {
            var productes = await _producteService.GetProductes();

            if (productes == null)
                return NotFound();

            return Ok(productes);
        }

        /// <brief>
        /// Inserta un nuevo registro en la tabla de productos utilizando los datos
        /// recibidos a través de un objeto DTO.
        /// </brief>
        /// <pre>
        /// - Los datos proporcionados deben ser válidos.
        /// - La categoría indicada debe existir en la base de datos.
        /// </pre>
        /// <post>
        /// - Se crea un nuevo producto en la base de datos.
        /// </post>
        /// <param name="dto">
        /// Objeto que contiene la información necesaria para crear el producto.
        /// </param>
        /// <returns>
        /// 201 Created si el producto se crea correctamente.
        /// 400 BadRequest si la categoría indicada no existe.
        /// </returns>
        [HttpPost("guardarProducte")]
        public async Task<ActionResult> CrearProducte(CreateProducteDTO dto)
        {

            var producte = await _producteService.CrearProducte(dto);

            if (producte == null)
                return BadRequest("La categoria no existe");

            return StatusCode(StatusCodes.Status201Created, producte);
        }

        /// <brief>
        /// Modifica los datos de un producto identificado por su id utilizando
        /// los valores proporcionados en el DTO de actualización.
        /// </brief>
        /// <pre>
        /// - El producto indicado debe existir en la base de datos.
        /// - Los datos proporcionados deben ser válidos.
        /// </pre>
        /// <post>
        /// - Los datos del producto quedan actualizados en la base de datos.
        /// </post>
        /// <param name="id">
        /// Identificador del producto a actualizar.
        /// </param>
        /// <param name="dto">
        /// Objeto que contiene los nuevos datos del producto.
        /// </param>
        /// <returns>
        /// 200 OK si la actualización se realiza correctamente.
        /// 404 NotFound si el producto no existe.
        /// </returns>
        [HttpPut("UpdateProducteDTO/{id}")]
        public async Task<ActionResult<Producte>> ActualitzarProducte(int id, UpdateProducteDTO dto)
        {
            var producte = await _producteService.UpdateProducteDTO(id, dto);

            if (producte == null)
                return NotFound();

            return Ok(producte);
        }

        /// <brief>
        /// Elimina de la base de datos el producto identificado por su id.
        /// </brief>
        /// <pre>
        /// - El producto indicado debe existir en la base de datos.
        /// </pre>
        /// <post>
        /// - El producto queda eliminado de la base de datos.
        /// </post>
        /// <param name="id">
        /// Identificador del producto a eliminar.
        /// </param>
        /// <returns>
        /// 204 NoContent si la eliminación se realiza correctamente.
        /// 404 NotFound si el producto no existe.
        /// </returns>
        [HttpDelete("eliminarProducte/{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _producteService.DeleteProducte(id);

            if (!eliminat)
                  return NotFound();

            return NoContent();           
        }
    }
}
