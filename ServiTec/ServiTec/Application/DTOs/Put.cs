namespace ServiTec.Application.DTOs
{
    public class ActualitzarProducte
    {
        public int PutIdProducte { get; set; }
        public string PutNom { get; set; } = null!;
        public string? PutDescripcio { get; set; }
        public decimal PutPreu { get; set; }
        public bool PutActiu { get; set; }
        public int PutIdCategoria { get; set; }
    }
}