package com.mrwhoknows.csgeeks.admin_ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.mrwhoknows.csgeeks.R
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_main.*

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        setSupportActionBar(adminToolbar)

        val navController = findNavController(R.id.adminNavHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
        NavigationUI.setupWithNavController(adminNavView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, adminDrawerLayout)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.adminNavHostFragment)
        return NavigationUI.navigateUp(navController, adminDrawerLayout)
    }
}