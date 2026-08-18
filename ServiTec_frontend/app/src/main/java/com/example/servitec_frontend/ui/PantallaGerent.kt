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

class PantallaGerent : AppCompatActivity() {
    private lateinit var btnTancarSessio: TextView
    private lateinit var afegirUsuari : MaterialCardView
    private lateinit var afegirProducte : MaterialCardView
    private lateinit var btnVistaCambrer: MaterialButton
    private lateinit var gestionarUsuaris : MaterialCardView
    private lateinit var gestinarProdcutes : MaterialCardView
    private lateinit var afegirCategories : MaterialCardView
    private lateinit var afegirMenjador : MaterialCardView
    private lateinit var gestionarMenjador : MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pantalla_gerent)
        btnTancarSessio = findViewById(R.id.btnTancarSessio)
        afegirUsuari = findViewById(R.id.cardAfegirUsuari)
        afegirProducte = findViewById(R.id.cardAfegirProducte)
        btnVistaCambrer = findViewById(R.id.btnVistaCambrer)
        gestionarUsuaris = findViewById(R.id.cardGestionarUsuaris)
        gestinarProdcutes = findViewById(R.id.cardGestionarProductes)
        afegirCategories = findViewById(R.id.cardAfegirCategories)
        afegirMenjador = findViewById(R.id.cardAfegirMenjador)
        gestionarMenjador = findViewById(R.id.cardGestionarMenjadors)

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
    private fun tancarSessio() {
        val sharedPreferences = getSharedPreferences("ServiTecPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()

        val intent = Intent(this, PantallaLogin::class.java)
        // Netejar l'historial de pantalles perquè no pugui tornar enrere
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
