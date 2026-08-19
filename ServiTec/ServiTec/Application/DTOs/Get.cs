using Microsoft.Identity.Client;

namespace ServiTec.Application.DTOs
{
    public class LiniaComandaDTO
    {
        public int IdLinia { get; set; }
        public int Quantitat { get; set; }
        public decimal PreuUnitari { get; set; }
        public decimal Subtotal { get; set; }
        public int IdComanda { get; set; }
        public int IdProducte { get; set; }
        public string Estat { get; set; } = null!;
        public int? IdCategoria { get; set; }
    }

    public class ComandaDTO
    {
        public int IdComanda { get; set; }

        public DateTime DataCreacio { get; set; }

        public string Estat { get; set; } = null!;

        public decimal Total { get; set; }

        public int IdTaula { get; set; }

        public int IdUsuari { get; set; }

        public List<LiniaComandaDTO> LiniaComanda { get; set; } = new();
    }

    public class CategoriaDTO
    {
        public int IdCategoria { get; set; }

        public string Nom { get; set; } = null!;

        public string? Descripcio { get; set; }
    }

    public class ProducteDTO
    {
        public int IdProducte { get; set; }
        public string Nom { get; set; }
        public string Descripcio { get; set; }
        public decimal Preu { get; set; }
        public int IdCategoria { get; set; }
    }

    public class UsuariDTO
    {
        public int IdUsuari { get; set; }
        public string nomUsuari { get; set; }
        public bool Actiu { get; set; }
        public bool Admin { get; set; }
        public string Rol { get; set; } = null!;

    }

    public class TaulaDTO
    {
        public int IdTaula { get; set; }

        public int Numero { get; set; }

        public int Capacitat { get; set; }

        public bool Estat { get; set; }
        public string? EstatComanda { get; set; }
        public int IdMenjador { get; set; }
        public double PosX { get; set; }
        public double PosY { get; set; }
    }

    public class ComandaCuinaDTO
    {
        public int IdComanda { get; set; }
        public int IdTaula { get; set; }
        public int NumTaula { get; set; }
        public DateTime DataHora { get; set; }
        public List<LiniaCuinaDTO> Linies { get; set; } = new();
    }

    public class LiniaCuinaDTO
    {
        public int IdLiniaComanda { get; set; }
        public int IdProducte { get; set; }
        public int Quantitat { get; set; }
        public int IdCategoria { get; set; } 
        public string NomProducte { get; set; } = null!;
    }

    public class MenjadorDTO
    {
        public int IdMenjador { get; set; }
        public string NomMenjador { get; set; } = null!;
        public bool Actiu { get; set; }
        public List<TaulaDTO> Taules { get; set; } = new();
    }
}