/**
 * @file login.component.ts
 * @description Component principal de la pantalla de login.
 *              Gestiona el formulari d'autenticació i mostra la llista
 *              d'usuaris obtinguda de la API en carregar la pàgina.
 *              TODO: implementar autenticació real contra el backend.
 * @author ServiTec
 */

// Importem Component per definir el component i OnInit per al cicle de vida
import { Component, OnInit } from '@angular/core';
// CommonModule activa *ngIf i *ngFor al template
import { CommonModule } from '@angular/common';
// FormsModule activa [(ngModel)] per vincular els inputs amb les variables
import { FormsModule } from '@angular/forms';
// Importem el servei que gestiona les crides HTTP dels usuaris
import { UserService } from '../../services/user.service';
// Importem la interfície User per tipar correctament les dades
import { User } from '../../models/user.model';

@Component({
  // Etiqueta HTML que representa aquest component
  selector: 'app-login',
  // El component es gestiona sol, sense NgModule
  standalone: true,
  // Mòduls necessaris per al template d'aquest component
  imports: [CommonModule, FormsModule],
  // Ruta al fitxer HTML del component
  templateUrl: './login.component.html',
  // Ruta al fitxer d'estils del component
  styleUrls: ['./login.component.scss']
})
// Implementem OnInit per executar codi just després de crear el component
export class LoginComponent implements OnInit {
  // Variable vinculada a l'input de nom d'usuari via [(ngModel)]
  usuario: string = '';
  // Variable vinculada a l'input de contrasenya via [(ngModel)]
  password: string = '';
  // Array que emmagatzema la llista d'usuaris obtinguda de la API
  usuarios: User[] = [];
  // Controla si es mostra el spinner de càrrega
  cargando: boolean = false;
  // Emmagatzema el missatge d'error si alguna crida falla
  error: string = '';

  // Angular injecta automàticament el UserService al constructor
  constructor(private userService: UserService) {}

  // Angular executa aquest mètode automàticament en carregar el component
  ngOnInit(): void {
    this.cargarUsuarios();
  }

  // Crida al servei per obtenir la llista d'usuaris de la API
  cargarUsuarios(): void {
    // Activem el spinner mentre esperem la resposta
    this.cargando = true;
    this.userService.getUsers().subscribe({
      // Si la crida va bé, guardem les dades i desactivem el spinner
      next: (data) => {
        this.usuarios = data;
        this.cargando = false;
      },
      // Si la crida falla, mostrem l'error i desactivem el spinner
      error: (err) => {
        this.error = 'No es pot connectar amb el servidor.';
        this.cargando = false;
        console.error(err);
      }
    });
  }

  // Gestiona l'intent d'inici de sessió
  // TODO: afegir autenticació real contra el backend
  login(): void {
    // Validem que els camps no estiguin buits
    if (!this.usuario || !this.password) {
      this.error = 'Si us plau, introdueix usuari i contrasenya.';
      return;
    }
    this.error = '';
    console.log('Login amb:', this.usuario);
  }
}