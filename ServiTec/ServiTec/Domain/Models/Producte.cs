// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Producte.cs
// Descripció:    Entitat de domini que representa un producte individual 
//                disponible a la carta del restaurant (preu, estat d'activació 
//                i categoria associada).
// ============================================================================

using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServiTec.Domain.Models;

/// <summary>
/// Entitat que representa un article o producte de la carta del restaurant.
/// </summary>
public partial class Producte
{
    /// <summary>
    /// Identificador únic del producte.
    /// </summary>
    public int IdProducte { get; set; }

    /// <summary>
    /// Nom comercial del producte.
    /// </summary>
    public string Nom { get; set; } = null!;

    /// <summary>
    /// Descripció detallada del producte o dels seus ingredients.
    /// </summary>
    public string? Descripcio { get; set; }

    /// <summary>
    /// Preu de venda al públic en euros.
    /// </summary>
    public decimal Preu { get; set; }

    /// <summary>
    /// Indica si el producte està actiu i disponible a la carta actualment.
    /// </summary>
    public bool Actiu { get; set; }

    /// <summary>
    /// Identificador únic de la categoria a la qual pertany el producte.
    /// </summary>
    public int IdCategoria { get; set; }

    /// <summary>
    /// Propietat de navegació cap a la categoria associada.
    /// </summary>
    public virtual Categoria Categoria { get; set; } = null!;

    // public virtual ICollection<LiniaComanda> LiniaComanda { get; set; } = new List<LiniaComanda>();
}