namespace ServiTec.Application.DTOs
{
    public class LoginResponseDTO
    {
        public string Token { get; set; } = string.Empty;
        public string NomUsuari { get; set; } = string.Empty;
        public string Rol { get; set; } = string.Empty;
    }
}
