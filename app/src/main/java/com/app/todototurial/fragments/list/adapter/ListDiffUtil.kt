package com.app.todototurial.fragments.list.adapter

import androidx.recyclerview.widget.DiffUtil
import com.app.todototurial.data.ToDoData

class ListDiffUtil(
    private val oldList: List<ToDoData>,
    private val newList:List<ToDoData>):DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList === newList
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
                && oldList[oldItemPosition].tittle == newList[newItemPosition].tittle
                && oldList[oldItemPosition].description == newList[newItemPosition].description
                && oldList[oldItemPosition].priority == newList[newItemPosition].priority
    }
}