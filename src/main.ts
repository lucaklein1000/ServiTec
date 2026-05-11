/**
 * @file main.ts
 * @description Punt d'entrada principal de l'aplicació ServiTec.
 *              És el primer fitxer que s'executa en carregar la web.
 *              S'encarrega d'arrencar l'aplicació Angular amb el component
 *              arrel i la configuració global.
 * @author ServiTec
 */

// Importem la funció que arrenca l'aplicació al navegador
import { bootstrapApplication } from '@angular/platform-browser';
// Importem la configuració global (rutes, HTTP...)
import { appConfig } from './app/app.config';
// Importem el component arrel que conté tota l'aplicació
import { AppComponent } from './app/app';

// Arranquem l'aplicació amb el component arrel i la configuració global
bootstrapApplication(AppComponent, appConfig)
  // Si alguna cosa falla en arrencar, ho mostrem per consola
  .catch((err) => console.error(err));