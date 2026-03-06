using System;
using System.Collections.Generic;

namespace ServiTec.Models;

public partial class Taula
{
    public int IdTaula { get; set; }

    public int Numero { get; set; }

    public int Capacitat { get; set; }

    public string Estat { get; set; } = null!;

    public virtual ICollection<Comanda> Comanda { get; set; } = new List<Comanda>();
}
