using System;
using System.Collections.Generic;

namespace ServiTec.Domain.Models;

public partial class Categoria
{
    public int IdCategoria { get; set; }

    public string Nom { get; set; } = null!;

    public string? Descripcio { get; set; }

    public virtual ICollection<Producte> Productes { get; set; } = new List<Producte>();
}
