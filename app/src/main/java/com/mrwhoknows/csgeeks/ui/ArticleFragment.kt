package com.mrwhoknows.csgeeks.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mrwhoknows.csgeeks.R
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var args: ArticleFragmentArgs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args = ArticleFragmentArgs.fromBundle(requireArguments())

        tvID.text = args.articleID
    }
}