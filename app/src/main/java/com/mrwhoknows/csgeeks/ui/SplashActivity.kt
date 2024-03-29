package com.mrwhoknows.csgeeks.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.mrwhoknows.csgeeks.databinding.ActivitySplashBinding
import com.mrwhoknows.csgeeks.repository.BlogRepositoryImpl
import com.mrwhoknows.csgeeks.ui.admin_page.AdminActivity
import com.mrwhoknows.csgeeks.ui.home_page.MainActivity
import com.mrwhoknows.csgeeks.util.Constants
import com.mrwhoknows.csgeeks.util.LoginInfo
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory

private const val TAG = "SplashActivity"

class SplashActivity : AppCompatActivity() {

    lateinit var viewModel: BlogViewModel
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val appSettingPrefs = getSharedPreferences(Constants.APP_THEME_SHARED_PREFS, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(
            appSettingPrefs.getInt(
                Constants.NIGHT_MODE,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        )

        val loginInfo = LoginInfo(this)

        val main = Intent(this, MainActivity::class.java)
        val admin = Intent(this, AdminActivity::class.java)

        val blogRepository = BlogRepositoryImpl()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory)[BlogViewModel::class.java]
        splashScreen.setKeepOnScreenCondition { true }

        Log.d(TAG, "isLoggedIn: " + loginInfo.isLoggedIn)
        if (loginInfo.isLoggedIn) {
            //author or admin user
            val token = loginInfo.loginToken

            if ((token != null) and (token != "null")) {
                viewModel.isLoggedUserLoggedIn(token!!)
            } else {
                navigateToActivity(main)
            }
            viewModel.isLoggedIn.observe(this) {
                when (it) {
                    is Resource.Success -> {
                        if (it.data!!.success)
                            navigateToActivity(admin)
                        else
                            navigateToActivity(main)
                    }
                    is Resource.Error -> {
                        navigateToActivity(main)
                        Log.d(TAG, "called main1")
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "onCreate: loading")
                    }
                }
            }
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