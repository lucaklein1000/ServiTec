// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        BloqueigRequestDTO.cs
// Descripció:    Objecte de transferència de dades (DTO) utilitzat per 
//                sol·licitar el bloqueig temporal d'una taula a favor d'un 
//                cambrer concret i evitar accessos concurrerts.
// ============================================================================

namespace ServiTec.Application.DTOs
{
    /// <summary>
    /// Objecte de sol·licitud per registrar el bloqueig d'una taula per part d'un cambrer.
    /// </summary>
    public class BloqueigRequestDTO
    {
        /// <summary>
        /// Nom o identificador de l'usuari/cambrer que sol·licita el bloqueig.
        /// </summary>
        public string NomCambrer { get; set; } = string.Empty;
    }
}