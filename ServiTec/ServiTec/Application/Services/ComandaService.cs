// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ComandaService.cs
// Descripció:    Servei principal de domini encarregat de la gestió completa de
//                comandes (creació, actualització, gestió d'estats de cuina,
//                afegir/restar línies de comanda i cobrament de taules).
// ============================================================================

using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.Interfaces;
using ServiTec.Domain.Models;
using ServiTec.Infrastructure.Data;

namespace ServiTec.Application.Services
{
    /// <summary>
    /// Servei de la capa d'aplicació encarregat de la lògica de negoci de les comandes,
    /// interacció amb la cuina i gestió d'estats de taules.
    /// </summary>
    public class ComandaService
    {
        private readonly IRepository<Comanda> _repository;
        private readonly IRepository<Producte> _productRepository;
        private readonly IRepository<Taula> _taulaRepository;
        private readonly ServiTecDbContext _context;

        public ComandaService(
            IRepository<Comanda> repository,
            IRepository<Producte> productRepository,
            IRepository<Taula> taulaRepository,
            ServiTecDbContext context)
        {
            _repository = repository;
            _productRepository = productRepository;
            _taulaRepository = taulaRepository;
            _context = context;
        }

        /// <summary>
        /// Cerca una comanda pel seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic de la comanda.</param>
        /// <returns>Instància de la comanda o null si no s'ha trobat.</returns>
        public async Task<Comanda?> GetById(int id)
        {
            return await _repository.GetById(id);
        }

        /// <summary>
        /// Elimina una comanda del sistema pel seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic de la comanda a eliminar.</param>
        /// <returns>Cert si s'ha eliminat correctament, o fals si no existia.</returns>
        public async Task<bool> DeleteComanda(int id)
        {
            var comanda = await _repository.GetById(id);

            if (comanda == null)
                return false;

            await _repository.Delete(comanda);
            return true;
        }

        /// <summary>
        /// Obté totes les comandes incloent el detall de les seves línies i productes associats.
        /// </summary>
        /// <returns>Llista de DTOs amb tota la informació resumida de les comandes.</returns>
        public async Task<IEnumerable<ComandaDTO>> GetComandas()
        {
            var query = await _repository.GetAll();

            var comandasConLineas = query
                .AsQueryable()
                .Include(c => c.LiniaComanda)
                    .ThenInclude(lc => lc.IdProducteNavigation)
                .ToList();

            return comandasConLineas.Select(p => new ComandaDTO
            {
                IdComanda = p.IdComanda,
                DataCreacio = p.DataCreacio,
                Estat = p.Estat,
                Total = p.Total,
                IdTaula = p.IdTaula,
                IdUsuari = p.IdUsuari,
                LiniaComanda = p.LiniaComanda.Select(l => new LiniaComandaDTO
                {
                    IdLinia = l.IdLinia,
                    Quantitat = l.Quantitat,
                    PreuUnitari = l.PreuUnitari,
                    Subtotal = l.Subtotal,
                    IdComanda = l.IdComanda,
                    IdProducte = l.IdProducte
                }).ToList()
            }).ToList();
        }

        /// <summary>
        /// Crea una nova comanda, valida l'estat de la taula, obté el preu actual dels productes i ocupa la taula.
        /// </summary>
        /// <param name="dto">Objecte de transferència de dades amb les dades de la comanda i les seves línies.</param>
        /// <returns>La comanda creada amb la seva estructura de línies.</returns>
        /// <exception cref="ArgumentException">Si la taula no existeix.</exception>
        /// <exception cref="InvalidOperationException">Si la taula ja està ocupada.</exception>
        public async Task<Comanda?> CrearComanda(CreateComandaDTO dto)
        {
            var taula = await _taulaRepository.GetById(dto.IdTaula);
            if (taula == null)
            {
                throw new ArgumentException("La mesa especificada no existe.");
            }

            if (!taula.Estat)
            {
                throw new InvalidOperationException("Esta mesa ya tiene una comanda activa.");
            }

            var comanda = new Comanda
            {
                DataCreacio = DateTime.Now,
                Estat = dto.Estat ?? "Pendent",
                IdTaula = dto.IdTaula,
                IdUsuari = dto.IdUsuari,
                Total = 0,
                LiniaComanda = new List<LiniaComanda>()
            };

            decimal granTotal = 0;

            foreach (var liniaDto in dto.Linies)
            {
                var producte = await _productRepository.GetById(liniaDto.IdProducte);

                if (producte != null)
                {
                    decimal preuUnitari = (decimal)producte.Preu;
                    decimal subtotal = preuUnitari * liniaDto.Quantitat;

                    granTotal += subtotal;

                    var novaLinia = new LiniaComanda
                    {
                        Quantitat = liniaDto.Quantitat,
                        PreuUnitari = preuUnitari,
                        Subtotal = subtotal,
                        IdProducte = liniaDto.IdProducte,
                        Estat = liniaDto.Estat,
                        IdCategoria = liniaDto.IdCategoria
                    };

                    comanda.LiniaComanda.Add(novaLinia);
                }
            }

            comanda.Total = granTotal;

            taula.Estat = false;
            await _taulaRepository.Update(taula);

            var resultat = await _repository.Create(comanda);

            return resultat;
        }

