package com.elkdocs.handwritter.presentation.about_screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.elkdocs.handwritter.R
import com.elkdocs.handwritter.databinding.FragmentAboutBinding
import com.elkdocs.handwritter.databinding.FragmentMainBinding


class AboutFragment : Fragment() {
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(layoutInflater)
       return binding.root
    }


}