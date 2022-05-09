package com.app.todototurial.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(application: Application):AndroidViewModel(application) {

    private val toDoDao= ToDoDatabase.getDatabase(application).toDoDao()
    private val repository:ToDoRepo = ToDoRepo(toDoDao)

    val getAllData:LiveData<List<ToDoData>> = repository.getAllData
    val getSortedByHighPriority:LiveData<List<ToDoData>> = repository.sortedByHighPriority
    val getSortedByMediumPriority:LiveData<List<ToDoData>> = repository.sortedByMediumPriority
    val getSortedByLowPriority:LiveData<List<ToDoData>> = repository.sortedByLowPriority


    fun insertData(toDoData: ToDoData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertData(toDoData)
        }
    }

    fun updateData(toDoData: ToDoData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateData(toDoData)
        }
    }

    fun deleteData(toDoData: ToDoData){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteData(toDoData)
        }
    }

    fun deleteAllData(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllData()
        }
    }

    fun searchDataFromDB(searchQuery: String): LiveData<List<ToDoData>> {
        return  repository.searchData(searchQuery)
    }

    fun sortByHigh(): LiveData<List<ToDoData>> {
        return  repository.sortByHigh()
    }
    fun sortByMedium(): LiveData<List<ToDoData>> {
        return  repository.sortByMeduim()
    }
    fun sortByLow(): LiveData<List<ToDoData>> {
        return  repository.sortByLow()
    }
}