        /// <summary>
        /// Actualitza la capçalera d'una comanda existent.
        /// </summary>
        /// <param name="id">Identificador de la comanda a modificar.</param>
        /// <param name="dto">DTO amb les noves dades de la capçalera.</param>
        /// <returns>La comanda actualitzada o null si no existeix.</returns>
        public async Task<Comanda?> UpdateComandaDTO(int id, UpdateComandaDTO dto)
        {
            var comanda = await _repository.GetById(id);

            if (comanda == null)
                return null;

            comanda.IdComanda = dto.IdComanda;
            comanda.DataCreacio = dto.DataCreacio;
            comanda.Estat = dto.Estat;
            comanda.Total = dto.Total;
            comanda.IdTaula = dto.IdTaula;
            comanda.IdUsuari = dto.IdUsuari;

            await _repository.Update(comanda);

            return comanda;
        }

        /// <summary>
        /// Obté la comanda activa (oberta o pendent) associada a un número de taula determinat.
        /// </summary>
        /// <param name="idTaula">Identificador de la taula.</param>
        /// <returns>Instància de la comanda activa amb les seves línies o null si no n'hi ha cap.</returns>
        public async Task<Comanda?> ObtenirComandaActivaSegonsTaulaAsync(int idTaula)
        {
            return await _context.Comanda
                .Include(c => c.LiniaComanda)
                    .ThenInclude(lc => lc.IdProducteNavigation)
                .FirstOrDefaultAsync(c => c.IdTaula == idTaula &&
                                         (c.Estat == "oberta" || c.Estat == "pendent"));
        }

        /// <summary>
        /// Obté el llistat de comandes i comandes de línia destinades a la pantalla de cuina.
        /// </summary>
        /// <returns>Llista de DTOs formatats per al mòdul de cuina.</returns>
        public async Task<List<ComandaCuinaDTO>> ObtenirComandesCuinaAsync()
        {
            return await _context.Comanda
                .Include(c => c.IdTaulaNavigation)
                .Include(c => c.LiniaComanda)
                    .ThenInclude(l => l.IdProducteNavigation)
                .Where(c => c.Estat == "pendent" || c.Estat == "oberta")
                .Where(c => c.LiniaComanda.Any(l => l.Estat == "pendentEnviar"))
                .Select(c => new ComandaCuinaDTO
                {
                    IdComanda = c.IdComanda,
                    IdTaula = c.IdTaula,
                    NumTaula = c.IdTaulaNavigation.Numero,
                    DataHora = c.DataCreacio,
                    Linies = c.LiniaComanda
                        .Where(l => l.Estat == "pendentEnviar")
                        .Select(l => new LiniaCuinaDTO
                        {
                            IdLiniaComanda = l.IdLinia,
                            IdProducte = l.IdProducte,
                            NomProducte = l.IdProducteNavigation != null ? l.IdProducteNavigation.Nom : "Sense nom",
                            Quantitat = l.Quantitat,
                            IdCategoria = l.IdProducteNavigation != null ? l.IdProducteNavigation.IdCategoria : 0
                        }).ToList()
                })
                .ToListAsync();
        }

        /// <summary>
        /// Canvia l'estat d'una comanda específica a la base de dades.
        /// </summary>
        /// <param name="idComanda">Identificador únic de la comanda.</param>
        /// <param name="nouEstat">Nou estat a assignar (ex. 'tancada', 'oberta').</param>
        /// <returns>Cert si s'ha canviat correctament, o fals si la comanda no existia.</returns>
        public async Task<bool> CanviarEstatComandaAsync(int idComanda, string nouEstat)
        {
            var comanda = await _context.Comanda.FindAsync(idComanda);
            if (comanda == null) return false;

            comanda.Estat = nouEstat;
            _context.Comanda.Update(comanda);
            await _context.SaveChangesAsync();

            return true;
        }

        /// <summary>
        /// Canvia l'estat d'una línia de comanda individual.
        /// </summary>
        /// <param name="idLinia">Identificador únic de la línia.</param>
        /// <param name="nouEstat">Nou estat a assignar (ex. 'Servit', 'EnProcés').</param>
        /// <returns>Cert si s'ha actualitzat correctament, o fals si la línia no existia.</returns>
        public async Task<bool> CanviarEstatLiniaAsync(int idLinia, string nouEstat)
        {
            var linia = await _context.LiniaComanda
                .Include(l => l.IdComandaNavigation)
                .FirstOrDefaultAsync(l => l.IdLinia == idLinia);

            if (linia == null) return false;

            linia.Estat = nouEstat;
            _context.LiniaComanda.Update(linia);
            await _context.SaveChangesAsync();

            return true;
        }

