package com.mrwhoknows.csgeeks.main_ui.author

import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.main_ui.MainActivity
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.model.Author
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel
import kotlinx.android.synthetic.main.fragment_author.*

class AuthorFragment : Fragment(R.layout.fragment_author) {

    private lateinit var viewModel: BlogViewModel
    private lateinit var authorEmail: String
    private lateinit var authorName: String
    private lateinit var args: AuthorFragmentArgs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel

        args = AuthorFragmentArgs.fromBundle(requireArguments())

//        TODO: Look for this bug
        val authorName = args.authorName
//        val authorName = "RedRanger"
        authorName?.let {
            viewModel.getAuthor(authorName)
            getAuthorData()
        }

    }

    private fun getAuthorData() {
        viewModel.author.observe(viewLifecycleOwner, { authorResource ->
            when (authorResource) {
                is Resource.Success -> {
                    Util.isLoading(bounceLoader, false)

                    val data = authorResource.data?.author
                    data?.let {
                        authorName = data.name
                        authorEmail = data.mail

                        tvAuthorProfileName.text = authorName
                        tvAuthorBio.text = data.bio
                        tvAuthorEmail.text = "email: $authorEmail"

                        var socials = ""
                        for (social: Author.Author.Social in data.social) {
                            socials += "${social.name}: ${social.url}\n\n"
                        }
                        tvAuthorSocials.text = socials
                    }
                    Linkify.addLinks(tvAuthorEmail, Linkify.ALL)
                    Linkify.addLinks(tvAuthorSocials, Linkify.ALL)
                }
                is Resource.Loading -> {
                    Util.isLoading(bounceLoader, true)
                }
                is Resource.Error -> {
                    Util.isLoading(bounceLoader, false)
                    Snackbar.make(requireView(), "Something Went Wrong", Snackbar.LENGTH_SHORT)
                }
            }
        })
    }

    private fun sendMailToAuthor() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, "From CS Geeks Blog App")
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, $authorName\n")
        val emails: Array<String> = arrayOf(authorEmail)
        intent.putExtra(Intent.EXTRA_EMAIL, emails)

        startActivity(Intent.createChooser(intent, "Send Email To $authorName"))
    }
}