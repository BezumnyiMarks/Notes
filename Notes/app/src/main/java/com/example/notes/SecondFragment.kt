package com.example.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notes.databinding.FragmentSecondBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val DATE_TIME = "DATE_TIME"
private const val DATE_TIME_ARCHIVED = "DATE_TIME_ARCHIVED"
private const val RELEVANT = "RELEVANT"
private const val URI = "URI"

class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!
    private val dBViewModel: DBViewModel by viewModels {
        object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T{
                val notesDao = (requireActivity().application as  App).db.notesDao()
                return DBViewModel(notesDao) as T
            }
        }
    }
    private val immageRequestCode = -1
    private var imageURI = ""
    private var data = Item(0L, "", "", "", true)
    private var currentImageURI = ""
    val rep = Repository()

    private var dateTime: Long? = null
    private var dateTimeArchived: Long? = null
    private var relevant: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dateTime = it.getLong(DATE_TIME)
            relevant = it.getBoolean(RELEVANT)
            dateTimeArchived = it.getLong(DATE_TIME_ARCHIVED)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != immageRequestCode && this.data.imageURI != ""){
            binding.imageView.setImageURI(Uri.parse(this.data.imageURI))
            currentImageURI = this.data.imageURI
        }
        else if (resultCode == Activity.RESULT_OK && resultCode == immageRequestCode){
            binding.imageView.setImageURI(data?.data)
            imageURI = data?.data.toString()
            currentImageURI = data?.data.toString()
            requireActivity().contentResolver.takePersistableUriPermission(data?.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (dateTime != null){
                binding.buttonDeletePicture.visibility = View.VISIBLE
            }
            else{
                binding.buttonDeletePicture.visibility = View.GONE
            }
        }
        else{
            currentImageURI = ""
            imageURI = ""
            binding.imageView.setImageDrawable(null)
            binding.constraintLayout.visibility = View.GONE
            binding.buttonDeletePicture.visibility = View.GONE
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("ShowToast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentImageURI = rep.getCurrentURI(requireActivity())
        showData()

        binding.buttonAddPicture.setOnClickListener{
            if (dateTime == null){
                if (binding.constraintLayout.visibility == View.GONE){
                    binding.constraintLayout.visibility = View.VISIBLE
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.type = "image/*"
                    startActivityForResult(intent, immageRequestCode)
                }
                else {
                    imageURI = ""
                    binding.imageView.setImageDrawable(null)
                    binding.constraintLayout.visibility = View.GONE
                }
            }
            else{
                if (binding.constraintLayout.visibility == View.GONE)
                    binding.constraintLayout.visibility = View.VISIBLE
                else
                    binding.constraintLayout.visibility = View.GONE
            }
        }

        binding.buttonDeletePicture.setOnClickListener {
            Toast.makeText(requireContext(), "Дабы удалить фотокарточку, жми продолжитѢльно, а нѢ кратко", Toast.LENGTH_LONG).show()
        }
        binding.buttonDeletePicture.setOnLongClickListener {
            currentImageURI = ""
            imageURI = ""
            binding.imageView.setImageDrawable(null)
            binding.buttonDeletePicture.visibility = View.GONE
            return@setOnLongClickListener true
        }

        binding.buttonRedactPicture.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "image/*"
            startActivityForResult(intent, immageRequestCode)
        }

        binding.buttonSave.setOnClickListener {
            rep.saveCurrentURI(requireActivity(),"")
            if (dateTime == null) {
                if (binding.editTextTitle.text.toString() != "" || binding.editTextDesc.text.toString() != "" || imageURI != "") {
                    dBViewModel.addItem(
                        Item(
                            Calendar.getInstance().timeInMillis,
                            binding.editTextTitle.text.toString(),
                            binding.editTextDesc.text.toString(),
                            imageURI,
                            true
                        )
                    )
                    Toast.makeText(
                        requireContext(),
                        "СвѢдѢния успѢшно сохранѢны",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                } else Toast.makeText(requireContext(), "НихѢра нѢтъ!", Toast.LENGTH_SHORT).show()
            }
            else{
                if (binding.editTextTitle.text.toString() != "" || binding.editTextDesc.text.toString() != "" || imageURI != "") {
                    dBViewModel.addItem(
                        Item(
                            dateTime!!,
                            binding.editTextTitle.text.toString(),
                            binding.editTextDesc.text.toString(),
                            imageURI,
                            true
                        )
                    )
                    Toast.makeText(
                        requireContext(),
                        "СвѢдѢния успѢшно обновлѢны",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
                } else Toast.makeText(requireContext(), "НихѢра нѢтъ!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonFinalDelete.setOnLongClickListener {
            dBViewModel.delete(listOf(data))
            Toast.makeText(
                requireContext(),
                "СвѢдѢния успѢшно удалѢны",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_SecondFragment_to_archiveFragment)
            return@setOnLongClickListener true
        }

        binding.buttonRestore.setOnLongClickListener {
            dBViewModel.addItem(Item(data.dateTime, data.title, data.description, data.imageURI, true))
            Toast.makeText(
                requireContext(),
                "СвѢдѢния успѢшно восстановлѢны",
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_SecondFragment_to_archiveFragment)
            return@setOnLongClickListener true
        }

        binding.imageView.setOnClickListener {
            rep.saveCurrentURI(requireActivity(),currentImageURI)
            if (currentImageURI != ""){
                val bundle = bundleOf(URI to currentImageURI)
                findNavController().navigate(R.id.action_SecondFragment_to_photoFragment, bundle)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showData(){
        if (relevant == true && dateTime != null){
            lifecycleScope.launchWhenCreated {
                binding.buttonRedactPicture.visibility = View.VISIBLE
                data = dBViewModel.getByDateTime(dateTime!!)
                if (currentImageURI == "")
                    currentImageURI = data.imageURI

                val description = data.description
                binding.editTextTitle.setText(data.title)
                binding.editTextDesc.setText(description)

                if (data.imageURI != "" && currentImageURI == ""){
                    imageURI = data.imageURI
                    binding.buttonDeletePicture.visibility = View.VISIBLE
                    binding.constraintLayout.visibility = View.VISIBLE
                    binding.imageView.setImageURI(Uri.parse(data.imageURI))
                }
                else if (currentImageURI != ""){
                    binding.buttonDeletePicture.visibility = View.VISIBLE
                    binding.constraintLayout.visibility = View.VISIBLE
                    binding.imageView.setImageURI(Uri.parse(currentImageURI))
                }
            }
        }
        else   {
            binding.constraintLayout.visibility = View.GONE
            binding.buttonDeletePicture.visibility = View.GONE
            binding.buttonRedactPicture.visibility = View.GONE
        }
        if (relevant == false && relevant != null){
            lifecycleScope.launchWhenCreated {
                data = dBViewModel.getByDateTime(dateTimeArchived!!)
                if (currentImageURI == "")
                    currentImageURI = data.imageURI
                binding.buttonDeletePicture.visibility = View.GONE
                binding.buttonRedactPicture.visibility = View.GONE
                binding.buttonAddPicture.visibility = View.GONE
                binding.buttonSave.visibility = View.GONE
                binding.constraintLayout.visibility = View.VISIBLE
                binding.buttonFinalDelete.visibility = View.VISIBLE
                binding.buttonRestore.visibility = View.VISIBLE

                val description = data.description
                binding.editTextTitle.setText(data.title)
                binding.editTextDesc.setText(description)
                if (currentImageURI == ""){
                    if (data.imageURI != "")
                        binding.imageView.setImageURI(Uri.parse(data.imageURI))
                }
                else binding.imageView.setImageURI(Uri.parse(currentImageURI))
            }
        }
    }
}

fun getMonth(month: Int): String{
    return when(month){
        1 -> "Января"
        2 -> "ФѢвраля"
        3 -> "Марта"
        4 -> "АпрѢля"
        5 -> "Мая"
        6 -> "Июня"
        7 -> "Июля"
        8 -> "Августа"
        9 -> "СѢнтября"
        10 -> "Октября"
        11 -> "Ноября"
        else -> "ДѢкабря"
    }
}