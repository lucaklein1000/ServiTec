using ServiTec.Domain.Models;
using System.ComponentModel.DataAnnotations.Schema;

public partial class LiniaComanda
{
    public int IdLinia { get; set; }

    public int Quantitat { get; set; }

    public decimal PreuUnitari { get; set; }

    public decimal Subtotal { get; set; }

    // 1. Relación con la Comanda
    public int IdComanda { get; set; }

    [ForeignKey("IdComanda")] // 🔥 Enlaza con el ID de la Comanda
    public virtual Comanda IdComandaNavigation { get; set; } = null!;


    // 2. Relación con el Producto
    public int IdProducte { get; set; }

    [ForeignKey("IdProducte")] 
    public virtual Producte IdProducteNavigation { get; set; } = null!;

    public int? IdCategoria { get; set; }

    [ForeignKey("IdCategoria")]
    public virtual Categoria? IdCategoriaNavigation { get; set; }

    public string Estat { get; set; }
}