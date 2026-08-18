using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Domain.Models;
using ServiTec.Infrastructure.Data;

public class ComandaService
{
    private readonly IRepository<Comanda> _repository;
    private readonly IRepository<Producte> _productRepository;
    private readonly IRepository<Taula> _taulaRepository;
    private readonly ServiTecDbContext _context; // 🎇 Añade el contexto directo

    public ComandaService(
        IRepository<Comanda> repository,
        IRepository<Producte> productRepository,
        IRepository<Taula> taulaResposity,
        ServiTecDbContext context) // 👈 Inyéctalo aquí
    {
        _repository = repository;
        _productRepository = productRepository;
        _taulaRepository = taulaResposity;
        _context = context;
    }

    public async Task<Comanda?> GetById(int id)
    {
        return await _repository.GetById(id);
    }

    public async Task<bool> DeleteComanda(int id)
    {
        var Comanda = await _repository.GetById(id);

        if (Comanda == null)
            return false;

        await _repository.Delete(Comanda);
        return true;
    }

    public async Task<IEnumerable<ComandaDTO>> GetComandas()
    {
        var query = await _repository.GetAll();

        // Forzamos la carga de las líneas y sus productos antes del mapeo
        var comandasConLineas = query
            .AsQueryable()
            .Include(c => c.LiniaComanda)
                .ThenInclude(lc => lc.IdProducteNavigation)
            .ToList();

        return comandasConLineas.Select(p => new ComandaDTO
        {
            IdComanda = p.IdComanda,
            DataCreacio = p.DataCreacio,
            Estat = p.Estat,
            Total = p.Total,
            IdTaula = p.IdTaula,
            IdUsuari = p.IdUsuari,
            // 🎇 Mapeamos la lista de líneas al DTO de salida
            LiniaComanda = p.LiniaComanda.Select(l => new LiniaComandaDTO
            {
                IdLinia = l.IdLinia,
                Quantitat = l.Quantitat,
                PreuUnitari = l.PreuUnitari,
                Subtotal = l.Subtotal,
                IdComanda = l.IdComanda,
                IdProducte = l.IdProducte
            }).ToList()
        }).ToList();
    }

    public async Task<Comanda?> CrearComanda(CreateComandaDTO dto)
    {
        var taula = await _taulaRepository.GetById(dto.PostIdTaula);
        if (taula == null)
        {
            // En servicios lanzamos excepciones de argumento o de negocio
            throw new ArgumentException("La mesa especificada no existe.");
        }

        // 2. Control de seguridad: Si ya está ocupada (Estat == false), no dejamos duplicar
        if (!taula.Estat)
        {
            throw new InvalidOperationException("Esta mesa ya tiene una comanda activa.");
        }

        // 1. Instanciamos el objeto principal Comanda
        var comanda = new Comanda
        {
            DataCreacio = DateTime.Now, // Fecha e hora actual del servidor
            Estat = dto.PostEstat ?? "Pendent",
            IdTaula = dto.PostIdTaula,
            IdUsuari = dto.PostIdUsuari,
            Total = 0, // Lo calcularemos sumando los productos
            LiniaComanda = new List<LiniaComanda>() // Colección de navegación en tu modelo Comanda
        };

        decimal granTotal = 0;

        // 2. Recorremos las líneas que nos ha enviado el camarero desde Android
        foreach (var liniaDto in dto.PostLinies)
        {
            // Usamos tu método GetById(id) del repositorio genérico de productos
            var producte = await _productRepository.GetById(liniaDto.PostIdProducte);

            if (producte != null)
            {
                // Tomamos el precio del momento exacto de la creación (La "Fotografía" del precio)
                decimal preuUnitari = (decimal)producte.Preu;
                decimal subtotal = preuUnitari * liniaDto.PostQuantitat;

                granTotal += subtotal;

                // Creamos la línea física que guardará el historial intacto
                var novaLinia = new LiniaComanda
                {
                    Quantitat = liniaDto.PostQuantitat,
                    PreuUnitari = preuUnitari,
                    Subtotal = subtotal,
                    IdProducte = liniaDto.PostIdProducte,
                    Estat = liniaDto.PostEstat,
                    IdCategoria = liniaDto.PostIdCategoria
                    // NO asignamos IdComanda. Al meterlo en la lista de 'comanda', EF lo mapea solo.
                };

                comanda.LiniaComanda.Add(novaLinia);
            }
        }

        // 3. Asignamos el total real calculado por el Back de forma segura
        comanda.Total = granTotal;

        taula.Estat = false;
        await _taulaRepository.Update(taula);

        // 4. Guardamos en la BD a través de tu repositorio genérico
        // Al pasarle 'comanda', EF guardará la cabecera y todas las filas de LiniasComanda de golpe.
        var resultat = await _repository.Create(comanda);

        return resultat;
    }

