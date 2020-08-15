package com.mrwhoknows.csgeeks.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mrwhoknows.csgeeks.R
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment(R.layout.fragment_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView.text = "LOGIN FRAG"
    }
}