        /// <summary>
        /// Processa el cobrament d'una comanda: marca totes les seves línies com a servides, tanca la comanda i allibera la taula.
        /// </summary>
        /// <param name="idComanda">Identificador únic de la comanda a cobrar.</param>
        /// <returns>Cert si tot el procés s'ha realitzat correctament.</returns>
        public async Task<bool> CobrarComandaAsync(int idComanda)
        {
            var comanda = await _context.Comanda
                .Include(c => c.LiniaComanda)
                .Include(c => c.IdTaulaNavigation)
                .FirstOrDefaultAsync(c => c.IdComanda == idComanda);

            if (comanda == null) return false;

            if (comanda.LiniaComanda != null)
            {
                var idsLinies = comanda.LiniaComanda.Select(l => l.IdLinia).ToList();

                foreach (var idLinia in idsLinies)
                {
                    await CanviarEstatLiniaAsync(idLinia, "Servit");
                }
            }

            await CanviarEstatComandaAsync(comanda.IdComanda, "tancada");

            if (comanda.IdTaulaNavigation != null)
            {
                comanda.IdTaulaNavigation.Estat = true;
            }

            await _context.SaveChangesAsync();
            return true;
        }

        /// <summary>
        /// Afegeix un conjunt de noves línies a una comanda ja existent i actualitza el seu total acumulat.
        /// </summary>
        /// <param name="idComanda">Identificador de la comanda a ampliar.</param>
        /// <param name="novesLiniesDto">Llista de noves línies a afegir.</param>
        /// <returns>La comanda actualitzada amb el nou total.</returns>
        /// <exception cref="ArgumentException">Si la comanda no existeix.</exception>
        public async Task<Comanda?> AfegirLiniesAComanda(int idComanda, List<CreateLiniaComandaDTO> novesLiniesDto)
        {
            var comanda = await _context.Comanda.FindAsync(idComanda);
            if (comanda == null)
            {
                throw new ArgumentException("La comanda no existe.");
            }

            decimal totalAdicional = 0;
            var novesEntitatsLinia = new List<LiniaComanda>();

            foreach (var liniaDto in novesLiniesDto)
            {
                var producte = await _productRepository.GetById(liniaDto.IdProducte);
                if (producte != null)
                {
                    decimal preuUnitari = (decimal)producte.Preu;
                    decimal subtotal = preuUnitari * liniaDto.Quantitat;

                    totalAdicional += subtotal;

                    novesEntitatsLinia.Add(new LiniaComanda
                    {
                        IdComanda = idComanda,
                        IdProducte = liniaDto.IdProducte,
                        Quantitat = liniaDto.Quantitat,
                        PreuUnitari = preuUnitari,
                        Subtotal = subtotal,
                        Estat = liniaDto.Estat ?? "Pendent"
                    });
                }
            }

            await _context.LiniaComanda.AddRangeAsync(novesEntitatsLinia);

            comanda.Total += totalAdicional;

            await _context.SaveChangesAsync();

            return comanda;
        }

        /// <summary>
        /// Elimina o redueix en una unitat la quantitat d'una línia de comanda, recalculant el total de la comanda pare.
        /// </summary>
        /// <param name="idLiniaComanda">Identificador únic de la línia de comanda.</param>
        /// <returns>Cert si la línia s'ha modificat/eliminat correctament.</returns>
        public async Task<bool> EliminarLiniaComandaAsync(int idLiniaComanda)
        {
            var linia = await _context.LiniaComanda
                .Include(l => l.IdComandaNavigation)
                .FirstOrDefaultAsync(l => l.IdLinia == idLiniaComanda);

            if (linia == null)
            {
                return false;
            }

            if (linia.Quantitat <= 1)
            {
                linia.Estat = "Eliminat";
                linia.PreuUnitari = 0;
                linia.Subtotal = 0;
                linia.Quantitat = 0;
            }
            else
            {
                linia.Quantitat -= 1;
                linia.Subtotal = linia.Quantitat * linia.PreuUnitari;
            }

            var comandaPare = linia.IdComandaNavigation;
            if (comandaPare != null)
            {
                var totalActiu = await _context.LiniaComanda
                    .Where(l => l.IdComanda == comandaPare.IdComanda && l.Estat != "Eliminat")
                    .SumAsync(l => l.Subtotal);

                comandaPare.Total = totalActiu;
            }

            await _context.SaveChangesAsync();
            return true;
        }
    }
}