using Microsoft.AspNetCore.Mvc;
using ServiTec.Application.DTOs;
using ServiTec.Application.DTOs.ServiTec.DTOs;
using ServiTec.Services;

namespace ServiTec.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class TaulaController : ControllerBase
    {
        private readonly TaulaService _service;

        public TaulaController(TaulaService service)
        {
            _service = service;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<TaulaDTO>>> GetAll()
        {
            return Ok(await _service.GetAll());
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<TaulaDTO>> GetById(int id)
        {
            var taula = await _service.GetById(id);

            if (taula == null)
                return NotFound();

            return Ok(taula);
        }

        [HttpPost]
        public async Task<ActionResult<TaulaDTO>> Create(CreateTaulaDTO dto)
        {
            var taula = await _service.Create(dto);

            return CreatedAtAction(nameof(GetById), new { id = taula.IdTaula }, taula);
        }

        [HttpPut("{id}")]
        public async Task<IActionResult> Update(int id, UpdateTaulaDTO dto)
        {
            var updated = await _service.Update(id, dto);

            if (!updated)
                return NotFound();

            return NoContent();
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(int id)
        {
            var deleted = await _service.Delete(id);

            if (!deleted)
                return NotFound();

            return NoContent();
        }
    }
}