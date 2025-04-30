package com.example.gym

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.gym.databinding.ActivityMainBinding
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //toolbar === barra superior
        setSupportActionBar(binding.appBarMain.toolbar)



        //drawerlaoyut  === Menú  desplegable lateral
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        val navInflater = navController.navInflater

        val graph = if (FirebaseAuth.getInstance().currentUser != null) {
            navInflater.inflate(R.navigation.mobile_navigation)
        } else {
            navInflater.inflate(R.navigation.auth_navigation)
        }
        navController.graph = graph

        if (graph.id == R.id.mobile_navigation) {
            appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.nav_profile, R.id.btn_open_timer, R.id.nav_calendario
                ), drawerLayout
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            val headerView = navView.getHeaderView(0)
            val nameTextView = headerView.findViewById<TextView>(R.id.nav_header_user_name)
            val emailTextView = headerView.findViewById<TextView>(R.id.nav_header_email)

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                FirebaseFirestore.getInstance().collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            nameTextView.text = document.getString("userName") ?: "Sin nombre"
                            emailTextView.text = document.getString("email") ?: "Sin correo"
                        }
                    }
            }
        } else {
            // Usuario en login/register, Drawer bloqueado
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            navView.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}