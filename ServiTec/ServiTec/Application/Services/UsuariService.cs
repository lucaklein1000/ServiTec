// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        UsuariService.cs
// Descripció:    Servei de domini encarregat de gestionar la lògica de negoci
//                dels usuaris del sistema (CRUD, encriptació de contrasenyes
//                i verificació d'autenticació/login).
// ============================================================================

using ServiTec.Application.DTOs;
using ServiTec.Application.Interfaces;
using ServiTec.Domain.Models;

namespace ServiTec.Application.Services
{
    /// <summary>
    /// Servei encarregat de gestionar l'accés a dades i les regles de negoci
    /// associades als usuaris de l'aplicació.
    /// </summary>
    public class UsuariService
    {
        private readonly IRepository<Usuari> _repository;

        public UsuariService(IRepository<Usuari> repository)
        {
            _repository = repository;
        }

        /// <summary>
        /// Obté la llista completa d'usuaris registrats al sistema.
        /// </summary>
        /// <returns>Col·lecció de tots els usuaris existents.</returns>
        public async Task<IEnumerable<Usuari>> GetAll()
        {
            return await _repository.GetAll();
        }

        /// <summary>
        /// Obté un usuari específic a partir del seu identificador únic.
        /// </summary>
        /// <param name="id">Identificador únic de l'usuari.</param>
        /// <returns>L'usuari trobat o null si no existeix.</returns>
        public async Task<Usuari?> GetById(int id)
        {
            return await _repository.GetById(id);
        }

        /// <summary>
        /// Cerca un usuari pel seu nom d'usuari (username).
        /// </summary>
        /// <param name="nomUsuari">Nom d'usuari a cercar.</param>
        /// <returns>L'usuari corresponent al nom o null si no existeix.</returns>
        public async Task<Usuari?> GetByNomUsuari(string nomUsuari)
        {
            var usuaris = await _repository.GetAll();
            return usuaris.FirstOrDefault(u => u.NomUsuari == nomUsuari);
        }

        /// <summary>
        /// Crea un nou usuari a la base de dades aplicant encriptació a la contrasenya.
        /// </summary>
        /// <param name="dto">Objecte de transferència de dades amb la informació del nou usuari.</param>
        /// <returns>L'entitat de l'usuari creat.</returns>
        public async Task<Usuari> Create(CreateUsuariDTO dto)
        {
            // Encriptem la contrasenya en text pla utilitzant l'algorisme BCrypt abans de persistir
            var contrasenyaXifrada = BCrypt.Net.BCrypt.HashPassword(dto.Contrasenya);

            var usuari = new Usuari
            {
                NomUsuari = dto.NomUsuari,
                Contrasenya = contrasenyaXifrada,
                Actiu = dto.Actiu,
                Admin = dto.Admin,
                Rol = dto.Rol
            };

            return await _repository.Create(usuari);
        }

        /// <summary>
        /// Actualitza les dades d'un usuari existent.
        /// </summary>
        /// <param name="id">Identificador únic de l'usuari a modificar.</param>
        /// <param name="dto">Objecte de transferència de dades amb les modificacions.</param>
        /// <returns>L'usuari actualitzat o null si no s'ha trobat.</returns>
        public async Task<Usuari?> Update(int id, UpdateUsuariDTO dto)
        {
            var usuari = await _repository.GetById(id);
            if (usuari == null) return null;

            usuari.NomUsuari = dto.NomUsuari;
            usuari.Rol = dto.Rol;
            usuari.Actiu = dto.Actiu;
            usuari.Admin = dto.Admin;

            // Només si l'administrador ha introduït una nova contrasenya la rehashagem i actualitzem
            if (!string.IsNullOrWhiteSpace(dto.Contrasenya))
            {
                usuari.Contrasenya = BCrypt.Net.BCrypt.HashPassword(dto.Contrasenya);
            }

            await _repository.Update(usuari);
            return usuari;
        }

        /// <summary>
        /// Elimina un usuari del sistema a partir del seu identificador.
        /// </summary>
        /// <param name="id">Identificador únic de l'usuari a eliminar.</param>
        /// <returns>Cert si s'ha eliminat correctament, o fals si no existia.</returns>
        public async Task<bool> Delete(int id)
        {
            var usuari = await _repository.GetById(id);
            if (usuari == null) return false;

            await _repository.Delete(usuari);
            return true;
        }

        /// <summary>
        /// Valida les credencials d'accés d'un usuari i comprova que el seu compte estigui actiu.
        /// </summary>
        /// <param name="nomUsuari">Nom d'usuari introduït.</param>
        /// <param name="contrasenya">Contrasenya en text pla a verificar.</param>
        /// <returns>L'instància de l'usuari si la validació és correcta; en cas contrari, null.</returns>
        public async Task<Usuari?> ValidarLogin(string nomUsuari, string contrasenya)
        {
            var usuari = await GetByNomUsuari(nomUsuari);
            if (usuari == null) return null;

            // Comprovar si el compte de l'usuari està deshabilitat
            if (!usuari.Actiu) return null;

            // Comparar el hash guardat amb la contrasenya introduïda
            if (!BCrypt.Net.BCrypt.Verify(contrasenya, usuari.Contrasenya))
            {
                return null;
            }

            return usuari;
        }
    }
}