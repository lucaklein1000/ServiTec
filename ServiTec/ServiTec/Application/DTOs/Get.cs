using Microsoft.Identity.Client;

namespace ServiTec.Application.DTOs
{
    public class ProducteDTO
    {
        public int IdProducte { get; set; }
        public string Nom { get; set; }
        public decimal Preu { get; set; }
    }

    public class UsuariDTO
    {
        public int IdUsuari { get; set; }
        public string nomUsuari { get; set; }
        public string contrasenya { get; set; }
        public bool Actiu { get; set; }
        public bool Admin { get; set; }
    }
}