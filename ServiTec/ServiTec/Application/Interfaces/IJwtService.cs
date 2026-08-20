using ServiTec.Domain.Models;

namespace ServiTec.Application.Interfaces
{
    public interface IJwtService
    {
        string GenerarToken(Usuari usuari);
    }
}
