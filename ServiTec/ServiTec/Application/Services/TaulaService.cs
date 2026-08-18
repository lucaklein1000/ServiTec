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
                    Estat = t.Estat,
                    EstatComanda = _context.Comanda
                        .Where(c => c.IdTaula == t.IdTaula && (c.Estat == "oberta" || c.Estat == "pendent"))
                        .OrderByDescending(c => c.DataCreacio)
                        .Select(c => c.Estat)
                        .FirstOrDefault() ?? "lliure"
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
                Numero = dto.PostNumero,
                Capacitat = dto.PostCapacitat,
                Estat = dto.PostEstat,
                IdMenjador = dto.PostIdMenjador,
                PosX = dto.PostPosX,
                PosY = dto.PostPosY
            };

            _context.Taules.Add(taula);
            await _context.SaveChangesAsync();

            return new TaulaDTO
            {
                IdTaula = taula.IdTaula,
                Numero = taula.Numero,
                Capacitat = taula.Capacitat,
                Estat = taula.Estat,
                IdMenjador = taula.IdMenjador,
                PosX = taula.PosX,
                PosY = taula.PosY
            };
        }

        public async Task<bool> Update(int id, UpdateTaulaDTO dto)
        {
            var taula = await _context.Taules.FindAsync(id);

            if (taula == null)
                return false;

            taula.Numero = dto.PutNumero;
            taula.Capacitat = dto.PutCapacitat;
            taula.Estat = dto.PutEstat;
            taula.PosX = dto.PutPosX;
            taula.PosY = dto.PutPosY;

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