    public async Task<Comanda?> UpdateComandaDTO(int id, UpdateComandaDTO dto)
    {
        var Comanda = await _repository.GetById(id);

        if (Comanda == null)
            return null;

        Comanda.IdComanda = dto.PutIdComanda;
        Comanda.DataCreacio = dto.PutDataCreacio;
        Comanda.Estat = dto.PutEstat;
        Comanda.Total = dto.PutTotal;
        Comanda.IdTaula = dto.PutIdTaula;
        Comanda.IdUsuari = dto.PutIdUsuari;

        await _repository.Update(Comanda);

        return Comanda;
    }

    public async Task<Comanda?> ObtenirComandaActivaSegonsTaulaAsync(int idTaula)
    {
        return await _context.Comanda
            .Include(c => c.LiniaComanda)
                .ThenInclude(lc => lc.IdProducteNavigation)
            .FirstOrDefaultAsync(c => c.IdTaula == idTaula &&
                                     (c.Estat == "oberta" || c.Estat == "pendent"));
    }

    public async Task<List<ComandaCuinaDTO>> ObtenirComandesCuinaAsync()
    {
        return await _context.Comanda
            .Include(c => c.IdTaulaNavigation)
            .Include(c => c.LiniaComanda)
                .ThenInclude(l => l.IdProducteNavigation)
            // 1. Solo comandas en estado "pendent" u "oberta"
            .Where(c => c.Estat == "pendent" || c.Estat == "oberta")
            // 2. FILTRO CLAVE: Solo traer comandas que tengan AL MENOS UNA línea pendiente
            .Where(c => c.LiniaComanda.Any(l => l.Estat == "pendentEnviar"))
            .Select(c => new ComandaCuinaDTO
            {
                IdComanda = c.IdComanda,
                IdTaula = c.IdTaula,
                NumTaula = c.IdTaulaNavigation.Numero,
                DataHora = c.DataCreacio,

                // 3. FILTRO CLAVE: Seleccionar ÚNICAMENTE las líneas pendientes
                Linies = c.LiniaComanda
                    .Where(l => l.Estat == "pendentEnviar") // 👈 AQUÍ FILTRAMOS LAS LÍNEAS
                    .Select(l => new LiniaCuinaDTO
                    {
                        IdLiniaComanda = l.IdLinia,
                        IdProducte = l.IdProducte,
                        NomProducte = l.IdProducteNavigation != null ? l.IdProducteNavigation.Nom : "Sense nom",
                        Quantitat = l.Quantitat,
                        IdCategoria = l.IdProducteNavigation != null ? l.IdProducteNavigation.IdCategoria : 0
                    }).ToList()
            })
            .ToListAsync();
    }

    public async Task<bool> CanviarEstatComandaAsync(int idComanda, string nouEstat)
    {
        var comanda = await _context.Comanda.FindAsync(idComanda);
        if (comanda == null) return false;

        comanda.Estat = nouEstat;
        _context.Comanda.Update(comanda);
        await _context.SaveChangesAsync();

        return true;
    }

    public async Task<bool> CanviarEstatLiniaAsync(int idLinia, string nouEstat)
    {
        var linia = await _context.LiniaComanda
            .Include(l => l.IdComandaNavigation)
            .FirstOrDefaultAsync(l => l.IdLinia == idLinia);

        if (linia == null) return false;

        linia.Estat = nouEstat;
        _context.LiniaComanda.Update(linia);
        await _context.SaveChangesAsync();

        return true;
    }

