package com.mrwhoknows.csgeeks.ui.home_page

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.repository.BlogRepository
import com.mrwhoknows.csgeeks.util.collapseKeyboardIfFocusOutsideEditText
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: BlogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainToolbar)

        val blogRepository = BlogRepository()
        val viewModelFactory =
            BlogViewModelFactory(
                blogRepository
            )
        viewModel = ViewModelProvider(this, viewModelFactory).get(BlogViewModel::class.java)

        val navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupWithNavController(navView, navController)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
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