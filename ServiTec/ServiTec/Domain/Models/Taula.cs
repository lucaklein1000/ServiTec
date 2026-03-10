using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models;

public partial class Taula
{
    public int IdTaula { get; set; }

    public int Numero { get; set; }

    public int Capacitat { get; set; }

    public bool Estat { get; set; }

    public virtual ICollection<Comanda> Comanda { get; set; } = new List<Comanda>();
}
