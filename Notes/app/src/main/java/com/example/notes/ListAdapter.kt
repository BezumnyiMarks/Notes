package com.example.notes

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notes.databinding.RecyclerItemModelBinding
import java.util.*

class ListAdapter(private val onClick:(Item) -> Unit) : ListAdapter<Item, RecyclerViewHolder>(ListAdapterDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return RecyclerViewHolder(
            RecyclerItemModelBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val item = getItem(position)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = item.dateTime
        val month = getMonth(calendar.get(Calendar.MONTH) + 1)
        val dateTimeText = "${calendar.get(Calendar.DAY_OF_MONTH)} $month ${calendar.get(Calendar.YEAR)} " + "\n" +
                "${getHour(calendar.get(Calendar.HOUR_OF_DAY))}:${getMinute(calendar.get(Calendar.MINUTE))}:${getSecond(calendar.get(Calendar.SECOND))}"

        with(holder.binding){
            if (item.title == "")
                textView.text = "Безъ заглавия"
            else textView.text = item.title
            textViewDateTime.text = dateTimeText
        }
        holder.binding.textView.setOnClickListener {
            onClick(item)
        }
        holder.binding.textViewDateTime.setOnClickListener {
            onClick(item)
        }
    }

    private fun getHour(hour: Int): String{
        return if(hour < 10) "0$hour"
        else hour.toString()
    }
    private fun getMinute(minute: Int): String{
        return if(minute < 10) "0$minute"
        else minute.toString()
    }
    private fun getSecond(second: Int): String{
        return if(second < 10) "0$second"
        else second.toString()
    }
}

class ListAdapterDiffUtilCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean =
        oldItem.dateTime == newItem.dateTime

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
        oldItem == newItem
}

class RecyclerViewHolder (val binding: RecyclerItemModelBinding) : RecyclerView.ViewHolder(binding.root)
