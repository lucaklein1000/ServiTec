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

    public async Task<Usuari> Create(CreateUsuariDTO dto)
    {
        var usuari = new Usuari
        {
            NomUsuari = dto.PostNomUsuari,
            Contrasenya = dto.PostContrasenya,
            Actiu = dto.PostActiu,
            Admin = dto.PostAdmin
        };

        return await _repository.Create(usuari);
    }

    public async Task<Usuari?> Update(int id, UpdateUsuariDTO dto)
    {
        var usuari = await _repository.GetById(id);

        if (usuari == null)
            return null;

        usuari.NomUsuari = dto.PutNomUsuari;
        usuari.Contrasenya = dto.PutContrasenya;
        usuari.Actiu = dto.PutActiu;
        usuari.Admin = dto.PutAdmin;

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
}