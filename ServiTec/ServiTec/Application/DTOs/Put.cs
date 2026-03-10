namespace ServiTec.Application.DTOs
{
    public class UpdateProducteDTO
    {
        public int PutIdProducte { get; set; }
        public string PutNom { get; set; } = null!;
        public string? PutDescripcio { get; set; }
        public decimal PutPreu { get; set; }
        public bool PutActiu { get; set; }
        public int PutIdCategoria { get; set; }
    }

    public class UpdateUsuariDTO
        {
            public int PutIdUsuari { get; set; }
            public string PutNomUsuari { get; set; } = null!;
            public string PutContrasenya { get; set; } = null!;
            public bool PutActiu { get; set; }
            public bool PutAdmin { get; set; }
    }
}