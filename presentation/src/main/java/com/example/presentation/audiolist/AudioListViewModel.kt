package com.example.presentation.audiolist

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

class AudioListViewModel(private val getAudioListUseCase: GetAudioListUseCase): ViewModel() {

    private val _audioList = MutableLiveData<List<AudioModel>>()
    val audioList: LiveData<List<AudioModel>> = _audioList

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

}