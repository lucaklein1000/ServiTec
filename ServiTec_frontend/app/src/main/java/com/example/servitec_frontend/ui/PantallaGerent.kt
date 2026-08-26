// ============================================================================
// Projecte:      ServiTec - Sistema de Gestió de Restaurants (TFG)
// Autor:         Luca Klein
// Titulació:     Grau en Enginyeria Informàtica (4t Curs)
// Institució:    Universitat de Girona (UdG)
// Fitxer:        PantallaGerent.kt
// Descripció:    Activity del panell principal d'administració/gerència. Permet 
//                navegar cap a les funcionalitats d'alta i gestió d'usuaris, 
//                productes, categories, menjadors, accedir a la vista de cambrer 
//                i gestionar el tancament de sessió.
// ============================================================================

package com.example.servitec_frontend.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.servitec_frontend.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.example.servitec_frontend.ui.PantallaGestionarMenjadors

/**
 * Activity que actua com a menú principal per als usuaris amb rol de gerent o administrador.
 * Proporciona accés centralitzat a la gestió del sistema i a l'enrutament de les diferents pantalles.
 */
class PantallaGerent : AppCompatActivity() {

    // Targetes de navegació per a accions de creació i gestió
    private lateinit var btnTancarSessio: TextView
    private lateinit var afegirUsuari: MaterialCardView
    private lateinit var afegirProducte: MaterialCardView
    private lateinit var btnVistaCambrer: MaterialButton
    private lateinit var gestionarUsuaris: MaterialCardView
    private lateinit var gestinarProdcutes: MaterialCardView
    private lateinit var afegirCategories: MaterialCardView
    private lateinit var afegirMenjador: MaterialCardView
    private lateinit var gestionarMenjador: MaterialCardView

    /**
     * Inicialitza la pantalla del panell de gerència, enllaça els components de la interfície i assigna els esdeveniments de navegació.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_gerent)

        // Vinculació dels elements de la interfície d'usuari
        btnTancarSessio = findViewById(R.id.btnTancarSessio)
        afegirUsuari = findViewById(R.id.cardAfegirUsuari)
        afegirProducte = findViewById(R.id.cardAfegirProducte)
        btnVistaCambrer = findViewById(R.id.btnVistaCambrer)
        gestionarUsuaris = findViewById(R.id.cardGestionarUsuaris)
        gestinarProdcutes = findViewById(R.id.cardGestionarProductes)
        afegirCategories = findViewById(R.id.cardAfegirCategories)
        afegirMenjador = findViewById(R.id.cardAfegirMenjador)
        gestionarMenjador = findViewById(R.id.cardGestionarMenjadors)

        // Configuració dels escoltadors d'esdeveniments de navegació
        btnTancarSessio.setOnClickListener {
            tancarSessio()
        }

        btnVistaCambrer.setOnClickListener {
            startActivity(Intent(this, PantallaPanell::class.java))
        }

        afegirUsuari.setOnClickListener {
            startActivity(Intent(this, PantallaAfegirUsuari::class.java))
        }

        afegirProducte.setOnClickListener {
            startActivity(Intent(this, PantallaAfegirProducte::class.java))
        }

        gestionarUsuaris.setOnClickListener {
            startActivity(Intent(this, PantallaGestionarPersonal::class.java))
        }

        gestinarProdcutes.setOnClickListener {
            startActivity(Intent(this, PantallaGestionarProductes::class.java))
        }

        afegirCategories.setOnClickListener {
            startActivity(Intent(this, PantallaAfegirCategoria::class.java))
        }

        afegirMenjador.setOnClickListener {
            startActivity(Intent(this, PantallaAfegirMenjador::class.java))
        }

        gestionarMenjador.setOnClickListener {
            startActivity(Intent(this, PantallaGestionarMenjadors::class.java))
        }
    }

    /**
     * Neteja les dades de la sessió desades a SharedPreferences i redirigeix l'usuari a la pantalla de login netejant la pila d'activitats.
     */
    private fun tancarSessio() {
        // Esborrat de les credencials i el token JWT emmagatzemats
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        // Redirecció a la pantalla d'inici de sessió netejant l'historial d'activitats
        val intent = Intent(this, PantallaLogin::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}