package com.example.notes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.FragmentFirstBinding

private const val DATE_TIME = "DATE_TIME"
private const val RELEVANT = "RELEVANT"
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
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
        val bundle = bundleOf(RELEVANT to item.relevant, DATE_TIME to item.dateTime)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
    }
    private val rep = Repository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rep.saveCurrentURI(requireActivity(),"")
        binding.buttonAdd.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        binding.buttonArchive.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_archiveFragment)
        }

        val swapHelper = swapItem()
        swapHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launchWhenCreated {
            val allItems = mutableListOf<Item>()
            dBViewModel.getAll().forEach { if (it.relevant)allItems.add(it) }
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

    fun swapItem(): ItemTouchHelper{
        return ItemTouchHelper(object : ItemTouchHelper.
        SimpleCallback(0, ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                lifecycleScope.launchWhenCreated {
                    val allItems = mutableListOf<Item>()
                    dBViewModel.getAll().forEach { if (it.relevant)allItems.add(it) }
                    if (allItems == mutableListOf<Item>())
                        binding.textView.visibility = View.VISIBLE
                    else binding.textView.visibility = View.GONE

                    val newItem = Item(allItems[viewHolder.absoluteAdapterPosition].dateTime,
                        allItems[viewHolder.absoluteAdapterPosition].title,
                        allItems[viewHolder.absoluteAdapterPosition].description,
                        allItems[viewHolder.absoluteAdapterPosition].imageURI,
                    false)

                    dBViewModel.addItem(newItem)
                    allItems.removeAt(viewHolder.absoluteAdapterPosition)
                    adapter.submitList(allItems)
                }
            }
        })
    }
}
