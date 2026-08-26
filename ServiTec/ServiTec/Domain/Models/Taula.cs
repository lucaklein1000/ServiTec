// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Taula.cs
// Descripció:    Entitat de domini que representa una taula del restaurant,
//                incloent la seva capacitat, estat d'ocupació, posició espacial 
//                i mecanisme de bloqueig per a control de concurrència.
// ============================================================================

using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models;

/// <summary>
/// Entitat que representa una taula física del restaurant i el seu estat actual.
/// </summary>
public partial class Taula
{
    /// <summary>
    /// Identificador únic de la taula.
    /// </summary>
    public int IdTaula { get; set; }

    /// <summary>
    /// Número assignat a la taula dins del menjador.
    /// </summary>
    public int Numero { get; set; }

    /// <summary>
    /// Capacitat màxima de comensals de la taula.
    /// </summary>
    public int Capacitat { get; set; }

    /// <summary>
    /// Estat d'ocupació de la taula (true: ocupada, false: lliure).
    /// </summary>
    public bool Estat { get; set; }

    /// <summary>
    /// Identificador únic del menjador o sala on està ubicada la taula.
    /// </summary>
    public int IdMenjador { get; set; }

    /// <summary>
    /// Coordenada X per a la representació gràfica del plànol de la sala.
    /// </summary>
    public double PosX { get; set; } = 0;

    /// <summary>
    /// Coordenada Y per a la representació gràfica del plànol de la sala.
    /// </summary>
    public double PosY { get; set; } = 0;

    /// <summary>
    /// Col·lecció de comandes realitzades en aquesta taula.
    /// </summary>
    public virtual ICollection<Comanda> Comanda { get; set; } = new List<Comanda>();

    /// <summary>
    /// Propietat de navegació cap al menjador al qual pertany la taula.
    /// </summary>
    public virtual Menjador IdMenjadorNavigation { get; set; } = null!;

    /// <summary>
    /// Indica si la taula està sent utilitzada actualment per un cambrer per prendre nota.
    /// </summary>
    public bool Bloquejada { get; set; } = false;

    /// <summary>
    /// Nom o identificador de l'usuari/cambrer que té la taula bloquejada.
    /// </summary>
    public string? UsuariBloqueig { get; set; }

    /// <summary>
    /// Marca temporal de l'últim bloqueig per gestionar timeouts automàtics.
    /// </summary>
    public DateTime? UltimBloqueig { get; set; }
}