// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Comanda.cs
// Descripció:    Entitat de domini que representa una comanda realitzada en una
//                taula, incloent el seu estat, total acumulat, cambrer assignat
//                i el desglose de línies de comanda associades.
// ============================================================================

using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models;

/// <summary>
/// Entitat que representa una comanda o comanda de restaurant.
/// </summary>
public partial class Comanda
{
    /// <summary>
    /// Identificador únic de la comanda.
    /// </summary>
    public int IdComanda { get; set; }

    /// <summary>
    /// Data i hora de creació de la comanda.
    /// </summary>
    public DateTime DataCreacio { get; set; }

    /// <summary>
    /// Estat actual de la comanda (ex. "oberta", "segons", "pendent", "pagada").
    /// </summary>
    public string Estat { get; set; } = null!;

    /// <summary>
    /// Import total acumulat de la comanda en euros.
    /// </summary>
    public decimal Total { get; set; }

    /// <summary>
    /// Identificador únic de la taula on s'ha obert la comanda.
    /// </summary>
    public int IdTaula { get; set; }

    /// <summary>
    /// Identificador únic del cambrer/usuari que ha creat la comanda.
    /// </summary>
    public int IdUsuari { get; set; }

    /// <summary>
    /// Propietat de navegació cap a la taula assignada.
    /// </summary>
    public virtual Taula IdTaulaNavigation { get; set; } = null!;

    /// <summary>
    /// Propietat de navegació cap a l'usuari/cambrer creador.
    /// </summary>
    public virtual Usuari IdUsuariNavigation { get; set; } = null!;

    /// <summary>
    /// Col·lecció de línies de detall amb els productes demanats.
    /// </summary>
    public virtual ICollection<LiniaComanda> LiniaComanda { get; set; } = new List<LiniaComanda>();

    /// <summary>
    /// Col·lecció d'associacions amb altres cambrers que han intervingut en la comanda.
    /// </summary>
    public virtual ICollection<LiniaUsuari> LiniaUsuaris { get; set; } = new List<LiniaUsuari>();
}