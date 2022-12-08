package com.example.android.todoplanner.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.todoplanner.databinding.EventsOverviewItemBinding
import com.example.android.todoplanner.presentation.model.ViewEventShortInfo

class EventsListAdapter (
    private val eventItemClickListener: EventItemClickListener
) : ListAdapter<ViewEventShortInfo, EventsListAdapter.EventHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = EventsOverviewItemBinding.inflate(layoutInflater, parent, false)
        return EventHolder(binding)
    }

    override fun onBindViewHolder(holder: EventHolder, position: Int) {
        // получить элемент списка по позиции
        val item = getItem(position)
        holder.binding.shortInfo = item
        // передача обработчика в конкретный объект биндинга
        holder.binding.clickListener = eventItemClickListener
        holder.binding.executePendingBindings()
    }

    class EventHolder (val binding: EventsOverviewItemBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback: DiffUtil.ItemCallback<ViewEventShortInfo>() {
        override fun areItemsTheSame(oldItem: ViewEventShortInfo, newItem: ViewEventShortInfo): Boolean {
            return oldItem.uid == newItem.uid
        }
        override fun areContentsTheSame(oldItem: ViewEventShortInfo, newItem: ViewEventShortInfo): Boolean {
            return oldItem == newItem
        }
    }
}

class EventItemClickListener (val clickLambda: (uid: Long) -> Unit) {
    // onClick присваивается через атрибут в xml
    fun onClick(shortInfo: ViewEventShortInfo) {
        clickLambda(shortInfo.uid)
    }
}
