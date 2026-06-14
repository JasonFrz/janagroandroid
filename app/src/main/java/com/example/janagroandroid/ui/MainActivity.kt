package com.example.janagroandroid.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
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

        val host = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = host.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddMain)

        bottomNav.setupWithNavController(navController)

        // 1. Role-based Navigation & Guest Handling
        viewModel.user.observe(this) { user ->
            val role = user?.role ?: "Customer"
            
            // Handle Forced Login for specific fragments only
            val currentDest = navController.currentDestination?.id
            val isPublicFragment = currentDest in setOf(
                R.id.homeFragment, 
                R.id.exploreFragment, 
                R.id.profileFragment, 
                R.id.loginFragment, 
                R.id.registerFragment, 
                R.id.splashFragment
            )
            
            if (user == null && !isPublicFragment) {
                navController.navigate(
                    R.id.loginFragment,
                    null,
                    navOptions { popUpTo(R.id.nav_graph) { inclusive = true } }
                )
            }

            // Setup Menu
            val isAdmin = role.equals("Admin", ignoreCase = true)
            val currentMenuRes = if (isAdmin) R.menu.menu_admin else R.menu.menu_customer
            if (bottomNav.menu.size() == 0 || (isAdmin && bottomNav.menu.findItem(R.id.adminHomeFragment) == null) || (!isAdmin && bottomNav.menu.findItem(R.id.adminHomeFragment) != null)) {
                bottomNav.menu.clear()
                bottomNav.inflateMenu(currentMenuRes)
            }

            updateFabVisibility(user, navController.currentDestination?.id)
        }

        fabAdd.bringToFront()

        // 2. UI logic
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateFabVisibility(viewModel.user.value, destination.id)
            
            val isMainFragment = destination.id in setOf(
                R.id.homeFragment, R.id.exploreFragment, R.id.historyFragment, R.id.profileFragment,
                R.id.adminHomeFragment, R.id.adminUsersFragment, R.id.adminMerchantsFragment, 
                R.id.adminVouchersFragment, R.id.adminReportsFragment
            )
            bottomAppBar.visibility = if (isMainFragment) View.VISIBLE else View.GONE
        }

        // 3. FAB Click Listener
        fabAdd.setOnClickListener {
            val user = viewModel.user.value
            if (user == null) {
                Toast.makeText(this, "Silakan login terlebih dahulu!", Toast.LENGTH_SHORT).show()
                navController.navigate(R.id.loginFragment)
            } else if (user.role.equals("Seller", ignoreCase = true)) {
                navController.navigate(R.id.addProductFragment)
            } else {
                Toast.makeText(this, "Hanya penjual yang dapat menambah produk!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateFabVisibility(user: com.example.janagroandroid.data.local.entity.UserEntity?, destId: Int?) {
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddMain)
        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val isSeller = user?.role?.equals("Seller", ignoreCase = true) == true
        val isHome = destId == R.id.homeFragment

        if (isSeller && isHome) {
            fabAdd.show()
            bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
        } else {
            fabAdd.hide()
            bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
        }
    }
}
