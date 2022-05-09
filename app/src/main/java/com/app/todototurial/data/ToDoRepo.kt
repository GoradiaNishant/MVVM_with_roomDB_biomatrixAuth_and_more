package com.app.todototurial.data

import androidx.lifecycle.LiveData

class ToDoRepo(private val toDoDao:ToDoDao) {

    val getAllData: LiveData<List<ToDoData>> = toDoDao.getAllData()
    val sortedByHighPriority:LiveData<List<ToDoData>> = toDoDao.sortByHighPriority()
    val sortedByMediumPriority:LiveData<List<ToDoData>> = toDoDao.sortByMeduimPriority()
    val sortedByLowPriority:LiveData<List<ToDoData>> = toDoDao.sortByLowPriority()

    suspend fun insertData(toDoData: ToDoData){
        toDoDao.insertData(toDoData = toDoData)
    }

    suspend fun updateData(toDoData: ToDoData){
        toDoDao.updateData(toDoData = toDoData)
    }

    suspend fun deleteData(toDoData: ToDoData){
        toDoDao.deleteData(toDoData = toDoData)
    }

    suspend fun deleteAllData(){
        toDoDao.deleteAllData()
    }

    fun searchData(query:String): LiveData<List<ToDoData>> {
        return toDoDao.search(query)
    }

    fun sortByHigh(): LiveData<List<ToDoData>> {
        return toDoDao.sortByHighPriority()
    }

    fun sortByMeduim(): LiveData<List<ToDoData>> {
        return toDoDao.sortByMeduimPriority()
    }

    fun sortByLow(): LiveData<List<ToDoData>> {
        return toDoDao.sortByLowPriority()
    }

}