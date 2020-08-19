package com.mrwhoknows.csgeeks.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.MainActivity
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_login.*

private const val TAG = "LoginFragment"

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        btLogin.setOnClickListener {
            //TODO add checks
            val username = tvUsername.editText!!.text.toString()
            val passwd = tvPassword.editText!!.text.toString()

            viewModel.loginUserToServer(username, passwd)

            //TODO add other stuff
            viewModel.loginUser.observe(viewLifecycleOwner, Observer { loginResource ->
                when (loginResource) {

                    is Resource.Success -> {
                        Snackbar.make(requireView(), "Login Success token: ${loginResource.data!!.token}", Snackbar.LENGTH_SHORT).show()
                        Log.d(TAG, "token: ${loginResource.data!!.token}")
                    }

                }
            })
        }
    }
}