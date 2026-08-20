using ServiTec.Application.DTOs;
using ServiTec.Application.Interfaces;

namespace ServiTec.Application.Services;

public class AuthService
{
    private readonly UsuariService _usuariService;
    private readonly IJwtService _jwtService;

    public AuthService(UsuariService usuariService, IJwtService jwtService)
    {
        _usuariService = usuariService;
        _jwtService = jwtService;
    }

    public async Task<LoginResponseDTO?> AutenticarAsync(LoginRequest dto)
    {
        // 1. Validar las credenciales
        var usuari = await _usuariService.ValidarLogin(dto.NomUsuari, dto.Contrasenya);
        if (usuari == null || !usuari.Actiu)
        {
            return null;
        }

        // 2. Generar el Token
        var token = _jwtService.GenerarToken(usuari);

        // 3. Mapear a DTO
        return new LoginResponseDTO
        {
            Token = token,
            NomUsuari = usuari.NomUsuari,
            Rol = usuari.Rol
        };
    }
}