// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        ReadDTOs.cs
// Descripció:    Col·lecció de DTOs utilitzats per a la consulta, lectura i
//                retorn de dades des de l'API cap als clients (Android / Web).
// ============================================================================

namespace ServiTec.Application.DTOs
{
    /// <summary>
    /// Objecte de transferència de dades per a la lectura d'una línia de comanda.
    /// </summary>
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

    /// <summary>
    /// Objecte de transferència de dades amb la informació completa d'una comanda i les seves línies.
    /// </summary>
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

    /// <summary>
    /// Objecte de transferència de dades per a la visualització de categories de la carta.
    /// </summary>
    public class CategoriaDTO
    {
        public int IdCategoria { get; set; }
        public string Nom { get; set; } = null!;
        public string? Descripcio { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades per a la informació detallada d'un producte.
    /// </summary>
    public class ProducteDTO
    {
        public int IdProducte { get; set; }
        public string Nom { get; set; } = string.Empty;
        public string Descripcio { get; set; } = string.Empty;
        public decimal Preu { get; set; }
        public int IdCategoria { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades amb les dades públiques i rol d'un usuari.
    /// </summary>
    public class UsuariDTO
    {
        public int IdUsuari { get; set; }
        public string NomUsuari { get; set; } = string.Empty;
        public bool Actiu { get; set; }
        public bool Admin { get; set; }
        public string Rol { get; set; } = null!;
    }

    /// <summary>
    /// Objecte de transferència de dades per a l'estat i posició d'una taula al plànol del menjador.
    /// </summary>
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

    /// <summary>
    /// DTO optimitzat per a la visualització de comandes a la pantalla de cuina.
    /// </summary>
    public class ComandaCuinaDTO
    {
        public int IdComanda { get; set; }
        public int IdTaula { get; set; }
        public int NumTaula { get; set; }
        public DateTime DataHora { get; set; }
        public List<LiniaCuinaDTO> Linies { get; set; } = new();
    }

    /// <summary>
    /// DTO amb el detall individual del producte pendent de preparació a cuina.
    /// </summary>
    public class LiniaCuinaDTO
    {
        public int IdLiniaComanda { get; set; }
        public int IdProducte { get; set; }
        public int Quantitat { get; set; }
        public int IdCategoria { get; set; }
        public string NomProducte { get; set; } = null!;
    }

    /// <summary>
    /// Objecte de transferència de dades per a un menjador i la seva col·lecció de taules.
    /// </summary>
    public class MenjadorDTO
    {
        public int IdMenjador { get; set; }
        public string NomMenjador { get; set; } = null!;
        public bool Actiu { get; set; }
        public List<TaulaDTO> Taules { get; set; } = new();
    }
}