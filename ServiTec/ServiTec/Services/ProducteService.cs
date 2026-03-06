namespace ServiTec.Services
{
    public class ProducteService
    {
        private readonly ProducteRepository _repository;

        public ProducteService(ProducteRepository repository)
        {
            _repository = repository;
        }

        public async Task<bool> DeleteProducte(int id)
        {
            var producte = await _repository.GetById(id);

            if (producte == null)
                return false;

            await _repository.Delete(producte);
            return true;
        }
    }
}
