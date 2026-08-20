// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        TaulaService.cs
// Descripció:    Servei de domini encarregat de gestionar la lògica de negoci
//                de les taules del restaurant (operacions CRUD, ubicació a la
//                distribució de la sala i càlcul de l'estat actual de comanda).
// ============================================================================

using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Domain.Models;
using ServiTec.Infrastructure.Data;

namespace ServiTec.Services
{
    /// <summary>
    /// Servei encarregat de gestionar les consultes i operacions de la base de dades
    /// associades a les taules del menjador.
    /// </summary>
    public class TaulaService
    {
        private readonly ServiTecDbContext _context;

        public TaulaService(ServiTecDbContext context)
        {
            _context = context;
        }

        /// <summary>
        /// Obté la llista completa de taules registrant el seu estat calculat de comanda actiu.
        /// </summary>
        /// <returns>Col·lecció de DTOs amb la informació completa de cada taula.</returns>
        public async Task<IEnumerable<TaulaDTO>> GetAll()
        {
            return await _context.Taules
                .Select(t => new TaulaDTO
                {
                    IdTaula = t.IdTaula,
                    Numero = t.Numero,
                    Capacitat = t.Capacitat,
                    Estat = t.Estat,
                    // Subconsulta per determinar si la taula té comandes obertes o pendents
                    EstatComanda = _context.Comanda
                        .Where(c => c.IdTaula == t.IdTaula && (c.Estat == "oberta" || c.Estat == "pendent"))
                        .OrderByDescending(c => c.DataCreacio)
                        .Select(c => c.Estat)
                        .FirstOrDefault() ?? "lliure"
                })
                .ToListAsync();
        }

        /// <summary>
        /// Obté la informació d'una taula específica pel seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic de la taula.</param>
        /// <returns>El DTO de la taula trobada o null si no existeix.</returns>
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

        /// <summary>
        /// Registra una nova taula al sistema amb les seves coordenades de posició al menjador.
        /// </summary>
        /// <param name="dto">Objecte de transferència de dades amb la configuració de la nova taula.</param>
        /// <returns>El DTO de la taula creada amb l'ID assignat.</returns>
        public async Task<TaulaDTO> Create(CreateTaulaDTO dto)
        {
            var taula = new Taula
            {
                Numero = dto.Numero,
                Capacitat = dto.Capacitat,
                Estat = dto.Estat,
                IdMenjador = dto.IdMenjador,
                PosX = dto.PosX,
                PosY = dto.PosY
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

        /// <summary>
        /// Actualitza la informació d'una taula existent, incloent la seva capacitat i posició.
        /// </summary>
        /// <param name="id">Identificador únic de la taula a modificar.</param>
        /// <param name="dto">Objecte de transferència de dades amb les modificacions.</param>
        /// <returns>Cert si s'ha actualitzat correctament, o fals si no existia.</returns>
        public async Task<bool> Update(int id, UpdateTaulaDTO dto)
        {
            var taula = await _context.Taules.FindAsync(id);

            if (taula == null)
                return false;

            taula.Numero = dto.Numero;
            taula.Capacitat = dto.Capacitat;
            taula.Estat = dto.Estat;
            taula.PosX = dto.PosX;
            taula.PosY = dto.PosY;

            await _context.SaveChangesAsync();

            return true;
        }

        /// <summary>
        /// Elimina una taula del sistema a partir del seu identificador.
        /// </summary>
        /// <param name="id">Identificador únic de la taula a eliminar.</param>
        /// <returns>Cert si s'ha eliminat correctament, o fals si no existia.</returns>
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