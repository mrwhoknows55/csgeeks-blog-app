package com.mrwhoknows.csgeeks.ui.home_page.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.ui.home_page.MainActivity
import com.mrwhoknows.csgeeks.util.Constants.LOGIN_TOKEN
import com.mrwhoknows.csgeeks.util.LoginInfo
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
            val username = usernameTextInput.editText!!.text.toString()
            val passwd = passwordTextInput.editText!!.text.toString()

            if (username.isBlank() or passwd.isBlank()) {

                if (username.isBlank()) usernameTextInput.error = "Please Enter Username"
                else usernameTextInput.error = null

                if (passwd.isBlank()) passwordTextInput.error = "Please Enter Password"
                else passwordTextInput.error = null
            } else {
                usernameTextInput.error = null
                passwordTextInput.error = null

                //Hide kyb when clicked on submit
                val kbd =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                kbd.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)

                viewModel.loginUserToServer(username, passwd)
                observeLogin()
            }
        }
    }

    private fun observeLogin() {
        viewModel.loginUser.observe(viewLifecycleOwner, { loginResource ->
            when (loginResource) {

                is Resource.Success -> {
                    Log.d(TAG, "token: ${loginResource.data!!.token}")
                    val token = loginResource.data?.token.toString()
                    val authorName = loginResource.data?.author.toString()
                    if (loginResource.data!!.success) {
                        saveLoginToken(true, token, authorName)
                        findNavController().navigate(R.id.action_loginFragment_to_adminActivity)
                        requireActivity().finish()
                    } else {
                        Snackbar.make(
                            requireView(),
                            loginResource.data!!.response,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }

                is Resource.Error -> {
                    Snackbar.make(
                        requireView(),
                        "Login Failed",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    Log.d(TAG, "error: ${loginResource.message!!}")
                    saveLoginToken(false, null, "")
                }

            }
        })
    }

    private fun saveLoginToken(isLoginSuccess: Boolean, loginToken: String?, authorName: String?) {
        val loginInfo = LoginInfo(requireActivity())

        if (isLoginSuccess) {
            loginInfo.editor.clear().remove(LOGIN_TOKEN)
            loginInfo.setToken(loginToken!!)
            loginInfo.setAuthorName(authorName!!)
            loginInfo.setIsLoggedIn(isLoginSuccess)
        } else
            loginInfo.setIsLoggedIn(isLoginSuccess)
    }
}