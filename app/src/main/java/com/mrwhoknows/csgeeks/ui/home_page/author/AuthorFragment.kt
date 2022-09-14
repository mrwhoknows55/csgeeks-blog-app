package com.mrwhoknows.csgeeks.ui.home_page.author

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mrwhoknows.csgeeks.R
import com.mrwhoknows.csgeeks.databinding.FragmentAuthorBinding
import com.mrwhoknows.csgeeks.ui.home_page.MainActivity
import com.mrwhoknows.csgeeks.util.Resource
import com.mrwhoknows.csgeeks.util.Util
import com.mrwhoknows.csgeeks.viewmodels.BlogViewModel

private const val TAG = "AuthorFragment"

class AuthorFragment : Fragment() {

    private lateinit var viewModel: BlogViewModel
    private lateinit var authorName: String
    private lateinit var binding: FragmentAuthorBinding
    private val args: AuthorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (requireActivity() as MainActivity).viewModel

        val authorName = args.authorName
        Log.d(TAG, "authorName: $authorName")

        authorName?.let {
            viewModel.getAuthor(authorName)
            getAuthorData()
        }

    }

    private fun getAuthorData() {
        viewModel.author.observe(viewLifecycleOwner) { authorResource ->
            when (authorResource) {
                is Resource.Success -> {
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)

                    val data = authorResource.data?.author
                    data?.let { author ->
                        authorName = data.name

                        binding.tvAuthorProfileName.text = authorName
                        binding.tvAuthorBio.text = data.bio

                        Glide.with(requireContext())
                            .load(author.profilePhoto)
                            .placeholder(R.drawable.ic_account_circle)
                            .circleCrop()
                            .into(binding.ivAuthorProfilePic)

                        val authorEmail = data.mail
                        binding.ivMailIcon.setOnClickListener {
                            openLinkInBrowser("mailto:$authorEmail")
                        }

                        val socials = data.social
                        binding.ivInstagramIcon.setOnClickListener {
                            openLinkInBrowser(socials[0].url)
                        }

                        binding.ivTwitterIcon.setOnClickListener {
                            openLinkInBrowser(socials[1].url)
                        }

                        binding.ivGithubIcon.setOnClickListener {
                            openLinkInBrowser(socials[2].url)
                        }

                    }
                }
                is Resource.Loading -> {
                    Util.isLoading(binding.bounceLoader, true)
                    Util.isLoading(binding.bounceLoaderBG, true)
                }
                is Resource.Error -> {
                    Util.isLoading(binding.bounceLoader, false)
                    Util.isLoading(binding.bounceLoaderBG, false)
                    Snackbar.make(requireView(), "Something Went Wrong", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openLinkInBrowser(socialUrl: String) {
        Log.d(TAG, "openLinkInBrowser: $socialUrl")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(socialUrl)
        startActivity(intent)
    }
}