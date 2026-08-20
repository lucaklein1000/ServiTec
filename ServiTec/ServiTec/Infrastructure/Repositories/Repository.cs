// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Repository.cs
// Descripció:    Implementació genèrica del repositori (Repository Pattern)
//                utilitzant Entity Framework Core per a l'accés a la base de dades.
// ============================================================================

using Microsoft.EntityFrameworkCore;
using ServiTec.Application.Interfaces;
using ServiTec.Infrastructure.Data;

namespace ServiTec.Infrastructure.Repositories
{
    /// <summary>
    /// Implementació concreta de la interfície de repositori genèric <see cref="IRepository{T}"/>
    /// encarregada de realitzar les operacions CRUD directes contra Entity Framework.
    /// </summary>
    /// <typeparam name="T">Tipus de l'entitat de domini.</typeparam>
    public class Repository<T> : IRepository<T> where T : class
    {
        private readonly ServiTecDbContext _context;
        private readonly DbSet<T> _dbSet;

        public Repository(ServiTecDbContext context)
        {
            _context = context;
            _dbSet = context.Set<T>();
        }

        /// <summary>
        /// Obté totes les instàncies de l'entitat des de la base de dades de forma asíncrona.
        /// </summary>
        /// <returns>Col·lecció de totes les entitats trobades.</returns>
        public async Task<IEnumerable<T>> GetAll()
        {
            return await _dbSet.ToListAsync();
        }

        /// <summary>
        /// Cerca una entitat específica per la seva clau primària.
        /// </summary>
        /// <param name="id">Identificador únic de l'entitat.</param>
        /// <returns>L'entitat trobada o null si no existeix.</returns>
        public async Task<T?> GetById(int id)
        {
            return await _dbSet.FindAsync(id);
        }

        /// <summary>
        /// Afegeix una nova entitat al conjunt de dades i guarda els canvis a la base de dades.
        /// </summary>
        /// <param name="entity">Instància de l'entitat a crear.</param>
        /// <returns>L'entitat creada amb el seu identificador assignat.</returns>
        public async Task<T> Create(T entity)
        {
            _dbSet.Add(entity);
            await _context.SaveChangesAsync();
            return entity;
        }

        /// <summary>
        /// Marca l'entitat com a modificada i persisteix els canvis a la base de dades.
        /// </summary>
        /// <param name="entity">Instància de l'entitat amb les dades actualitzades.</param>
        public async Task Update(T entity)
        {
            _dbSet.Update(entity);
            await _context.SaveChangesAsync();
        }

        /// <summary>
        /// Elimina la registre de l'entitat de la base de dades.
        /// </summary>
        /// <param name="entity">Instància de l'entitat a eliminar.</param>
        public async Task Delete(T entity)
        {
            _dbSet.Remove(entity);
            await _context.SaveChangesAsync();
        }
    }
}