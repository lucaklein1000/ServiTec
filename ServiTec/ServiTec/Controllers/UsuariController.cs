using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;

namespace ServiTec.Controllers
{
    public class UsuariController : Controller
    {
        // GET: UsuariController
        public ActionResult Index()
        {
            return View();
        }

        // GET: UsuariController/Details/5
        public ActionResult Details(int id)
        {
            return View();
        }

        // GET: UsuariController/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: UsuariController/Create
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create(IFormCollection collection)
        {
            try
            {
                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }

        // GET: UsuariController/Edit/5
        public ActionResult Edit(int id)
        {
            return View();
        }

        // POST: UsuariController/Edit/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit(int id, IFormCollection collection)
        {
            try
            {
                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }

        // GET: UsuariController/Delete/5
        public ActionResult Delete(int id)
        {
            return View();
        }

        // POST: UsuariController/Delete/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Delete(int id, IFormCollection collection)
        {
            try
            {
                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }
    }
}
