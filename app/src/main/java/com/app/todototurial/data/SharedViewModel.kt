package com.app.todototurial.data

import android.app.Application
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.app.todototurial.R

class SharedViewModel(application: Application): AndroidViewModel(application) {

    val listener:AdapterView.OnItemSelectedListener=object:AdapterView.OnItemSelectedListener{

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            when(position){
                0->{(parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application,R.color.red)) }
                1->{(parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application,R.color.orange)) }
                2->{(parent?.getChildAt(0) as TextView).setTextColor(ContextCompat.getColor(application,R.color.green)) }
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}

    }

    val emptyDatabase: MutableLiveData<Boolean> = MutableLiveData(true)
    fun checkIfDatabaseIsEmpty(toDoData: List<ToDoData>){
        emptyDatabase.value=toDoData.isEmpty()
    }

    fun parsePriority(mPriority:String): Priority {
        return when(mPriority){
            "High Priority"->{Priority.HIGH}
            "Medium Priority"->{Priority.MEDUIM}
            "Low Priority"->{Priority.LOW}
            else->Priority.LOW
        }
    }

    fun parsePriorityToInt(priority: Priority):Int{
        return when(priority){
            Priority.HIGH -> 0
            Priority.MEDUIM -> 1
            Priority.LOW -> 2
        }
    }
}