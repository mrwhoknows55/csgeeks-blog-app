package com.mrwhoknows.csgeeks.admin_ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.main_ui.MainActivity
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory
import kotlinx.android.synthetic.main.activity_admin.*

class AdminActivity : AppCompatActivity() {

    lateinit var viewModel: BlogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        setSupportActionBar(adminToolbar)

        val blogRepository = BlogRepository()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(BlogViewModel::class.java)

        initLogoutInMenu()
        val navController = findNavController(R.id.adminNavHostFragment)
        NavigationUI.setupWithNavController(adminNavView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, adminDrawerLayout)
    }

    private fun initLogoutInMenu() {
        adminNavView.menu.getItem(3).setOnMenuItemClickListener {

            val sharedPreferences = this.getSharedPreferences("TOKEN", 0)
            val editor = sharedPreferences.edit()

            editor.remove("LOGIN_TOKEN")
            editor.apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.adminNavHostFragment)
        return NavigationUI.navigateUp(navController, adminDrawerLayout)
    }
}