package com.app.todototurial.fragments.list

import android.app.AlertDialog
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.view.*
import android.view.animation.OvershootInterpolator
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.app.todototurial.MainActivity.Companion.cryptObject
import com.app.todototurial.R
import com.app.todototurial.data.SharedViewModel
import com.app.todototurial.data.ToDoData
import com.app.todototurial.data.ToDoViewModel
import com.app.todototurial.databinding.FragmentListBinding
import com.app.todototurial.fragments.list.adapter.ListAdapter
import com.app.todototurial.fragments.list.adapter.SwipeToDelete
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import java.util.concurrent.Executor
import androidx.recyclerview.widget.LinearLayoutManager




class ListFragment : Fragment(),SearchView.OnQueryTextListener {

    private val adapter: ListAdapter by lazy { ListAdapter(this) }
    private val mToDoViewModel:ToDoViewModel by viewModels()
    private val mSharedViewModel:SharedViewModel by viewModels()

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.floatingActionButton.setOnClickListener{
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        binding.floatingActionButton.setOnClickListener{
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        binding.taskList.adapter=adapter
        binding.taskList.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.taskList.itemAnimator = SlideInUpAnimator(OvershootInterpolator(1f))
        swipeToDelete(binding.taskList)

        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkIfDatabaseIsEmpty(data)
            adapter.setData(data, cryptObject)
        })

        mSharedViewModel.emptyDatabase.observe(viewLifecycleOwner, Observer {
            showEmptyDatabase(it)
        })

        setHasOptionsMenu(true)

        return view
    }


    private fun swipeToDelete(recyclerView: RecyclerView){
        val swipeToDeleteCallback = object:SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //deleting item
                val itemToDelete = adapter.dataList[viewHolder.adapterPosition]
                mToDoViewModel.deleteData(itemToDelete)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                Toast.makeText(requireContext(),"Success Removed Item!!!", Toast.LENGTH_SHORT).show()
                //restoring item
                restoreDeletedData(viewHolder.itemView,itemToDelete,viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper=ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedData(view:View,deletedItem:ToDoData,position:Int){
       val snackbar=Snackbar.make(view,"Deleted '${deletedItem.tittle}'", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo"){
            mToDoViewModel.insertData(deletedItem)
            adapter.notifyItemChanged(position)
        }
        snackbar.show()
    }


    private fun showEmptyDatabase(emptyDatabase:Boolean) {
        if(emptyDatabase){
            binding.emptySign.visibility = View.VISIBLE
            binding.noData.visibility = View.VISIBLE
        }else{
            binding.emptySign.visibility = View.INVISIBLE
            binding.noData.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu,menu)

        val searchItem = menu.findItem(R.id.menu_search)
        val searchAction = searchItem.actionView as? SearchView
        searchAction?.isSubmitButtonEnabled = true
        searchAction?.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
           R.id.delete_all -> deleteAllFromDb()
           R.id.menu_high_priority -> mToDoViewModel.getSortedByHighPriority.observe(viewLifecycleOwner, Observer { adapter.setData(it,cryptObject) })
           R.id.menu_medium_priority -> mToDoViewModel.getSortedByMediumPriority.observe(viewLifecycleOwner, Observer { adapter.setData(it,cryptObject) })
           R.id.menu_low_priority -> mToDoViewModel.getSortedByLowPriority.observe(viewLifecycleOwner, Observer { adapter.setData(it,cryptObject) })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllFromDb() {

        val builder= AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_, _ ->
            mToDoViewModel.deleteAllData()
            Toast.makeText(requireContext(),"Success Removed Everything!!!", Toast.LENGTH_LONG).show()
        }
        builder.setNegativeButton("No"){_, _ -> }
        builder.setTitle("Delete All'?")
        builder.setMessage("Are you sure to delete all items???")
        builder.create().show()
    }

    override fun onDestroy() {
        _binding=null
        super.onDestroy()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchThroughDbByQuery(query)
        }
        return true
    }

    private fun searchThroughDbByQuery(query: String) {
        val searchQuery = "%$query%"
        mToDoViewModel.searchDataFromDB(searchQuery).observe(this, Observer {list->
            list?.let {
                adapter.setData(it,cryptObject)
            }
        })
    }

}