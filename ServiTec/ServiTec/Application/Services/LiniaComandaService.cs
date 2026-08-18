using Microsoft.EntityFrameworkCore;
using ServiTec.Application.DTOs;
using ServiTec.Application.DTOs.ServiTec.DTOs;
using ServiTec.Domain.Models;

namespace ServiTec.Services
{
    public class LiniaComandaService
    {
        private readonly IRepository<LiniaComanda> _repository;
        private readonly IRepository<Producte> _productRepository;
        private readonly IRepository<Comanda> _comandaRepository;

        public LiniaComandaService(
            IRepository<LiniaComanda> repository,
            IRepository<Producte> productRepository,
            IRepository<Comanda> comandaRepository)
        {
            _repository = repository;
            _productRepository = productRepository;
            _comandaRepository = comandaRepository;
        }

        // 1. OBTENER TODAS LAS LÍNEAS
        public async Task<IEnumerable<LiniaComandaDTO>> GetAll()
        {
            var liniaComandas = await _repository.GetAll();
            return liniaComandas.Select(p => new LiniaComandaDTO
            {
                IdLinia = p.IdLinia,
                Quantitat = p.Quantitat,
                PreuUnitari = p.PreuUnitari,
                Subtotal = p.Subtotal,
                IdComanda = p.IdComanda,
                IdProducte = p.IdProducte,
                Estat = p.Estat,
                // Usamos 'p' de forma consistente
                IdCategoria = p.IdCategoria ?? p.IdProducteNavigation?.IdCategoria
            }).ToList();
        }

        // 2. OBTENER UNA LÍNEA POR ID
        public async Task<LiniaComanda?> GetById(int id)
        {
            return await _repository.GetById(id);
        }

        public async Task<LiniaComandaDTO?> Create(CreateLiniaComandaDTO dto)
        {
            // 🔍 Buscamos el producto con el ID exacto del DTO
            var producte = await _productRepository.GetById(dto.PostIdProducte);
            if (producte == null) return null;

            decimal preuUnitari = (decimal)producte.Preu;

            var nuevaLinia = new LiniaComanda
            {
                IdComanda = dto.PostIdComanda,
                IdProducte = dto.PostIdProducte,
                Quantitat = dto.PostQuantitat,
                PreuUnitari = preuUnitari,
                Subtotal = preuUnitari * dto.PostQuantitat,
                Estat = "Pendent",
                // Si no se asigna categoría explícita en el DTO, hereda la del producto
                IdCategoria = dto.PostIdCategoria ?? producte.IdCategoria
            };

            var resultat = await _repository.Create(nuevaLinia);
            await ActualitzarTotalComanda(resultat.IdComanda);

            return new LiniaComandaDTO
            {
                IdLinia = resultat.IdLinia,
                Quantitat = resultat.Quantitat,
                PreuUnitari = resultat.PreuUnitari,
                Subtotal = resultat.Subtotal,
                IdComanda = resultat.IdComanda,
                IdProducte = resultat.IdProducte,
                Estat = resultat.Estat,
                IdCategoria = resultat.IdCategoria
            };
        }

        // 🛠️ MÉTODO AUXILIAR PRIVADO: Modificado para usar tu _comandaRepository genérico
        private async Task ActualitzarTotalComanda(int idComanda)
        {
            // 1. Obtenemos la comanda cabecera
            var comanda = await _comandaRepository.GetById(idComanda);

            if (comanda != null)
            {
                // 2. Obtenemos todas las líneas del sistema a través del repositorio genérico
                var totesLesLinies = await _repository.GetAll();

                // 3. Filtramos las que pertenecen a esta comanda y sumamos sus subtotales
                comanda.Total = totesLesLinies
                    .Where(l => l.IdComanda == idComanda)
                    .Sum(l => l.Subtotal);

                // 4. Actualizamos la comanda en la base de datos
                await _comandaRepository.Update(comanda);
            }
        }

        public async Task<bool> Update(int id, UpdateLiniaComandaDTO dto)
        {
            // 🔍 Buscamos la línea usando tu repositorio genérico
            var linia = await _repository.GetById(id);
            if (linia == null) return false;

            // Actualizamos la cantidad con el campo de tu DTO
            linia.Quantitat = dto.PutQuantitat; // ⚠️ Revisa si en tu DTO se llama PostQuantitat o Quantitat
            linia.Subtotal = linia.Quantitat * linia.PreuUnitari; // Recalculamos el subtotal de la línea

            // 💾 Guardamos los cambios a través del repositorio genérico
            await _repository.Update(linia);

            // 🔄 Sincronizamos el total general de la factura
            await ActualitzarTotalComanda(linia.IdComanda);
            return true;
        }

        public async Task<bool> Delete(int id)
        {
            // 🔍 Buscamos la línea para saber a qué comanda pertenecía antes de borrarla
            var linia = await _repository.GetById(id);
            if (linia == null) return false;

            int idComanda = linia.IdComanda;

            // ❌ Borramos usando tu repositorio genérico
            await _repository.Delete(linia);

            // 🔄 Sincronizamos el total de la comanda cabecera restando este plato
            await ActualitzarTotalComanda(idComanda);
            return true;
        }
    }
}