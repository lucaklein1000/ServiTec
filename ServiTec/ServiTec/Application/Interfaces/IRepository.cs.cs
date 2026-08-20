// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        IRepository.cs
// Descripció:    Interfície genèrica que defineix el patró Repositori (Repository
//                Pattern) per a les operacions bàsiques de persistència (CRUD).
// ============================================================================

namespace ServiTec.Application.Interfaces
{
    /// <summary>
    /// Interfície genèrica de repositori per a la gestió d'entitats del domini.
    /// </summary>
    /// <typeparam name="T">Tipus de l'entitat de domini.</typeparam>
    public interface IRepository<T> where T : class
    {
        /// <summary>
        /// Obté totes les instàncies registrades de l'entitat.
        /// </summary>
        /// <returns>Col·lecció de totes les entitats trobades.</returns>
        Task<IEnumerable<T>> GetAll();

        /// <summary>
        /// Cerca una entitat específica pel seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic de l'entitat.</param>
        /// <returns>L'entitat trobada o null si no existeix.</returns>
        Task<T?> GetById(int id);

        /// <summary>
        /// Persisteix una nova entitat al sistema de dades.
        /// </summary>
        /// <param name="entity">Instància de l'entitat a crear.</param>
        /// <returns>L'entitat creada amb el seu identificador assignat.</returns>
        Task<T> Create(T entity);

        /// <summary>
        /// Actualitza l'estat d'una entitat existent a la base de dades.
        /// </summary>
        /// <param name="entity">Instància de l'entitat amb les modificacions.</param>
        Task Update(T entity);

        /// <summary>
        /// Elimina una entitat de la base de dades.
        /// </summary>
        /// <param name="entity">Instància de l'entitat a eliminar.</param>
        Task Delete(T entity);
    }
}