// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Usuari.cs
// Descripció:    Entitat de domini que representa els usuaris del sistema 
//                (cambrers, personal de cuina i administradors), gestionant 
//                les seves credencials, rols i permisos d'accés.
// ============================================================================

using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models;

/// <summary>
/// Entitat que representa un usuari o treballador del sistema ServiTec.
/// </summary>
public partial class Usuari
{
    /// <summary>
    /// Identificador únic de l'usuari.
    /// </summary>
    public int IdUsuari { get; set; }

    /// <summary>
    /// Nom d'usuari utilitzat per a l'autenticació al sistema.
    /// </summary>
    public string NomUsuari { get; set; } = null!;

    /// <summary>
    /// Contrasenya de l'usuari encriptada/hashing.
    /// </summary>
    public string Contrasenya { get; set; } = null!;

    /// <summary>
    /// Indica si l'usuari està actiu i pot accedir a l'aplicació.
    /// </summary>
    public bool Actiu { get; set; }

    /// <summary>
    /// Indica si l'usuari disposa de permisos d'administrador.
    /// </summary>
    public bool Admin { get; set; }

    /// <summary>
    /// Rol assignat a l'usuari dins del sistema (ex. "Cambrer", "Cuina", "Admin").
    /// </summary>
    public string Rol { get; set; } = null!;

    /// <summary>
    /// Col·lecció de comandes creades directament per aquest usuari.
    /// </summary>
    public virtual ICollection<Comanda> Comanda { get; set; } = new List<Comanda>();

    /// <summary>
    /// Col·lecció d'associacions a comandes en què l'usuari ha intervingut.
    /// </summary>
    public virtual ICollection<LiniaUsuari> LiniaUsuaris { get; set; } = new List<LiniaUsuari>();
}