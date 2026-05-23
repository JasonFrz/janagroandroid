package com.example.janagroandroid.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.janagroandroid.R
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi Navigasi
        val host = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = host.navController

        // Tampilkan Splash di awal jika baru pertama kali buka
        if (savedInstanceState == null) {
            navController.navigate(R.id.splashFragment)
        }

        // 2. Inisialisasi View
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddMain)

        // 3. Setup Navigasi
        bottomNav.setupWithNavController(navController)

        // Memastikan FAB berada di depan layer BottomNav
        fabAdd.bringToFront()

        // Matikan fungsi klik pada item dummy di tengah (placeholder)
        bottomNav.menu.findItem(R.id.placeholder)?.isEnabled = false

        // 4. Logika Sembunyi/Muncul Bar & FAB
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Tentukan di mana bar harus muncul (Hanya di fragment utama, bukan di Splash)
            val showBar = destination.id in setOf(
                R.id.homeFragment,
                R.id.cartFragment,
                R.id.historyFragment,
                R.id.profileFragment
            )

            if (showBar) {
                bottomAppBar.visibility = View.VISIBLE
                fabAdd.show()
                fabAdd.bringToFront()
            } else {
                bottomAppBar.visibility = View.GONE
                fabAdd.hide()
            }
        }

        // 5. Listener Tombol Plus
        fabAdd.setOnClickListener {
            Toast.makeText(this, "Silakan login untuk menambah produk!", Toast.LENGTH_SHORT).show()
        }
    }
}