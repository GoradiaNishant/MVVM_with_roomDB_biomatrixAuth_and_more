package com.app.todototurial.fragments.list.adapter

import android.app.ProgressDialog.show
import android.content.ClipData
import android.content.ClipboardManager
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.todototurial.MainActivity
import com.app.todototurial.R
import com.app.todototurial.data.Priority
import com.app.todototurial.data.ToDoData
import com.app.todototurial.databinding.RvLayoutBinding
import com.app.todototurial.fragments.list.ListFragment
import com.app.todototurial.fragments.list.ListFragmentDirections
import java.util.concurrent.Executor
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import android.content.Context.CLIPBOARD_SERVICE
import android.view.View

import androidx.core.content.ContextCompat.getSystemService





class ListAdapter(var mContext: ListFragment) : RecyclerView.Adapter<ListAdapter.ItemViewHolder>() {

    var dataList = emptyList<ToDoData>()
    var isAuth = MutableLiveData<Boolean>(false)

    //Auth variables
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var biometricPromptInfo: BiometricPrompt.PromptInfo

    class ItemViewHolder(val binding: RvLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvLayoutBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.tittle.text = dataList[position].tittle
        holder.binding.description.text = dataList[position].description
        holder.binding.setVisibility.setOnClickListener {

            executor = mContext.context?.let { it1 -> ContextCompat.getMainExecutor(it1) }!!

            val callback = object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int,
                                                       errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(holder.binding.setVisibility.context,
                            "Authentication error: $errString", Toast.LENGTH_SHORT)
                            .show()
                        holder.binding.copyBtn.visibility= View.INVISIBLE
                        isAuth.value = false
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(holder.binding.setVisibility.context,
                            "Authentication succeeded!", Toast.LENGTH_SHORT)
                            .show()
                        isAuth.value = true
                        holder.binding.copyBtn.visibility= View.VISIBLE
                        holder.binding.setVisibility.setImageResource(R.drawable.ic_visibility_off)
                        holder.binding.description.transformationMethod = SingleLineTransformationMethod.getInstance()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(holder.binding.setVisibility.context, "Authentication failed",
                            Toast.LENGTH_SHORT)
                            .show()
                        isAuth.value = false
                        holder.binding.copyBtn.visibility= View.INVISIBLE
                    }
                }

            biometricPrompt = BiometricPrompt(mContext, executor, callback)

            biometricPromptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
//                .setNegativeButtonText("Use account password")
                .setDeviceCredentialAllowed(true)
                .build()

            if (isAuth.value == true){
                holder.binding.setVisibility.setImageResource(R.drawable.ic_visibility)
                holder.binding.description.transformationMethod = PasswordTransformationMethod.getInstance()
                isAuth.value = false
                holder.binding.copyBtn.visibility= View.INVISIBLE
            }else{
                biometricPrompt.authenticate(biometricPromptInfo)
            }
        }
        holder.binding.copyBtn.setOnClickListener {
            val clipboard = holder.itemView.context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("Password",holder.binding.description.text)
            clipboard.setPrimaryClip(clip)
        }

        when (dataList[position].priority) {
            Priority.HIGH -> holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            Priority.MEDUIM -> holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.orange))
            Priority.LOW -> holder.binding.priorityIndicator.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        }
        holder.binding.rowBg.setOnClickListener{
            val action =
                ListFragmentDirections.actionListFragmentToUpdateFragment(dataList[position])
            holder.itemView.findNavController().navigate(action)

        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    fun setData(toDoData: List<ToDoData>,isAuthencated:Boolean){
        val listDiffUtil = ListDiffUtil(dataList,toDoData)
        val listDiffUtilResult = DiffUtil.calculateDiff(listDiffUtil)
        dataList=toDoData
        listDiffUtilResult.dispatchUpdatesTo(this)
    }
}