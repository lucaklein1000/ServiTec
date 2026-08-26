// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Categoria.cs
// Descripció:    Entitat de domini que representa una categoria de productes
//                dins del catàleg del restaurant (ex. Begudes, Entrants, etc.).
// ============================================================================

using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models;

/// <summary>
/// Entitat que representa la categoria a la qual pertanyen els productes de la carta.
/// </summary>
public partial class Categoria
{
    /// <summary>
    /// Identificador únic de la categoria.
    /// </summary>
    public int IdCategoria { get; set; }

    /// <summary>
    /// Nom de la categoria (ex. "Postres", "Begudes").
    /// </summary>
    public string Nom { get; set; } = null!;

    /// <summary>
    /// Descripció opcional amb informació detallada sobre la categoria.
    /// </summary>
    public string? Descripcio { get; set; }

    /// <summary>
    /// Col·lecció de productes associats a aquesta categoria.
    /// </summary>
    public virtual ICollection<Producte> Productes { get; set; } = new List<Producte>();
}