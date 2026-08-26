// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        LiniaComanda.cs
// Descripció:    Entitat de domini que representa cada línia de detall d'una 
//                comanda, registrant el producte seleccionat, la quantitat, 
//                el preu unitari, el subtotal i l'estat de preparació.
// ============================================================================

using ServiTec.Domain.Models;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServiTec.Domain.Models;

/// <summary>
/// Entitat que representa el detall d'un producte individual dins d'una comanda.
/// </summary>
public partial class LiniaComanda
{
    /// <summary>
    /// Identificador únic de la línia de comanda.
    /// </summary>
    public int IdLinia { get; set; }

    /// <summary>
    /// Quantitat d'unitats demanades del producte.
    /// </summary>
    public int Quantitat { get; set; }

    /// <summary>
    /// Preu unitari del producte en el moment de realitzar la comanda.
    /// </summary>
    public decimal PreuUnitari { get; set; }

    /// <summary>
    /// Subtotal calculat per a aquesta línia (Quantitat x PreuUnitari).
    /// </summary>
    public decimal Subtotal { get; set; }

    // 1. Relació amb la Comanda
    /// <summary>
    /// Identificador únic de la comanda a la qual pertany la línia.
    /// </summary>
    public int IdComanda { get; set; }

    /// <summary>
    /// Propietat de navegació cap a la comanda associada.
    /// </summary>
    [ForeignKey("IdComanda")]
    public virtual Comanda IdComandaNavigation { get; set; } = null!;

    // 2. Relació amb el Producte
    /// <summary>
    /// Identificador únic del producte sol·licitat.
    /// </summary>
    public int IdProducte { get; set; }

    /// <summary>
    /// Propietat de navegació cap al producte associat.
    /// </summary>
    [ForeignKey("IdProducte")]
    public virtual Producte IdProducteNavigation { get; set; } = null!;

    // 3. Relació opcional amb la Categoria
    /// <summary>
    /// Identificador únic de la categoria associada al producte.
    /// </summary>
    public int? IdCategoria { get; set; }

    /// <summary>
    /// Propietat de navegació cap a la categoria opcional del producte.
    /// </summary>
    [ForeignKey("IdCategoria")]
    public virtual Categoria? IdCategoriaNavigation { get; set; }

    /// <summary>
    /// Estat de preparació o servit de la línia de comanda (ex. "pendent", "preparat").
    /// </summary>
    public string Estat { get; set; } = null!;
}