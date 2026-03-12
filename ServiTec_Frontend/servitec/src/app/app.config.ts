/**
 * @file app.config.ts
 * @description Configuració global de l'aplicació ServiTec.
 *              Defineix els serveis disponibles a tota l'app:
 *              el sistema de navegació entre pàgines i el client HTTP
 *              per fer crides a la API de backend.
 * @author ServiTec
 */

// Importem el tipus que defineix com ha de ser una configuració Angular
import { ApplicationConfig } from '@angular/core';
// Importem la funció que activa el sistema de navegació entre pàgines
import { provideRouter } from '@angular/router';
// Importem la funció que activa el client HTTP per fer crides a la API
import { provideHttpClient } from '@angular/common/http';
// Importem les rutes definides a app.routes.ts
import { routes } from './app.routes';

// Configuració global exportada per ser usada a main.ts
export const appConfig: ApplicationConfig = {
  providers: [
    // Activa el sistema de navegació entre pàgines amb les rutes definides
    provideRouter(routes),
    // Activa el client HTTP globalment per a tots els serveis de l'app
    provideHttpClient()
  ]
};