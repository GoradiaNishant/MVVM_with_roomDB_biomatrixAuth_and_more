package com.app.todototurial.data

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.parcelize.Parcelize

enum class Priority {
    HIGH,
    MEDUIM,
    LOW
}

class Converter{

    @TypeConverter
    fun fromPriority(priority: Priority):String{
        return priority.name
    }

    @TypeConverter
    fun toPriority(priority: String):Priority{
        return Priority.valueOf(priority)
    }

}

@Entity(tableName = "todo_table")
@Parcelize
data class ToDoData(
    @PrimaryKey(autoGenerate = true)
    var id:Int,
    var tittle:String,
    var priority:Priority,
    var description:String,
):Parcelable

@Dao
interface ToDoDao{

    @Query("SELECT * FROM todo_table ORDER BY id ASC")
    fun getAllData():LiveData<List<ToDoData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertData(toDoData: ToDoData)

    @Update
    suspend fun updateData(toDoData: ToDoData)

    @Delete
    suspend fun deleteData(toDoData: ToDoData)

    @Query("DELETE FROM todo_table")
    suspend fun deleteAllData()

    @Query("SELECT * FROM todo_table WHERE tittle LIKE :searchQuery")
    fun search(searchQuery: String):LiveData<List<ToDoData>>

    @Query("SELECT * FROM todo_table ORDER BY CASE WHEN priority LIKE 'H%' THEN 1 WHEN priority LIKE 'M%' THEN 2 WHEN priority LIKE 'L%' THEN 3 END")
    fun sortByHighPriority():LiveData<List<ToDoData>>

    @Query("SELECT * FROM todo_table ORDER BY CASE WHEN priority LIKE 'M%' THEN 1 WHEN priority LIKE 'L%' THEN 2 WHEN priority LIKE 'H%' THEN 3 END")
    fun sortByMeduimPriority():LiveData<List<ToDoData>>

    @Query("SELECT * FROM todo_table ORDER BY CASE WHEN priority LIKE 'L%' THEN 1 WHEN priority LIKE 'H%' THEN 2 WHEN priority LIKE 'M%' THEN 3 END")
    fun sortByLowPriority():LiveData<List<ToDoData>>

}

@Database(entities = [ToDoData::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ToDoDatabase : RoomDatabase(){
    abstract fun toDoDao():ToDoDao

    companion object{

        @Volatile
        private var INSTANCE:ToDoDatabase?=null

        fun getDatabase(context: Context):ToDoDatabase{
            val tempInstance= INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance=Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_database",
                ).build()
                INSTANCE=instance
                return instance
            }
        }
    }
}


