// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        CreateDTOs.cs
// Descripció:    Col·lecció de DTOs utilitzats per a la creació i alta de noves
//                entitats al sistema de dades.
// ============================================================================

namespace ServiTec.Application.DTOs
{
    /// <summary>
    /// Objecte de transferència de dades per a la creació d'una línia de comanda.
    /// </summary>
    public class CreateLiniaComandaDTO
    {
        public int IdComanda { get; set; }
        public int IdProducte { get; set; }
        public int Quantitat { get; set; }
        public string Estat { get; set; } = null!;
        public int? IdCategoria { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades per a la creació d'una comanda completa amb les seves línies.
    /// </summary>
    public class CreateComandaDTO
    {
        public string Estat { get; set; } = null!;
        public int IdTaula { get; set; }
        public int IdUsuari { get; set; }
        public List<CreateLiniaComandaDTO> Linies { get; set; } = new List<CreateLiniaComandaDTO>();
    }

    /// <summary>
    /// Objecte de transferència de dades per a la creació d'una nova categoria a la carta.
    /// </summary>
    public class CreateCategoriaDTO
    {
        public int IdCategoria { get; set; }
        public string Nom { get; set; } = null!;
        public string? Descripcio { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades per a l'alta d'un nou producte al menú.
    /// </summary>
    public class CreateProducteDTO
    {
        public string Nom { get; set; } = null!;
        public string? Descripcio { get; set; }
        public decimal Preu { get; set; }
        public bool Actiu { get; set; }
        public int IdCategoria { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades per al registre d'un nou usuari del sistema.
    /// </summary>
    public class CreateUsuariDTO
    {
        public string NomUsuari { get; set; } = null!;
        public string Contrasenya { get; set; } = null!;
        public bool Actiu { get; set; }
        public bool Admin { get; set; }
        public string Rol { get; set; } = null!;
    }

    /// <summary>
    /// Objecte de transferència de dades per a l'alta i posicionament d'una nova taula.
    /// </summary>
    public class CreateTaulaDTO
    {
        public int Numero { get; set; }
        public int Capacitat { get; set; }
        public bool Estat { get; set; }
        public string? EstatComanda { get; set; }
        public int IdMenjador { get; set; }
        public int PosX { get; set; }
        public int PosY { get; set; }
    }

    /// <summary>
    /// Objecte de transferència de dades per a la creació d'un nou menjador o sala.
    /// </summary>
    public class CreateMenjadorDTO
    {
        public string NomMenjador { get; set; } = null!;
        public bool Actiu { get; set; }
        public List<CreateTaulaDTO> Taules { get; set; } = new List<CreateTaulaDTO>();
    }
}