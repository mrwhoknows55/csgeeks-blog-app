package com.mrwhoknows.csgeeks.ui.home_page.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.databinding.FragmentLoginBinding
import com.mrwhoknows.csgeeks.ui.home_page.MainActivity
import com.mrwhoknows.csgeeks.util.Constants.LOGIN_TOKEN
import com.mrwhoknows.csgeeks.util.LoginInfo
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel

private const val TAG = "LoginFragment"

class LoginFragment : Fragment() {

    private lateinit var viewModel: BlogViewModel
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (requireActivity() as MainActivity).viewModel

        binding.btLogin.setOnClickListener {
            val username = binding.usernameTextInput.editText!!.text.toString()
            val passwd = binding.passwordTextInput.editText!!.text.toString()

            if (username.isBlank() or passwd.isBlank()) {

                if (username.isBlank()) binding.usernameTextInput.error = "Please Enter Username"
                else binding.usernameTextInput.error = null

                if (passwd.isBlank()) binding.passwordTextInput.error = "Please Enter Password"
                else binding.passwordTextInput.error = null
            } else {
                binding.usernameTextInput.error = null
                binding.passwordTextInput.error = null

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
        viewModel.loginUser.observe(viewLifecycleOwner) { loginResource ->
            when (loginResource) {

                is Resource.Success -> {
                    Log.d(TAG, "token: ${loginResource.data!!.token}")
                    val token = loginResource.data?.token.toString()
                    val authorName = loginResource.data?.author.toString()
                    if (loginResource.data!!.success) {
                        saveLoginToken(true, token, authorName)
                        requireActivity().findNavController(R.id.navHostFragment).navigate(R.id.action_loginFragment_to_adminActivity)
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
                is Resource.Loading -> {

                }
            }
        }
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