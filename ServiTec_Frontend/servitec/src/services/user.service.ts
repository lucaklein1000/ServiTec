/**
 * @file user.service.ts
 * @description Servei encarregat de gestionar totes les crides HTTP
 *              relacionades amb els usuaris del sistema.
 *              Actua com a intermediari entre els components i la API de backend.
 *              Si la URL de la API canvia, només cal modificar aquest fitxer.
 * @author ServiTec
 */

// Importem el decorador per marcar la classe com a servei injectable
import { Injectable } from '@angular/core';
// Importem el client HTTP d'Angular per fer crides a la API
import { HttpClient } from '@angular/common/http';
// Importem Observable de RxJS per gestionar respostes asíncrones
import { Observable } from 'rxjs';
// Importem la interfície User per tipar correctament les dades
import { User } from '../models/user.model';
// Importem les variables d'entorn per obtenir la URL base de la API
import { environment } from '../environments/environment';

// Servei disponible globalment, Angular crea una única instància per a tota l'app
@Injectable({
  providedIn: 'root'
})
export class UserService {
  // URL completa de l'endpoint per obtenir els usuaris
  private apiUrl = `${environment.apiUrl}/api/Usuari/llistar`;

  // Angular injecta automàticament el HttpClient al constructor
  constructor(private http: HttpClient) {}

  // Retorna un Observable amb la llista d'usuaris obtinguda de la API
  // La crida HTTP no s'executa fins que algú faci .subscribe()
  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }
}