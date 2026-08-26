// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        Menjador.cs
// Descripció:    Entitat de domini que representa una sala o menjador del 
//                restaurant (ex. Sala Principal, Terrassa, Menjador Privat).
// ============================================================================

using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models
{
    /// <summary>
    /// Entitat que representa un espai físic o sala del restaurant on s'ubiquen les taules.
    /// </summary>
    public class Menjador
    {
        /// <summary>
        /// Identificador únic del menjador o sala.
        /// </summary>
        public int IdMenjador { get; set; }

        /// <summary>
        /// Nom descriptiu del menjador (ex. "Terrassa", "Sala Principal").
        /// </summary>
        public string NomMenjador { get; set; } = null!;

        /// <summary>
        /// Indica si el menjador està actualment operatiu i disponible per al servei.
        /// </summary>
        public bool Actiu { get; set; }

        /// <summary>
        /// Col·lecció de taules pertanyents a aquesta sala o menjador.
        /// </summary>
        public virtual ICollection<Taula> Taula { get; set; } = new List<Taula>();
    }
}