    public async Task<bool> CobrarComandaAsync(int idComanda)
    {
        // Cargar la comanda junto con sus líneas y la mesa asociada
        var comanda = await _context.Comanda
            .Include(c => c.LiniaComanda)
            .Include(c => c.IdTaulaNavigation)
            .FirstOrDefaultAsync(c => c.IdComanda == idComanda);

        if (comanda == null) return false;

        // 1. Cambiar el estado de todas sus líneas a "Servit" usando CanviarEstatLiniaAsync
        if (comanda.LiniaComanda != null)
        {
            // Creamos una copia de los IDs para iterar sin problemas de contexto
            var idsLinies = comanda.LiniaComanda.Select(l => l.IdLinia).ToList();

            foreach (var idLinia in idsLinies)
            {
                await CanviarEstatLiniaAsync(idLinia, "Servit");
            }
        }

        // 2. Cambiar estado de la comanda a "tancada"
        await CanviarEstatComandaAsync(comanda.IdComanda, "tancada");

        // 3. Liberar la mesa asociada
        if (comanda.IdTaulaNavigation != null)
        {
            comanda.IdTaulaNavigation.Estat = true;
        }

        await _context.SaveChangesAsync();
        return true;
    }

    public async Task<Comanda?> AfegirLiniesAComanda(int idComanda, List<CreateLiniaComandaDTO> novesLiniesDto)
    {
        // 1. Buscamos solo la cabecera de la comanda (SIN Include de líneas)
        var comanda = await _context.Comanda.FindAsync(idComanda);
        if (comanda == null)
        {
            throw new ArgumentException("La comanda no existe.");
        }

        decimal totalAdicional = 0;
        var novesEntitatsLinia = new List<LiniaComanda>();

        // 2. Procesamos SOLO las nuevas líneas enviadas desde Android
        foreach (var liniaDto in novesLiniesDto)
        {
            var producte = await _productRepository.GetById(liniaDto.PostIdProducte);
            if (producte != null)
            {
                decimal preuUnitari = (decimal)producte.Preu;
                decimal subtotal = preuUnitari * liniaDto.PostQuantitat;

                totalAdicional += subtotal;

                novesEntitatsLinia.Add(new LiniaComanda
                {
                    IdComanda = idComanda,
                    IdProducte = liniaDto.PostIdProducte,
                    Quantitat = liniaDto.PostQuantitat,
                    PreuUnitari = preuUnitari,
                    Subtotal = subtotal,
                    Estat = liniaDto.PostEstat ?? "Pendent"
                });
            }
        }

        // 3. Insertamos directamente la lista de líneas NUEVAS en el DbSet de LiniaComanda
        await _context.LiniaComanda.AddRangeAsync(novesEntitatsLinia);

        // 4. Actualizamos el total de la comanda principal
        comanda.Total += totalAdicional;

        // 5. Guardamos en la base de datos de una sola vez
        await _context.SaveChangesAsync();

        return comanda;
    }

    public async Task<bool> EliminarLiniaComandaAsync(int idLiniaComanda)
    {
        // 1. Cercar la línia de comanda a la BD incloent la comanda pare
        var linia = await _context.LiniaComanda
            .Include(l => l.IdComandaNavigation)
            .FirstOrDefaultAsync(l => l.IdLinia == idLiniaComanda);

        if (linia == null)
        {
            return false; // No existeix la línia
        }

        if (linia.Quantitat <= 1)
        {
            // 2. Si queda 1 o menys -> Marcar com a Eliminat i posar a 0
            linia.Estat = "Eliminat";
            linia.PreuUnitari = 0;
            linia.Subtotal = 0;
            linia.Quantitat = 0;
        }
        else
        {
            // 3. Si n'hi ha més d'una -> Restar 1 i recalcular el subtotal d'aquesta línia
            linia.Quantitat -= 1;
            linia.Subtotal = linia.Quantitat * linia.PreuUnitari; // 👈 SOLUCIÓ: Recalcular subtotal de la línia
        }

        // 4. Recalcular SEMPRE el total general de la comanda pare (sumant només les línies actives/no eliminades)
        var comandaPare = linia.IdComandaNavigation;
        if (comandaPare != null)
        {
            var totalActiu = await _context.LiniaComanda
                .Where(l => l.IdComanda == comandaPare.IdComanda && l.Estat != "Eliminat")
                .SumAsync(l => l.Subtotal);

            comandaPare.Total = totalActiu;
        }

        // 5. Guardar canvis a SQL Server
        await _context.SaveChangesAsync();
        return true;
    }
}
