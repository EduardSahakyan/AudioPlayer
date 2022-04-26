package com.example.presentation.audiolist

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.data.models.AudioModel
import com.example.data.services.AudioService
import com.example.presentation.R
import com.example.presentation.audiolist.adapter.AudioListAdapter
import com.example.presentation.databinding.FragmentAudioListBinding
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream

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
                "READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTINGS",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private lateinit var mService: AudioService
    private var mBound: Boolean = false
    private var selectedAudio = 0

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as AudioService.LocalBinder
            mService = binder.getService()
            mService.changePlayPauseButton = { isPlaying ->
                if(isPlaying)
                    binding.playPauseBtn.setImageResource(android.R.drawable.ic_media_pause)
                else
                    binding.playPauseBtn.setImageResource(android.R.drawable.ic_media_play)
            }
            mService.stopServiceAndHideMusicBar = {
                selectedAudio = -1
                binding.constraintLayout.visibility = View.GONE
            }
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
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
        listeners()
    }

    private fun initAdapter(){
        binding.apply {
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        adapter.onPlayClickListener = { position ->
            if(selectedAudio != position) {
                if (!mBound) {
                    startService(viewModel.audioList.value!!)
                    lifecycleScope.launch {
                        delay(550)
                        mService.onTrackPlay(position)
                    }
                } else
                    mService.onTrackPlay(position)
                viewModel.updateSelectedTrack(position)
            }
        }
    }

    private fun startService(
        audioList: List<AudioModel>
    ) {
        val intent = Intent(requireContext(), AudioService::class.java)
        ContextCompat.startForegroundService(requireContext(), intent)
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        lifecycleScope.launch{
            delay(500)
            mService.setupAudioList(audioList)
        }
    }

    private fun listeners() = with(binding){
        prvBtn.setOnClickListener {
            if(selectedAudio > 0) {
                viewModel.updateSelectedTrack(selectedAudio - 1)
                mService.onTrackPrevious()
            }
        }
        playPauseBtn.setOnClickListener {
            mService.onTrackPause()
        }
        nextBtn.setOnClickListener {
            if(selectedAudio < viewModel.getAudioListSize() - 1) {
                viewModel.updateSelectedTrack(selectedAudio + 1)
                mService.onTrackNext()
            }
        }
        closeAudioBtn.setOnClickListener {
            mService.audioPlayerClose()
        }
    }

    private fun getAudioList(){
        viewModel.getAudioList()
    }

    private fun observers(){
        viewModel.audioList.observe(viewLifecycleOwner){
            adapter.updateData(it)
        }
        viewModel.selectedTrack.observe(viewLifecycleOwner){
            selectedAudio = it
            if(it != -1)
                bindTrackView(true)
            else
                bindTrackView(false)
        }
    }

    private fun bindTrackView(isSelected: Boolean) = with(binding) {
        if(isSelected) {
            constraintLayout.visibility = View.VISIBLE
            songTitle.text = viewModel.getTrackName(selectedAudio)
            audioLogo.setImageBitmap(viewModel.getTrackBitmap(selectedAudio))
        }else
            constraintLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if(mBound) {
            mBound = false
            Intent(requireContext(), AudioService::class.java).also {
                requireContext().unbindService(connection)
            }
        }
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
                "READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTINGS",
                Toast.LENGTH_SHORT
            ).show()
        } else reqPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

}