using System;
using System.Collections.Generic;

namespace ServiTec.Models;

public partial class LiniaUsuari
{
    public int IdLiniaUsuari { get; set; }

    public int IdUsuari { get; set; }

    public int IdComanda { get; set; }

    public virtual Comanda IdComandaNavigation { get; set; } = null!;

    public virtual Usuari IdUsuariNavigation { get; set; } = null!;
}
