package com.example.presentation.audiodetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.presentation.R
import com.example.presentation.databinding.FragmentAudioDetailsBinding

class AudioDetailsFragment : Fragment() {

    private var _binding: FragmentAudioDetailsBinding? = null
    private val binding: FragmentAudioDetailsBinding
    get() = _binding ?: throw RuntimeException("AudioDetailsBinding is null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}