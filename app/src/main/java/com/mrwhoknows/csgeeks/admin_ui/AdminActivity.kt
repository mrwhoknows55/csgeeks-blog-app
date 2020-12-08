package com.mrwhoknows.csgeeks.admin_ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.main_ui.MainActivity
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.Constants.AUTHOR_NAME
import com.mrwhoknows.csgeeks.util.Constants.LOGIN_TOKEN
import com.mrwhoknows.csgeeks.util.Constants.TOKEN_SHARED_PREFF
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory
import kotlinx.android.synthetic.main.activity_admin.*

private const val TAG = "AdminActivity"

class AdminActivity : AppCompatActivity() {

    lateinit var viewModel: BlogViewModel
    var USER_TOKEN = ""
    var AUTHOR = ""

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

        val adminSharedPrefs = this.getSharedPreferences(TOKEN_SHARED_PREFF, 0)
        USER_TOKEN = adminSharedPrefs.getString(LOGIN_TOKEN, "empty").toString()
        AUTHOR = adminSharedPrefs.getString(AUTHOR_NAME, "empty").toString()

        initLogoutInMenu()
        val navController = findNavController(R.id.adminNavHostFragment)
        NavigationUI.setupWithNavController(adminNavView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, adminDrawerLayout)
    }

    private fun initLogoutInMenu() {
        adminNavView.menu.getItem(3).setOnMenuItemClickListener {

            val dialogClickListener: DialogInterface.OnClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            logoutUser()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                        }
                    }
                }

            val builder = MaterialAlertDialogBuilder(this)
            builder.setMessage("Are you sure?")
                .setBackground(ContextCompat.getDrawable(baseContext, R.color.colorBackgroundDark3))
                .setPositiveButton("Yes, Logout", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()

            true
        }
    }

    private fun logoutUser() {
        viewModel.logoutUserFromServer(USER_TOKEN)

        viewModel.logoutUserFromLiveData.observe(this, Observer {
            when (it) {
                is Resource.Success -> {
                    Snackbar.make(adminToolbar, "Log Out Success!", Snackbar.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    Snackbar.make(adminToolbar, "Something Went Wrong", Snackbar.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.adminNavHostFragment)
        return NavigationUI.navigateUp(navController, adminDrawerLayout)
    }
}