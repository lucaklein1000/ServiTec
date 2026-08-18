using ServiTec.Domain.Models;
using ServiTec.Application.DTOs;

public class UsuariService
{
    private readonly IRepository<Usuari> _repository;

    public UsuariService(IRepository<Usuari> repository)
    {
        _repository = repository;
    }

    public async Task<IEnumerable<Usuari>> GetAll()
    {
        return await _repository.GetAll();
    }

    public async Task<Usuari?> GetById(int id)
    {
        return await _repository.GetById(id);
    }

    public async Task<Usuari?> GetByNomUsuari(string nomUsuari)
    {
        var usuaris = await _repository.GetAll();
        return usuaris.FirstOrDefault(u => u.NomUsuari == nomUsuari);
    }

    public async Task<Usuari> Create(CreateUsuariDTO dto)
    {
        var usuari = new Usuari
        {
            NomUsuari = dto.PostNomUsuari,
            Contrasenya = dto.PostContrasenya = BCrypt.Net.BCrypt.HashPassword(dto.PostContrasenya),
            Actiu = dto.PostActiu,
            Admin = dto.PostAdmin,
            Rol = dto.PostRol
        };

        return await _repository.Create(usuari);
    }

    public async Task<Usuari?> Update(int id, UpdateUsuariDTO dto)
    {
        var usuari = await _repository.GetById(id);
        if (usuari == null) return null;

        usuari.NomUsuari = dto.PutNomUsuari;
        usuari.Rol = dto.PutRol;
        usuari.Actiu = dto.PutActiu;
        usuari.Admin = dto.PutAdmin;

        // Solo si el administrador escribió una nueva contraseña la hasheamos y actualizamos
        if (!string.IsNullOrWhiteSpace(dto.PutContrasenya))
        {
            usuari.Contrasenya = BCrypt.Net.BCrypt.HashPassword(dto.PutContrasenya);
        }

        await _repository.Update(usuari);

        return usuari;
    }

    public async Task<bool> Delete(int id)
    {
        var usuari = await _repository.GetById(id);

        if (usuari == null)
            return false;

        await _repository.Delete(usuari);
        return true;
    }

    public async Task<Usuari?> ValidarLogin(string nomUsuari, string contrasenya)
    {
        var usuari = await GetByNomUsuari(nomUsuari);
        if (usuari == null) return null;

        if (!BCrypt.Net.BCrypt.Verify(contrasenya, usuari.Contrasenya))
        {
            return null;
        }

        return usuari;
    }
}