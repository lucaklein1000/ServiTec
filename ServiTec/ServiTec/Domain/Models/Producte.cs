using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;

namespace ServiTec.Domain.Models;

public partial class Producte
{
    public int IdProducte { get; set; }

    public string Nom { get; set; } = null!;

    public string? Descripcio { get; set; }

    public decimal Preu { get; set; }

    public bool Actiu { get; set; }

    public int IdCategoria { get; set; }


    public virtual Categoria Categoria { get; set; } = null!;

    //public virtual ICollection<LiniaComanda> LiniaComanda { get; set; } = new List<LiniaComanda>();
    
}
