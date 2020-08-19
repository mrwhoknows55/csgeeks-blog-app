package com.mrwhoknows.csgeeks.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mrwhoknows.csgeeks.MainActivity
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }
}