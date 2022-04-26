package com.example.presentation.audiolist

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.AudioModel
import com.example.domain.usecases.GetAudioListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.ByteArrayOutputStream

class AudioListViewModel(private val getAudioListUseCase: GetAudioListUseCase): ViewModel() {

    private val _audioList = MutableLiveData<List<AudioModel>>()
    val audioList: LiveData<List<AudioModel>> = _audioList

    private val _selectedTrack = MutableLiveData<Int>(-1)
    val selectedTrack: LiveData<Int> = _selectedTrack

    fun getAudioList(){
        getAudioListUseCase()
            .flowOn(Dispatchers.IO)
            .onEach {
                if(_audioList.value.isNullOrEmpty())
                    _audioList.value = listOf(it)
                else
                    _audioList.value = _audioList.value?.plus(it)
                Log.d("t12", it.toString())
            }
            .flowOn(Dispatchers.Main)
            .launchIn(viewModelScope)

    }

    fun getTrackName(position: Int): String = audioList.value?.get(position)?.name ?: ""

    fun getTrackBitmap(position: Int): Bitmap = audioList.value?.get(position)?.logo!!

    fun getAudioListSize(): Int = audioList.value?.size ?: 0

    fun updateSelectedTrack(position: Int){
        _selectedTrack.value = position
    }

}