// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        LiniaUsuari.cs
// Descripció:    Entitat de domini que actua com a taula intermèdia per 
//                establir la relació molts a molts entre els usuaris (cambrers) 
//                i les comandes en què han intervingut.
// ============================================================================

using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models;

/// <summary>
/// Entitat d'associació que registra la traçabilitat dels usuaris que intervenen en una comanda.
/// </summary>
public partial class LiniaUsuari
{
    /// <summary>
    /// Identificador únic de l'associació entre usuari i comanda.
    /// </summary>
    public int IdLiniaUsuari { get; set; }

    /// <summary>
    /// Identificador únic de l'usuari (cambrer) associat a la comanda.
    /// </summary>
    public int IdUsuari { get; set; }

    /// <summary>
    /// Identificador únic de la comanda en què l'usuari ha intervingut.
    /// </summary>
    public int IdComanda { get; set; }

    /// <summary>
    /// Propietat de navegació cap a la comanda relacionada.
    /// </summary>
    public virtual Comanda IdComandaNavigation { get; set; } = null!;

    /// <summary>
    /// Propietat de navegació cap a l'usuari relacionat.
    /// </summary>
    public virtual Usuari IdUsuariNavigation { get; set; } = null!;
}