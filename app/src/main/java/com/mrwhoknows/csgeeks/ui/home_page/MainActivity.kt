package com.mrwhoknows.csgeeks.ui.home_page

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.databinding.ActivityMainBinding
import com.mrwhoknows.csgeeks.repository.BlogRepositoryImpl
import com.mrwhoknows.csgeeks.util.Constants
import com.mrwhoknows.csgeeks.util.collapseKeyboardIfFocusOutsideEditText
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var appSettingPrefs: SharedPreferences
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var viewModel: BlogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        appSettingPrefs = getSharedPreferences(Constants.APP_THEME_SHARED_PREFS, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(
            appSettingPrefs.getInt(
                Constants.NIGHT_MODE,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        )

        val blogRepository = BlogRepositoryImpl()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory)[BlogViewModel::class.java]

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.mainNavView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        val navHeaderLayout = binding.mainNavView.getHeaderView(0)

        navHeaderLayout.findViewById<TextView>(R.id.tvHeaderAuthorName).apply {
            visibility = View.VISIBLE
            text = this.resources.getString(R.string.regular_usr)
        }
        navHeaderLayout.findViewById<ImageView>(R.id.ivHeaderAuthorProfile).apply {
            visibility = View.VISIBLE
            setImageDrawable(ContextCompat.getDrawable(baseContext, R.drawable.ic_account_circle))
        }

        navHeaderLayout.findViewById<TextView>(R.id.tvHeaderAuthorMail).apply {
            visibility = View.VISIBLE
            text = this.resources.getString(R.string.login_if_author_msg)
        }
        setNavMenuItemClicks()
    }

    private fun setNavMenuItemClicks() {
        binding.mainNavView.menu.getItem(1).setOnMenuItemClickListener {
            setAppThemeDialog()
            true
        }
    }

    private fun setAppThemeDialog() {
        val isNightModeOn =
            appSettingPrefs.getInt(Constants.NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val appSettingPrefsEditor = appSettingPrefs.edit()

        AppCompatDelegate.setDefaultNightMode(isNightModeOn)
        var checkedItem: Int = when (isNightModeOn) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> 2
            AppCompatDelegate.MODE_NIGHT_YES -> 0
            AppCompatDelegate.MODE_NIGHT_NO -> 1
            else -> 2
        }
        val singleItems = arrayOf("Dark", "Light", "Auto")

        MaterialAlertDialogBuilder(this)
            .setTitle("App Theme")
            .setBackground(ContextCompat.getDrawable(baseContext, R.color.colorBackgroundDark3))
            .setNeutralButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                checkedItem = which
            }
            .setPositiveButton("Apply") { dialog, _ ->
                when (checkedItem) {
                    0 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        appSettingPrefsEditor.putInt(
                            Constants.NIGHT_MODE,
                            AppCompatDelegate.MODE_NIGHT_YES
                        )
                    }
                    1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        appSettingPrefsEditor.putInt(
                            Constants.NIGHT_MODE,
                            AppCompatDelegate.MODE_NIGHT_NO
                        )
                    }
                    else -> {
                        AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        )
                        appSettingPrefsEditor.putInt(
                            Constants.NIGHT_MODE,
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        )
                    }
                }
                appSettingPrefsEditor.apply()
                dialog.dismiss()
            }.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.navHostFragment)
        return NavigationUI.navigateUp(navController, binding.drawerLayout)
    }

    //    Collapse the keyboard when the user taps outside the EditText
    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {

        currentFocus?.let { oldFocus ->
            super.dispatchTouchEvent(motionEvent)
            val newFocus = currentFocus ?: oldFocus
            collapseKeyboardIfFocusOutsideEditText(motionEvent, oldFocus, newFocus)
        }
        return super.dispatchTouchEvent(motionEvent)
    }
}