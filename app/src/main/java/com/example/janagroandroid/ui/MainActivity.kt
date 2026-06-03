package com.example.janagroandroid.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.janagroandroid.R
import com.example.janagroandroid.di.AppGraph
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels {
        AppViewModelFactory(application, AppGraph.repository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi Navigasi
        val host = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = host.navController

        // 2. Inisialisasi View
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddMain)

        // 3. Setup Navigasi
        bottomNav.setupWithNavController(navController)

        // 3.1. Role-based Bottom Navigation
        viewModel.user.observe(this) { user ->
            val role = user?.role ?: "Customer"
            
            // Handle Logout or Role Change
            val currentDest = navController.currentDestination?.id
            if (user == null && currentDest != R.id.homeFragment && currentDest != R.id.loginFragment && currentDest != R.id.registerFragment && currentDest != R.id.splashFragment) {
                // Arahkan langsung ke Home (Guest) saat logout
                navController.navigate(R.id.loginFragment) {
                    popUpTo(R.id.nav_graph) { inclusive = true }
                }
            }

            // Reset menu only if changed to avoid losing state
            val currentMenuRes = if (role == "Admin") R.menu.menu_admin else R.menu.menu_customer
            
            // Simpan menu lama untuk perbandingan
            val isMenuAdmin = bottomNav.menu.findItem(R.id.adminHomeFragment) != null
            val shouldBeAdmin = role == "Admin"
            
            if (isMenuAdmin != shouldBeAdmin || bottomNav.menu.size() == 0) {
                bottomNav.menu.clear()
                bottomNav.inflateMenu(currentMenuRes)
            }

            // Selalu sembunyikan FAB untuk Customer/Guest sesuai permintaan
            // Hanya tampilkan jika role adalah "Seller" (jika ada nantinya)
            if (role == "Admin" || role == "Customer" || user == null) {
                fabAdd.hide()
                bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
            } else {
                // Misal untuk role lain yang butuh FAB +
                fabAdd.show()
                bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            }
        }

        // Memastikan FAB berada di depan layer BottomNav
        fabAdd.bringToFront()

        // 4. Logika Sembunyi/Muncul Bar & FAB
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val user = viewModel.user.value
            val role = user?.role ?: "Customer"
            val isAdmin = role == "Admin"

            // Tentukan di mana bar harus muncul
            val isCustomerMainFragment = destination.id in setOf(
                R.id.homeFragment,
                R.id.exploreFragment,
                R.id.cartFragment,
                R.id.historyFragment,
                R.id.profileFragment
            )
            
            val isAdminMainFragment = destination.id in setOf(
                R.id.adminHomeFragment,
                R.id.adminUsersFragment,
                R.id.adminMerchantsFragment,
                R.id.adminVouchersFragment,
                R.id.adminReportsFragment
            )

            if (isCustomerMainFragment || isAdminMainFragment) {
                bottomAppBar.visibility = View.VISIBLE
                
                // FAB hanya muncul untuk role tertentu (misal: Seller)
                // Customer, Guest, dan Admin TIDAK memunculkan FAB sesuai permintaan
                if (role == "Seller" && destination.id == R.id.homeFragment) {
                    fabAdd.show()
                } else {
                    fabAdd.hide()
                }

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