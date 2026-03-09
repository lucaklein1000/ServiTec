using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models;

public partial class Comanda
{
    public int IdComanda { get; set; }

    public DateTime DataCreacio { get; set; }

    public string Estat { get; set; } = null!;

    public decimal Total { get; set; }

    public int IdTaula { get; set; }

    public int IdUsuari { get; set; }

    public virtual Taula IdTaulaNavigation { get; set; } = null!;

    public virtual Usuari IdUsuariNavigation { get; set; } = null!;

    public virtual ICollection<LiniaComanda> LiniaComanda { get; set; } = new List<LiniaComanda>();

    public virtual ICollection<LiniaUsuari> LiniaUsuaris { get; set; } = new List<LiniaUsuari>();
}
