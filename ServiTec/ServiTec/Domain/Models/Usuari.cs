using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models;

public partial class Usuari
{
    public int IdUsuari { get; set; }

    public string NomUsuari { get; set; } = null!;

    public string Contrasenya { get; set; } = null!;

    public bool Actiu { get; set; }

    public bool Admin { get; set; }

    public virtual ICollection<Comanda> Comanda { get; set; } = new List<Comanda>();

    public virtual ICollection<LiniaUsuari> LiniaUsuaris { get; set; } = new List<LiniaUsuari>();
}
