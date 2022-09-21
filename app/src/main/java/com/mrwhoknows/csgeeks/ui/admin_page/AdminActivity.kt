package com.mrwhoknows.csgeeks.ui.admin_page

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.databinding.ActivityAdminBinding
import com.mrwhoknows.csgeeks.repository.BlogRepositoryImpl
import com.mrwhoknows.csgeeks.ui.home_page.MainActivity
import com.mrwhoknows.csgeeks.util.Constants
import com.mrwhoknows.csgeeks.util.Constants.AUTHOR_NAME
import com.mrwhoknows.csgeeks.util.Constants.LOGIN_TOKEN
import com.mrwhoknows.csgeeks.util.Constants.NIGHT_MODE
import com.mrwhoknows.csgeeks.util.Constants.TOKEN_SHARED_PREFF
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.collapseKeyboardIfFocusOutsideEditText
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory

private const val TAG = "AdminActivity"

class AdminActivity : AppCompatActivity() {

    private lateinit var appSettingPrefs: SharedPreferences
    private lateinit var adminSharedPrefs: SharedPreferences
    private lateinit var tvHeaderAuthorName: TextView
    private lateinit var tvHeaderAuthorMail: TextView
    private lateinit var ivHeaderAuthorProfile: ImageView
    private val binding by lazy { ActivityAdminBinding.inflate(layoutInflater) }
    lateinit var viewModel: BlogViewModel
    var userToken = ""
    var authorName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.adminToolbar)
        appSettingPrefs = getSharedPreferences(Constants.APP_THEME_SHARED_PREFS, MODE_PRIVATE)
        AppCompatDelegate.setDefaultNightMode(
            appSettingPrefs.getInt(
                NIGHT_MODE,
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
        )

        val blogRepository = BlogRepositoryImpl()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory)[BlogViewModel::class.java]

        adminSharedPrefs = this.getSharedPreferences(TOKEN_SHARED_PREFF, 0)
        userToken = adminSharedPrefs.getString(LOGIN_TOKEN, null).toString()
        authorName = adminSharedPrefs.getString(AUTHOR_NAME, null).toString()

        val navHeaderLayout = binding.adminNavView.getHeaderView(0)
        tvHeaderAuthorName = navHeaderLayout.findViewById<TextView>(R.id.tvHeaderAuthorName).apply {
            visibility = View.VISIBLE
            text = authorName
        }
        ivHeaderAuthorProfile = navHeaderLayout.findViewById<ImageView>(R.id.ivHeaderAuthorProfile).apply {
            visibility = View.VISIBLE
        }
        tvHeaderAuthorMail = navHeaderLayout.findViewById<TextView>(R.id.tvHeaderAuthorMail).apply {
            visibility = View.VISIBLE
        }

        viewModel.getAuthor(authorName)
        viewModel.author.observe(this) { authorResource ->
            when (authorResource) {
                is Resource.Success -> {
                    authorResource.data?.let { data ->
                        Glide.with(baseContext)
                            .load(data.author.profilePhoto)
                            .placeholder(R.drawable.ic_account_circle)
                            .circleCrop()
                            .into(ivHeaderAuthorProfile)
                        tvHeaderAuthorName.text = data.author.name
                        tvHeaderAuthorMail.text = data.author.mail
                    }

                }
                else -> {
                    Log.d(TAG, "onCreate: author image didn't load")
                    Log.d(TAG, "onCreate: author details didn't load")
                }
            }
        }

        setNavMenuItemClicks()
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.adminNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.adminNavView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, binding.adminDrawerLayout)
    }

    private fun setNavMenuItemClicks() {
        binding.adminNavView.menu.getItem(4).setOnMenuItemClickListener {
            setLogoutDialog()
            true
        }
        binding.adminNavView.menu.getItem(3).setOnMenuItemClickListener {
            setAppThemeDialog()
            true
        }
    }

    private fun setAppThemeDialog() {
        val isNightModeOn =
            appSettingPrefs.getInt(NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
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
                        appSettingPrefsEditor.putInt(NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_YES)
                    }
                    1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        appSettingPrefsEditor.putInt(NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    else -> {
                        AppCompatDelegate.setDefaultNightMode(
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        )
                        appSettingPrefsEditor.putInt(
                            NIGHT_MODE,
                            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        )
                    }
                }
                appSettingPrefsEditor.apply()
                dialog.dismiss()
            }.show()
    }

    private fun setLogoutDialog() {
        val dialogClickListener: DialogInterface.OnClickListener =
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        logoutUser()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }

        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Logout")
            .setMessage("Are you sure?")
            .setBackground(
                ContextCompat.getDrawable(
                    baseContext,
                    R.color.colorBackgroundDark3
                )
            )
            .setPositiveButton("Yes, Logout", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()

    }


    private fun logoutUser() {
        val editor = adminSharedPrefs.edit()
        viewModel.logoutUserFromServer(userToken)

        viewModel.logoutUserFromLiveData.observe(this) {
            when (it) {
                is Resource.Success -> {
                    Snackbar.make(binding.adminToolbar, "Log Out Success!", Snackbar.LENGTH_SHORT).show()
                    editor.putString(LOGIN_TOKEN, null).apply()
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                    startActivity(intent)
                    finish()
                }
                is Resource.Error -> {
                    Snackbar.make(binding.adminToolbar, "Something Went Wrong", Snackbar.LENGTH_SHORT)
                        .show()
                }
                is Resource.Loading -> {
                    Snackbar.make(binding.adminToolbar, "Logging Out...", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.adminNavHostFragment)
        return NavigationUI.navigateUp(navController, binding.adminDrawerLayout)
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