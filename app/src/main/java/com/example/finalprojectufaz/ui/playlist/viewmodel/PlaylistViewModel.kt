package com.example.finalprojectufaz.ui.playlist.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalprojectufaz.data.local.playlist.PlaylistDao
import com.example.finalprojectufaz.data.local.playlist.PlaylistEntity
import com.example.finalprojectufaz.data.local.playlist.TrackEntity
import com.example.finalprojectufaz.data.local.quiz.QuizDao
import com.example.finalprojectufaz.domain.core.Resource
import com.example.finalprojectufaz.domain.playlist.PlaylistDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel(private val dao: PlaylistDao,private
val quizDao: QuizDao):ViewModel() {

    private val _playlists = MutableLiveData<Resource<List<PlaylistDTO>>>()

     val cachePlaylists = MutableLiveData<List<PlaylistDTO>>()

    val playlists : LiveData<Resource<List<PlaylistDTO>>> get() = _playlists



    fun insert(name:String){
        viewModelScope.launch(Dispatchers.IO) {
          dao.addPlaylist(PlaylistEntity(0,name))

        }
    }

    fun getPlaylists(){

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = dao.getPlaylists()
                if(list.isNotEmpty()){
                   val pls = mutableListOf<PlaylistDTO>()
                    list.forEach {
                        val countPL = dao.getCount(it.id)
                        val plModel = PlaylistDTO(it.id,countPL,it.name)
                        pls.add(plModel)
                    }
                    _playlists.postValue(Resource.Success(pls))
                }else{
                    _playlists.postValue(Resource.Success(emptyList()))
                }
            }catch (e:Exception){
               _playlists.postValue(Resource.Error(e))
            }


        }
    }

    fun addToPlaylists(trackId:Int,pIds:List<Int>){
        viewModelScope.launch(Dispatchers.IO) {
            try {
               pIds.forEach { id->
                   dao.insertPlaylist(TrackEntity(0,trackId,id))
               }
            }catch (e:Exception){

            }
        }
    }

    fun remove(playlistId:Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
               dao.removePlaylist(playlistId)
                getPlaylists()
            }catch (e:Exception){

            }
        }
    }



}