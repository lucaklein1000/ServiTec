// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ProducteService.cs
// Descripció:    Servei de domini encarregat de gestionar la lògica de negoci
//                dels productes de la carta del restaurant (CRUD, consulta de
//                català i transformació a DTOs).
// ============================================================================

using ServiTec.Application.DTOs;
using ServiTec.Application.Interfaces;
using ServiTec.Domain.Models;

namespace ServiTec.Application.Services
{
    /// <summary>
    /// Servei encarregat de gestionar les operacions de la base de dades
    /// associades als productes comercialitzats al restaurant.
    /// </summary>
    public class ProducteService
    {
        private readonly IRepository<Producte> _repository;
        private readonly IRepository<Taula> _taulaRepository;

        public ProducteService(IRepository<Producte> repository, IRepository<Taula> taulaRepository)
        {
            _repository = repository;
            _taulaRepository = taulaRepository;
        }

        /// <summary>
        /// Obté un producte específic a partir del seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic del producte.</param>
        /// <returns>L'entitat del producte trobat o null si no existeix.</returns>
        public async Task<Producte?> GetById(int id)
        {
            return await _repository.GetById(id);
        }

        /// <summary>
        /// Desactiva un producte del sistema (esborrat lògic) si no hi ha cap taula oberta.
        /// </summary>
        /// <param name="id">Identificador únic del producte a desactivar.</param>
        /// <returns>Cert si s'ha desactivat correctament.</returns>
        /// <exception cref="InvalidOperationException">Si hi ha taules obertes al sistema.</exception>
        public async Task<bool> DeleteProducte(int id)
        {
            // 1. Comprovar si hi ha alguna taula oberta
            var taules = await _taulaRepository.GetAll();
            var taulesObertes = taules.Where(t => t.Estat == false);
            if (taulesObertes.Any())
            {
                throw new InvalidOperationException("Hi ha taules obertes. No pots eliminar productes amb taules obertes.");
            }

            // 2. Cercar el producte
            var producte = await _repository.GetById(id);

            if (producte == null)
                return false;

            // 3. Esborrat lògic: canviar estat a inactiu
            producte.Actiu = false;

            await _repository.Update(producte);
            return true;
        }

        /// <summary>
        /// Obté la llista completa de productes i els mapeja cap a la seva representació DTO.
        /// </summary>
        /// <returns>Col·lecció de DTOs amb la informació de tots els productes.</returns>
        public async Task<IEnumerable<ProducteDTO>> GetProductes()
        {
            var productes = await _repository.GetAll();
            return productes.Select(p => new ProducteDTO
            {
                IdProducte = p.IdProducte,
                Nom = p.Nom,
                Descripcio = p.Descripcio,
                Preu = p.Preu,
                Actiu = p.Actiu,
                IdCategoria = p.IdCategoria
            }).ToList();
        }

        /// <summary>
        /// Crea un nou producte a la carta a partir de les dades subministrades.
        /// </summary>
        /// <param name="dto">Objecte de transferència de dades amb la informació del nou producte.</param>
        /// <returns>L'entitat del producte creat.</returns>
        public async Task<Producte?> CrearProducte(CreateProducteDTO dto)
        {
            var producte = new Producte
            {
                Nom = dto.Nom,
                Descripcio = dto.Descripcio,
                Preu = dto.Preu,
                Actiu = dto.Actiu,
                IdCategoria = dto.IdCategoria
            };

            var resultat = await _repository.Create(producte);

            return resultat;
        }

        /// <summary>
        /// Actualitza la informació d'un producte existent.
        /// </summary>
        /// <param name="id">Identificador únic del producte a modificar.</param>
        /// <param name="dto">Objecte de transferència de dades amb les modificacions.</param>
        /// <returns>El producte actualitzat o null si no s'ha trobat.</returns>
        public async Task<Producte?> UpdateProducteDTO(int id, UpdateProducteDTO dto)
        {
            var producte = await _repository.GetById(id);

            if (producte == null)
                return null;

            producte.Nom = dto.Nom;
            producte.Descripcio = dto.Descripcio;
            producte.Preu = dto.Preu;
            producte.Actiu = dto.Actiu;
            producte.IdCategoria = dto.IdCategoria;

            await _repository.Update(producte);

            return producte;
        }
    }
}