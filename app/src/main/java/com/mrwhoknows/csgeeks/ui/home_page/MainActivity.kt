package com.mrwhoknows.csgeeks.ui.home_page

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.Constants
import com.mrwhoknows.csgeeks.util.collapseKeyboardIfFocusOutsideEditText
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var appSettingPrefs: SharedPreferences
    lateinit var viewModel: BlogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)
        appSettingPrefs = getSharedPreferences(Constants.APP_THEME_SHARED_PREFS, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(
            appSettingPrefs.getInt(
                Constants.NIGHT_MODE,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        )

        val blogRepository = BlogRepository()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(BlogViewModel::class.java)

        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupWithNavController(navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        setNavMenuItemClicks()
    }

    private fun setNavMenuItemClicks() {
        navView.menu.getItem(1).setOnMenuItemClickListener {
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
            .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                checkedItem = which
            }
            .setPositiveButton("Apply") { dialog, which ->
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
        return NavigationUI.navigateUp(navController, drawerLayout)
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