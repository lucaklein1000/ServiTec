// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        MenjadorService.cs
// Descripció:    Servei de domini encarregat de gestionar la lògica de negoci
//                dels menjadors o sales del restaurant (CRUD, distribució de
//                taules associades i estat global de les comandes).
// ============================================================================

using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.Interfaces;
using ServiTec.Domain.Models;
using ServiTec.Infrastructure.Data;

namespace ServiTec.Application.Services
{
    /// <summary>
    /// Servei encarregat de gestionar l'accés a dades i les regles de negoci
    /// associades a les sales o menjadors del restaurant.
    /// </summary>
    public class MenjadorService
    {
        private readonly ServiTecDbContext _context;
        private readonly IRepository<Menjador> _menjadorRepository;

        public MenjadorService(ServiTecDbContext context, IRepository<Menjador> menjadorRepository)
        {
            _context = context;
            _menjadorRepository = menjadorRepository;
        }

        /// <summary>
        /// Obté la informació d'un menjador específic pel seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic del menjador.</param>
        /// <returns>El DTO del menjador trobat o null si no existeix.</returns>
        public async Task<MenjadorDTO?> GetById(int id)
        {
            var menjador = await _menjadorRepository.GetById(id);

            if (menjador == null)
                return null;

            return new MenjadorDTO
            {
                IdMenjador = menjador.IdMenjador,
                NomMenjador = menjador.NomMenjador,
                Actiu = menjador.Actiu
            };
        }

        /// <summary>
        /// Obté la llista completa de menjadors incloent el detall de les seves taules i l'estat actual de cada comanda.
        /// </summary>
        /// <returns>Col·lecció de DTOs amb l'estructura completa de menjadors i taules.</returns>
        public async Task<IEnumerable<MenjadorDTO>> GetMenjadors()
        {
            var menjadors = await _context.Menjadors
                .Include(m => m.Taula)
                .ToListAsync();

            return menjadors.Select(m => new MenjadorDTO
            {
                IdMenjador = m.IdMenjador,
                NomMenjador = m.NomMenjador,
                Actiu = m.Actiu,
                Taules = m.Taula.Select(t => new TaulaDTO
                {
                    IdTaula = t.IdTaula,
                    Numero = t.Numero,
                    Capacitat = t.Capacitat,
                    Estat = t.Estat,
                    IdMenjador = t.IdMenjador,
                    EstatComanda = _context.Comanda
                        .Where(c => c.IdTaula == t.IdTaula && (c.Estat == "oberta" || c.Estat == "pendent"))
                        .OrderByDescending(c => c.DataCreacio)
                        .Select(c => c.Estat)
                        .FirstOrDefault() ?? "lliure",
                    PosX = t.PosX,
                    PosY = t.PosY
                }).ToList()
            }).ToList();
        }

        /// <summary>
        /// Crea un nou menjador registrant opcionalment la disposició inicial de les seves taules.
        /// </summary>
        /// <param name="dto">Objecte de transferència de dades amb la informació del nou menjador.</param>
        /// <returns>L'entitat del menjador creat.</returns>
        public async Task<Menjador?> Create(CreateMenjadorDTO dto)
        {
            var menjador = new Menjador
            {
                NomMenjador = dto.NomMenjador,
                Actiu = true,

                // Assignem de forma seqüencial el número de taula segons la posició a la llista rebuda
                Taula = dto.Taules?.Select((t, index) => new Taula
                {
                    Numero = index + 1,
                    Capacitat = t.Capacitat,
                    PosX = t.PosX,
                    PosY = t.PosY,
                    Estat = true
                }).ToList() ?? new List<Taula>()
            };

            var resultat = await _menjadorRepository.Create(menjador);
            return resultat;
        }

        /// <summary>
        /// Actualitza el nom i l'estat d'activació d'un menjador existent.
        /// </summary>
        /// <param name="id">Identificador únic del menjador a modificar.</param>
        /// <param name="dto">Objecte de transferència de dades amb les modificacions.</param>
        /// <returns>El menjador actualitzat o null si no s'ha trobat.</returns>
        public async Task<Menjador?> Update(int id, UpdateMenjadorDTO dto)
        {
            var menjador = await _menjadorRepository.GetById(id);

            if (menjador == null)
                return null;

            menjador.NomMenjador = dto.NomMenjador;
            menjador.Actiu = dto.Actiu;

            await _menjadorRepository.Update(menjador);
            return menjador;
        }

        /// <summary>
        /// Elimina un menjador del sistema a partir del seu identificador.
        /// </summary>
        /// <param name="id">Identificador únic del menjador a eliminar.</param>
        /// <returns>Cert si s'ha eliminat correctament, o fals si no existia.</returns>
        public async Task<bool> Delete(int id)
        {
            var menjador = await _menjadorRepository.GetById(id);

            if (menjador == null)
                return false;

            await _menjadorRepository.Delete(menjador);
            return true;
        }
    }
}