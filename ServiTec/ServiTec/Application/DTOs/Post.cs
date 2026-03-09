namespace ServiTec.Application.DTOs
{
    public class CreateProducteDTO
    {
        public string PostNom { get; set; } = null!;
        public string? PostDescripcio { get; set; }
        public decimal PostPreu { get; set; }
        public bool PostActiu { get; set; }
        public int PostIdCategoria { get; set; }
    }
}