// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        UpdateDTOs.cs
// Descripció:    Col·lecció de DTOs utilitzats per a la modificació i
//                actualització d'entitats existents a la base de dades.
// ============================================================================

namespace ServiTec.Application.DTOs
{
    /// <summary>
    /// Objecte de transferència de dades per a l'actualització d'una línia de comanda.
    /// </summary>
    public class UpdateLiniaComandaDTO
    {
        public int IdLinia { get; set; }
        public int Quantitat { get; set; }
        public decimal PreuUnitari { get; set; }
        public decimal Subtotal { get; set; }
        public int IdComanda { get; set; }
        public int IdProducte { get; set; }
        public string Estat { get; set; } = string.Empty;
    }

    /// <summary>
    /// Objecte de transferència de dades per a l'actualització de les dades de capçalera d'una comanda.
    /// </summary>
    public class UpdateComandaDTO
    {
        public int IdComanda { get; set; }
        public DateTime DataCreacio { get; set; }
        public string Estat { get; set; } = null!;
        public decimal Total { get; set; }
        public int IdTaula { get; set; }
        public int IdUsuari { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades per a l'actualització d'una categoria de la carta.
    /// </summary>
    public class UpdateCategoriaDTO
    {
        public string Nom { get; set; } = null!;
        public string? Descripcio { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades per a l'actualització d'un producte existent.
    /// </summary>
    public class UpdateProducteDTO
    {
        public string Nom { get; set; } = null!;
        public string? Descripcio { get; set; }
        public decimal Preu { get; set; }
        public bool Actiu { get; set; }
        public int IdCategoria { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades per a la modificació d'un usuari i els seus permisos.
    /// </summary>
    public class UpdateUsuariDTO
    {
        public string NomUsuari { get; set; } = null!;
        public string Contrasenya { get; set; } = null!;
        public bool Actiu { get; set; }
        public bool Admin { get; set; }
        public string Rol { get; set; } = null!;
    }

    /// <summary>
    /// Objecte de transferència de dades per a l'actualització de la disposició o estat d'una taula.
    /// </summary>
    public class UpdateTaulaDTO
    {
        public int Numero { get; set; }
        public int Capacitat { get; set; }
        public bool Estat { get; set; }
        public string? EstatComanda { get; set; }
        public double PosX { get; set; }
        public double PosY { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades per a la modificació d'un menjador o sala.
    /// </summary>
    public class UpdateMenjadorDTO
    {
        public string NomMenjador { get; set; } = null!;
        public bool Actiu { get; set; }
    }
}