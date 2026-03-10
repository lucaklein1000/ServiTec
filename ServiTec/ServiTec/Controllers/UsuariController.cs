using Microsoft.AspNetCore.Mvc;
using ServiTec.Domain.Models;
using ServiTec.Application.DTOs;

namespace ServiTec.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class UsuariController : ControllerBase
    {
        private readonly UsuariService _usuariService;

        public UsuariController(UsuariService usuariService)
        {
            _usuariService = usuariService;
        }

        /// <brief>
        /// Recupera la llista completa d'usuaris del sistema.
        /// </brief>
        /// <pre>
        /// - El servei d'usuaris ha d'estar operatiu.
        /// </pre>
        /// <post>
        /// - Es retorna una col·lecció amb tots els usuaris registrats.
        /// </post>
        /// <returns>
        /// 200 OK amb la llista d'usuaris.
        /// </returns>
        [HttpGet("llistar")]
        public async Task<ActionResult<IEnumerable<Usuari>>> LlistarUsuaris()
        {
            var usuaris = await _usuariService.GetAll();
            return Ok(usuaris);
        }

        /// <brief>
        /// Cerca un usuari concret a partir del seu identificador.
        /// </brief>
        /// <pre>
        /// - L'identificador proporcionat ha de ser vàlid.
        /// </pre>
        /// <post>
        /// - Si l'usuari existeix, es retorna la seva informació.
        /// </post>
        /// <param name="id">
        /// Identificador de l'usuari a cercar.
        /// </param>
        /// <returns>
        /// 200 OK amb l'usuari trobat.
        /// 404 NotFound si l'usuari no existeix.
        /// </returns>
        [HttpGet("buscar/{id}")]
        public async Task<ActionResult<Usuari>> BuscarUsuari(int id)
        {
            var usuari = await _usuariService.GetById(id);

            if (usuari == null)
                return NotFound();

            return Ok(usuari);
        }

        /// <brief>
        /// Crea un nou usuari al sistema.
        /// </brief>
        /// <pre>
        /// - Les dades del DTO han de ser vàlides.
        /// </pre>
        /// <post>
        /// - Es crea un nou registre d'usuari al sistema.
        /// </post>
        /// <param name="dto">
        /// Objecte DTO que conté la informació necessària per crear l'usuari.
        /// </param>
        /// <returns>
        /// 201 Created si la creació es realitza correctament.
        /// </returns>
        [HttpPost("crear")]
        public async Task<ActionResult> CrearUsuari(CreateUsuariDTO dto)
        {
            var usuari = await _usuariService.Create(dto);

            return StatusCode(StatusCodes.Status201Created, usuari);
        }

        /// <brief>
        /// Actualitza la informació d'un usuari existent.
        /// </brief>
        /// <pre>
        /// - L'usuari indicat ha d'existir.
        /// - Les dades proporcionades han de ser vàlides.
        /// </pre>
        /// <post>
        /// - Les dades de l'usuari queden actualitzades al sistema.
        /// </post>
        /// <param name="id">
        /// Identificador de l'usuari a actualitzar.
        /// </param>
        /// <param name="dto">
        /// Objecte DTO amb les noves dades de l'usuari.
        /// </param>
        /// <returns>
        /// 200 OK si l'actualització es realitza correctament.
        /// 404 NotFound si l'usuari no existeix.
        /// </returns>
        [HttpPut("actualitzar/{id}")]
        public async Task<ActionResult> ActualitzarUsuari(int id, UpdateUsuariDTO dto)
        {
            var usuari = await _usuariService.Update(id, dto);

            if (usuari == null)
                return NotFound();

            return Ok(usuari);
        }

        /// <brief>
        /// Elimina un usuari del sistema.
        /// </brief>
        /// <pre>
        /// - L'usuari indicat ha d'existir al sistema.
        /// </pre>
        /// <post>
        /// - L'usuari és eliminat del sistema.
        /// </post>
        /// <param name="id">
        /// Identificador de l'usuari a eliminar.
        /// </param>
        /// <returns>
        /// 204 NoContent si l'eliminació es realitza correctament.
        /// 404 NotFound si l'usuari no existeix.
        /// </returns>
        [HttpDelete("eliminar/{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var eliminat = await _usuariService.Delete(id);

            if (!eliminat)
                return NotFound();

            return NoContent();
        }
    }
}