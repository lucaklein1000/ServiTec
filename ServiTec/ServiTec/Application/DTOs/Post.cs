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

    public class CreateUsuariDTO
        {
            public string PostNomUsuari { get; set; } = null!;
            public string PostContrasenya { get; set; } = null!;
            public bool PostActiu { get; set; }
            public bool PostAdmin { get; set; }
    }
}