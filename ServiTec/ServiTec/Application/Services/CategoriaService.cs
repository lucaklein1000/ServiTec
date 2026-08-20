// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        CategoriaService.cs
// Descripció:    Servei de domini encarregat de la gestió del catàleg de
//                categories de productes (creació, consulta, modificació i
//                eliminació de la carta del restaurant).
// ============================================================================

using ServiTec.Application.DTOs;
using ServiTec.Application.Interfaces;
using ServiTec.Domain.Models;

namespace ServiTec.Application.Services
{
    /// <summary>
    /// Servei encarregat de gestionar les operacions de la base de dades
    /// associades a les categories de productes del restaurant.
    /// </summary>
    public class CategoriaService
    {
        private readonly IRepository<Categoria> _repository;

        public CategoriaService(IRepository<Categoria> repository)
        {
            _repository = repository;
        }

        /// <summary>
        /// Cerca una categoria pel seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic de la categoria.</param>
        /// <returns>Instància de la categoria trobada o null si no existeix.</returns>
        public async Task<Categoria?> GetById(int id)
        {
            return await _repository.GetById(id);
        }

        /// <summary>
        /// Elimina una categoria del sistema pel seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic de la categoria a eliminar.</param>
        /// <returns>Cert si s'ha eliminat correctament, o fals si no existia.</returns>
        public async Task<bool> DeleteCategoria(int id)
        {
            var categoria = await _repository.GetById(id);

            if (categoria == null)
                return false;

            await _repository.Delete(categoria);
            return true;
        }

        /// <summary>
        /// Obté el llistat complet de categories registrades al sistema.
        /// </summary>
        /// <returns>Col·lecció de DTOs amb la informació bàsica de les categories.</returns>
        public async Task<IEnumerable<CategoriaDTO>> GetCategorias()
        {
            var categorias = await _repository.GetAll();
            return categorias.Select(p => new CategoriaDTO
            {
                IdCategoria = p.IdCategoria,
                Nom = p.Nom,
                Descripcio = p.Descripcio
            }).ToList();
        }

        /// <summary>
        /// Crea una nova categoria al catàleg del restaurant.
        /// </summary>
        /// <param name="dto">Objecte de transferència de dades amb la informació de la nova categoria.</param>
        /// <returns>La categoria creada amb el seu identificador assignat.</returns>
        public async Task<Categoria?> CrearCategoria(CreateCategoriaDTO dto)
        {
            var categoria = new Categoria
            {
                Nom = dto.Nom,
                Descripcio = dto.Descripcio,
                IdCategoria = dto.IdCategoria
            };

            var resultat = await _repository.Create(categoria);

            return resultat;
        }

        /// <summary>
        /// Modifica el nom i la descripció d'una categoria existent.
        /// </summary>
        /// <param name="id">Identificador únic de la categoria a modificar.</param>
        /// <param name="dto">DTO amb les noves dades actualitzades.</param>
        /// <returns>La categoria actualitzada o null si no s'ha trobat.</returns>
        public async Task<Categoria?> UpdateCategoriaDTO(int id, UpdateCategoriaDTO dto)
        {
            var categoria = await _repository.GetById(id);

            if (categoria == null)
                return null;

            categoria.Nom = dto.Nom;
            categoria.Descripcio = dto.Descripcio;

            await _repository.Update(categoria);

            return categoria;
        }
    }
}