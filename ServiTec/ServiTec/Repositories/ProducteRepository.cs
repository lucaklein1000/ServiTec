using ServiTec.Data;
using ServiTec.Models;

public class ProducteRepository
{
    private readonly ServiTecDbContext _context;

    public ProducteRepository(ServiTecDbContext context)
    {
        _context = context;
    }

    public async Task<Producte?> GetById(int id)
    {
        return await _context.Productes.FindAsync(id);
    }

    public async Task Delete(Producte p)
    {
        _context.Productes.Remove(p);
        await _context.SaveChangesAsync();
    }
}