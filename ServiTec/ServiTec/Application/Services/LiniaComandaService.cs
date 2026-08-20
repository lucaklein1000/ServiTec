// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        LiniaComandaService.cs
// Descripció:    Servei de domini encarregat de gestionar les línies de detall
//                de les comandes (afegir/modificar/eliminar plats o begudes,
//                gestió d'estats de cuina i recalcul automàtic del total).
// ============================================================================

using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.Interfaces;
using ServiTec.Domain.Models;

namespace ServiTec.Services
{
    /// <summary>
    /// Servei encarregat de gestionar les operacions de la base de dades
    /// associades als elements individuals (línies) continguts a cada comanda.
    /// </summary>
    public class LiniaComandaService
    {
        private readonly IRepository<LiniaComanda> _repository;
        private readonly IRepository<Producte> _productRepository;
        private readonly IRepository<Comanda> _comandaRepository;

        public LiniaComandaService(
            IRepository<LiniaComanda> repository,
            IRepository<Producte> productRepository,
            IRepository<Comanda> comandaRepository)
        {
            _repository = repository;
            _productRepository = productRepository;
            _comandaRepository = comandaRepository;
        }

        /// <summary>
        /// Obté la llista completa de línies de comanda registrades al sistema.
        /// </summary>
        /// <returns>Col·lecció de DTOs amb la informació de cada línia.</returns>
        public async Task<IEnumerable<LiniaComandaDTO>> GetAll()
        {
            var liniaComandas = await _repository.GetAll();
            return liniaComandas.Select(p => new LiniaComandaDTO
            {
                IdLinia = p.IdLinia,
                Quantitat = p.Quantitat,
                PreuUnitari = p.PreuUnitari,
                Subtotal = p.Subtotal,
                IdComanda = p.IdComanda,
                IdProducte = p.IdProducte,
                Estat = p.Estat,
                IdCategoria = p.IdCategoria ?? p.IdProducteNavigation?.IdCategoria
            }).ToList();
        }

        /// <summary>
        /// Obté una línia de comanda específica pel seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic de la línia de comanda.</param>
        /// <returns>L'entitat de la línia trobada o null si no existeix.</returns>
        public async Task<LiniaComanda?> GetById(int id)
        {
            return await _repository.GetById(id);
        }

        /// <summary>
        /// Crea una nova línia de comanda, assigna el preu actual del producte i recalcula el total de la comanda mare.
        /// </summary>
        /// <param name="dto">Objecte de transferència de dades amb les dades del producte i quantitat.</param>
        /// <returns>El DTO de la línia creada o null si el producte no existeix.</returns>
        public async Task<LiniaComandaDTO?> Create(CreateLiniaComandaDTO dto)
        {
            var producte = await _productRepository.GetById(dto.IdProducte);
            if (producte == null) return null;

            decimal preuUnitari = (decimal)producte.Preu;

            var nuevaLinia = new LiniaComanda
            {
                IdComanda = dto.IdComanda,
                IdProducte = dto.IdProducte,
                Quantitat = dto.Quantitat,
                PreuUnitari = preuUnitari,
                Subtotal = preuUnitari * dto.Quantitat,
                Estat = "Pendent",
                IdCategoria = dto.IdCategoria ?? producte.IdCategoria
            };

            var resultat = await _repository.Create(nuevaLinia);

            // Sincronitzem el total acumulat de la comanda principal
            await ActualitzarTotalComanda(resultat.IdComanda);

            return new LiniaComandaDTO
            {
                IdLinia = resultat.IdLinia,
                Quantitat = resultat.Quantitat,
                PreuUnitari = resultat.PreuUnitari,
                Subtotal = resultat.Subtotal,
                IdComanda = resultat.IdComanda,
                IdProducte = resultat.IdProducte,
                Estat = resultat.Estat,
                IdCategoria = resultat.IdCategoria
            };
        }

        /// <summary>
        /// Modifica la quantitat d'un element d'una comanda i actualitza el subtotal i el total global.
        /// </summary>
        /// <param name="id">Identificador únic de la línia a modificar.</param>
        /// <param name="dto">Objecte de transferència de dades amb la nova quantitat.</param>
        /// <returns>Cert si s'ha actualitzat correctament, o fals si no existia.</returns>
        public async Task<bool> Update(int id, UpdateLiniaComandaDTO dto)
        {
            var linia = await _repository.GetById(id);
            if (linia == null) return false;

            linia.Quantitat = dto.Quantitat;
            linia.Subtotal = linia.Quantitat * linia.PreuUnitari;

            await _repository.Update(linia);

            // Recalculem l'import total de la comanda
            await ActualitzarTotalComanda(linia.IdComanda);
            return true;
        }

        /// <summary>
        /// Elimina un element d'una comanda i recalcula l'import total resultant.
        /// </summary>
        /// <param name="id">Identificador únic de la línia a eliminar.</param>
        /// <returns>Cert si s'ha eliminat correctament, o fals si no existia.</returns>
        public async Task<bool> Delete(int id)
        {
            var linia = await _repository.GetById(id);
            if (linia == null) return false;

            int idComanda = linia.IdComanda;

            await _repository.Delete(linia);

            // Reajustem el total de la comanda un cop extreta la línia
            await ActualitzarTotalComanda(idComanda);
            return true;
        }

        /// <summary>
        /// Mètode privat encarregat de recalcular el sumatori de subtotals i actualitzar el camp Total de la comanda.
        /// </summary>
        /// <param name="idComanda">Identificador únic de la comanda a recalcular.</param>
        private async Task ActualitzarTotalComanda(int idComanda)
        {
            var comanda = await _comandaRepository.GetById(idComanda);

            if (comanda != null)
            {
                var totesLesLinies = await _repository.GetAll();

                comanda.Total = totesLesLinies
                    .Where(l => l.IdComanda == idComanda)
                    .Sum(l => l.Subtotal);

                await _comandaRepository.Update(comanda);
            }
        }
    }
}