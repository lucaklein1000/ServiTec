using ServiTec.Application.DTOs;
using ServiTec.Domain.Models;

namespace ServiTec.Application.Services
{
    public class ProducteService
    {
        private readonly IRepository<Producte> _repository;

        public ProducteService(IRepository<Producte> repository)
        {
            _repository = repository;
        }

        public async Task<Producte?> GetById(int id)
        {
            return await _repository.GetById(id);
        }

        public async Task<bool> DeleteProducte(int id)
        {
            var producte = await _repository.GetById(id);

            if (producte == null)
                return false;

            await _repository.Delete(producte);
            return true;
        }

        public async Task<IEnumerable<ProducteDTO>> GetProductes()
        {
            var productes = await _repository.GetAll();
            return productes.Select(p => new ProducteDTO
            {
                IdProducte = p.IdProducte,
                Nom = p.Nom,
                Preu = p.Preu
            }).ToList();
        }

        public async Task<Producte?> CrearProducte(CreateProducteDTO dto)
        {
            var producte = new Producte
            {
                Nom = dto.PostNom,
                Descripcio = dto.PostDescripcio,
                Preu = dto.PostPreu,
                Actiu = dto.PostActiu,
                IdCategoria = dto.PostIdCategoria
            };

            var resultat = await _repository.Create(producte);

            return resultat;
        }

        public async Task<Producte?> ActualitzarProducte(int id, ActualitzarProducte dto)
        {
            var producte = await _repository.GetById(id);

            if (producte == null)
                return null;

            producte.Nom = dto.PutNom;
            producte.Descripcio = dto.PutDescripcio;
            producte.Preu = dto.PutPreu;
            producte.Actiu = dto.PutActiu;
            producte.IdCategoria = dto.PutIdCategoria;

            await _repository.Update(producte);

            return producte;
        }
    }
}
