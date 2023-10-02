package com.example.notes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notes.databinding.FragmentArchiveBinding
import com.example.notes.databinding.FragmentFirstBinding

private const val RELEVANT = "RELEVANT"
private const val DATE_TIME_ARCHIVED = "DATE_TIME_ARCHIVED"
class ArchiveFragment : Fragment() {

    private var _binding: FragmentArchiveBinding? = null
    private val binding get() = _binding!!
    private val dBViewModel: DBViewModel by viewModels {
        object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T{
                val notesDao = (requireActivity().application as  App).db.notesDao()
                return DBViewModel(notesDao) as T
            }
        }
    }
    private val adapter = ListAdapter{item -> onItemClick(item)}
    private fun onItemClick(item: Item) {
        val bundle = bundleOf(
            RELEVANT to item.relevant,
            DATE_TIME_ARCHIVED to item.dateTime
        )
        findNavController().navigate(R.id.action_archiveFragment_to_SecondFragment, bundle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentArchiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter
        lifecycleScope.launchWhenCreated {
            val allItems = mutableListOf<Item>()
            dBViewModel.getAll().forEach { if (!it.relevant)allItems.add(it) }
            if (allItems == mutableListOf<Item>())
                binding.textView.visibility = View.VISIBLE
            else binding.textView.visibility = View.GONE
            adapter.submitList(allItems)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}