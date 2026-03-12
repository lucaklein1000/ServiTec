/**
 * @file app.routes.ts
 * @description Defineix el mapa de navegació de l'aplicació ServiTec.
 *              Cada ruta associa una URL amb el component que s'ha de mostrar.
 *              Aquí s'aniran afegint les noves pantalles de l'aplicació
 *              (comandas, inventari, etc.) a mesura que es desenvolupin.
 * @author ServiTec
 */

// Importem el tipus que defineix com ha de ser un array de rutes
import { Routes } from '@angular/router';
// Importem el component que es mostrarà a la ruta /login
import { LoginComponent } from '../pages/login/login.component';

// Array de rutes exportat per ser usat a app.config.ts
export const routes: Routes = [
  // Redirigeix la ruta arrel cap a /login automàticament
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  // Ruta de login: carrega el LoginComponent quan la URL és /login
  { path: 'login', component: LoginComponent },
];