package com.example.presentation.audiolist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data.services.AudioService
import com.example.presentation.R
import com.example.presentation.audiolist.adapter.AudioListAdapter
import com.example.presentation.databinding.FragmentAudioListBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioListFragment : Fragment() {

    private val viewModel: AudioListViewModel by viewModel()
    private val adapter = AudioListAdapter()
    private var _binding: FragmentAudioListBinding? = null
    private val binding: FragmentAudioListBinding
    get() = _binding ?: throw RuntimeException("AudioListBinding is null")

    private val reqPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it) {
            initAdapter()
            getAudioList()
            observers()
        } else {
            Toast.makeText(
                requireContext(),
                "READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTTINGS",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!checkPermission()){
            requestPermission()
            return
        }
        initAdapter()
        getAudioList()
        observers()
    }

    private fun initAdapter(){
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        adapter.onPlayClickListener = { data , name, byteArray ->
            val stopIntent = Intent(requireContext(), AudioService::class.java)
            requireContext().stopService(stopIntent)
            ContextCompat.startForegroundService(requireContext(),AudioService.newIntent(requireContext(), data, name, byteArray))
        }
    }

    private fun getAudioList(){
        viewModel.getAudioList()
    }

    private fun observers(){
        viewModel.audioList.observe(viewLifecycleOwner){
            adapter.updateData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(
                requireContext(),
                "READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTTINGS",
                Toast.LENGTH_SHORT
            ).show()
        } else reqPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

}