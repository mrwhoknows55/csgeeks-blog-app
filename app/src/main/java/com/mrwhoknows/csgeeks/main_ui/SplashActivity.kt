package com.mrwhoknows.csgeeks.main_ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.admin_ui.AdminActivity
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory

private const val TAG = "SplashActivity"

class SplashActivity : AppCompatActivity() {

    lateinit var viewModel: BlogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val main = Intent(this, MainActivity::class.java)
        val admin = Intent(this, AdminActivity::class.java)

        val blogRepository = BlogRepository()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(BlogViewModel::class.java)
        viewModel.getAllArticles()

        val sharedPreferences = this.getSharedPreferences("TOKEN", 0)
        Log.d(TAG, "isLoggedIn: " + sharedPreferences.getBoolean("IS_LOGGED_IN", false))
        if (sharedPreferences.getBoolean("IS_LOGGED_IN", false)) {
            //author or admin user
            val token = sharedPreferences.getString("LOGIN_TOKEN", "empty")
            viewModel.isLoggedUserLoggedIn(token!!)
            viewModel.isLoggedIn.observe(this, Observer {
                if (it is Resource.Success) {
                    navigateToActivity(admin)
                    Log.d(TAG, "called admin")
                }
                if (it is Resource.Error) {
                    navigateToActivity(main)
                    Log.d(TAG, "called main1")
                }
            })
        } else {
            navigateToActivity(main)
            Log.d(TAG, "called main2")
        }
    }

    private fun navigateToActivity(intent: Intent) {
        startActivity(intent)
        finish()
    }
}