package com.app.todototurial.fragments.update

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.app.todototurial.R
import com.app.todototurial.data.SharedViewModel
import com.app.todototurial.data.ToDoData
import com.app.todototurial.data.ToDoViewModel
import com.app.todototurial.databinding.FragmentUpdateBinding

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!

    private val args by navArgs<UpdateFragmentArgs>()

    val mToDoViewModel: ToDoViewModel by viewModels()
    val mSharedViewModel: SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.updateTaskTittle.setText(args.currentItem.tittle)
        binding.updateTaskDiscription.setText(args.currentItem.description)
        binding.updateTaskPriority.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))
        binding.updateTaskPriority.onItemSelectedListener = mSharedViewModel.listener
        setHasOptionsMenu(true)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.update_fragment_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_update -> updateDataOfDb()
            R.id.menu_delete -> deleteDataFromDb()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteDataFromDb() {

        val builder=AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_, _ ->
            mToDoViewModel.deleteData(args.currentItem)
            Toast.makeText(requireContext(),"Success Updating", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }
        builder.setNegativeButton("No"){_, _ -> }
        builder.setTitle("Delete '${args.currentItem.tittle}'?")
        builder.setMessage("Are you sure to delete this item???")
        builder.create().show()
    }

    private fun updateDataOfDb() {
        val tittle=binding.updateTaskTittle.text.toString()
        val priority=binding.updateTaskPriority.selectedItem.toString()
        val description=binding.updateTaskDiscription.text.toString()

        if(tittle.isNotEmpty() && description.isNotEmpty()){
            val updatedData= ToDoData(args.currentItem.id,tittle,mSharedViewModel.parsePriority(priority),description)
            mToDoViewModel.updateData(updatedData)
            Toast.makeText(requireContext(),"Success Deleted Item", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(),"Value Can't be Empty", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        _binding=null
        super.onDestroy()
    }
}