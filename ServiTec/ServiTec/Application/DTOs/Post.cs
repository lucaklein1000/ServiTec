using ServiTec.Application.DTOs;
using System.Text.Json.Serialization;

namespace ServiTec.Application.DTOs
{
    public class CreateLiniaComandaDTO
    {
        [JsonPropertyName("PostIdComanda")]
        public int PostIdComanda { get; set; }

        [JsonPropertyName("PostIdProducte")]
        public int PostIdProducte { get; set; }

        [JsonPropertyName("PostQuantitat")]
        public int PostQuantitat { get; set; }
        public string PostEstat { get; set; } = null!;
        public int? PostIdCategoria { get; set; }
    }

    public class CreateComandaDTO
    {
        public string PostEstat { get; set; } = null!;
        public int PostIdTaula { get; set; }
        public int PostIdUsuari { get; set; }
        [JsonPropertyName("postLinies")]
        public List<CreateLiniaComandaDTO> PostLinies { get; set; } = new List<CreateLiniaComandaDTO>();
    }

    public class CreateCategoriaDTO
    {
        public int PostIdCategoria { get; set; }

        public string PostNom { get; set; } = null!;

        public string? PostDescripcio { get; set; }
    }

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
        public string PostRol { get; set; } = null!;
    }

    public class CreateTaulaDTO
    {
        public int PostNumero { get; set; }
        public int PostCapacitat { get; set; }
        public bool PostEstat { get; set; }
        public string? PostEstatComanda { get; set; }
        public int PostIdMenjador { get; set; }
        public int PostPosX { get; set; }
        public int PostPosY { get; set; }
    }

    public class CreateMenjadorDTO
    {
        public string PostNomMenjador { get; set; } = null!;
        public bool PostActiu { get; set; }
        public List<CreateTaulaDTO> PostTaules { get; set; } = new List<CreateTaulaDTO>();
    }
}