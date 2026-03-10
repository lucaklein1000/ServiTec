using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.DTOs.ServiTec.DTOs;
using ServiTec.Domain.Models;
using ServiTec.Infrastructure.Data;

namespace ServiTec.Services
{
    public class TaulaService
    {
        private readonly ServiTecDbContext _context;

        public TaulaService(ServiTecDbContext context)
        {
            _context = context;
        }

        public async Task<IEnumerable<TaulaDTO>> GetAll()
        {
            return await _context.Taules
                .Select(t => new TaulaDTO
                {
                    IdTaula = t.IdTaula,
                    Numero = t.Numero,
                    Capacitat = t.Capacitat,
                    Estat = t.Estat
                })
                .ToListAsync();
        }

        public async Task<TaulaDTO?> GetById(int id)
        {
            var taula = await _context.Taules.FindAsync(id);

            if (taula == null)
                return null;

            return new TaulaDTO
            {
                IdTaula = taula.IdTaula,
                Numero = taula.Numero,
                Capacitat = taula.Capacitat,
                Estat = taula.Estat
            };
        }

        public async Task<TaulaDTO> Create(CreateTaulaDTO dto)
        {
            var taula = new Taula
            {
                Numero = dto.Numero,
                Capacitat = dto.Capacitat,
                Estat = dto.Estat
            };

            _context.Taules.Add(taula);
            await _context.SaveChangesAsync();

            return new TaulaDTO
            {
                IdTaula = taula.IdTaula,
                Numero = taula.Numero,
                Capacitat = taula.Capacitat,
                Estat = taula.Estat
            };
        }

        public async Task<bool> Update(int id, UpdateTaulaDTO dto)
        {
            var taula = await _context.Taules.FindAsync(id);

            if (taula == null)
                return false;

            taula.Numero = dto.Numero;
            taula.Capacitat = dto.Capacitat;
            taula.Estat = dto.Estat;

            await _context.SaveChangesAsync();

            return true;
        }

        public async Task<bool> Delete(int id)
        {
            var taula = await _context.Taules.FindAsync(id);

            if (taula == null)
                return false;

            _context.Taules.Remove(taula);
            await _context.SaveChangesAsync();

            return true;
        }
    }
}