using System;
using System.Collections.Generic;

namespace ServiTec.Models;

public partial class LiniaComanda
{
    public int IdLinia { get; set; }

    public int Quantitat { get; set; }

    public decimal PreuUnitari { get; set; }

    public decimal Subtotal { get; set; }

    public int IdComanda { get; set; }

    public int IdProducte { get; set; }

    public virtual Comanda IdComandaNavigation { get; set; } = null!;

    public virtual Producte IdProducteNavigation { get; set; } = null!;
}
