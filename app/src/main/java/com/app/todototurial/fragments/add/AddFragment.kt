package com.app.todototurial.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.todototurial.R
import com.app.todototurial.data.Priority
import com.app.todototurial.data.SharedViewModel
import com.app.todototurial.data.ToDoData
import com.app.todototurial.data.ToDoViewModel
import com.app.todototurial.databinding.FragmentAddBinding


class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    val mToDoViewModel: ToDoViewModel by viewModels()
    val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.taskPriority.onItemSelectedListener = mSharedViewModel.listener

        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add){
            insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun insertDataToDb() {
        val mTittle=binding.taskTittle.text.toString()
        val mPriority=binding.taskPriority.selectedItem.toString()
        val mDescription=binding.taskDiscription.text.toString()

        if(mTittle.isNotEmpty() && mDescription.isNotEmpty()){
            var newData=ToDoData(0,mTittle,mSharedViewModel.parsePriority(mPriority),mDescription)
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(),"Success",Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(),"Value Can't be Empty",Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        _binding=null
        super.onDestroy()
    }
}