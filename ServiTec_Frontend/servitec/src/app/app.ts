/**
 * @file app.ts
 * @description Component arrel de l'aplicació ServiTec.
 *              És el contenidor principal on Angular carrega
 *              el component corresponent a la ruta activa.
 *              No conté lògica pròpia, només fa de marc.
 * @author ServiTec
 */

// Importem el decorador per definir un component Angular
import { Component } from '@angular/core';
// Importem RouterOutlet, que és el "marc" on Angular carrega el component de la ruta activa
import { RouterOutlet } from '@angular/router';

@Component({
  // Etiqueta HTML que representa aquest component (usada a index.html)
  selector: 'app-root',
  // El component es gestiona sol, sense NgModule
  standalone: true,
  // Declarem que usem RouterOutlet al template
  imports: [RouterOutlet],
  // Template mínim: només el forat on Angular injectarà cada pàgina
  template: `<router-outlet />`
})
// Classe buida, aquest component només fa de contenidor
export class AppComponent {}