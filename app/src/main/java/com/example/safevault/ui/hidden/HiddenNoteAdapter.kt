package com.example.safevault.ui.hidden

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.safevault.data.model.HiddenNote
import com.example.safevault.databinding.ItemHiddenNoteBinding
import com.example.safevault.utils.formatToString
import com.example.safevault.utils.limitLength

class HiddenNoteAdapter(private val listener: HiddenNoteClickListener) : 
    ListAdapter<HiddenNote, HiddenNoteAdapter.HiddenNoteViewHolder>(HiddenNoteDiffCallback()) {

    interface HiddenNoteClickListener {
        fun onHiddenNoteClick(hiddenNote: HiddenNote)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HiddenNoteViewHolder {
        val binding = ItemHiddenNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HiddenNoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HiddenNoteViewHolder, position: Int) {
        val hiddenNote = getItem(position)
        holder.bind(hiddenNote)
    }

    inner class HiddenNoteViewHolder(private val binding: ItemHiddenNoteBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val hiddenNote = getItem(position)
                    listener.onHiddenNoteClick(hiddenNote)
                }
            }
        }

        fun bind(hiddenNote: HiddenNote) {
            binding.tvTitle.text = hiddenNote.title
            // Note: content is encrypted, so we won't display it in the list
            binding.tvDate.text = hiddenNote.updatedAt.formatToString()
        }
    }

    class HiddenNoteDiffCallback : DiffUtil.ItemCallback<HiddenNote>() {
        override fun areItemsTheSame(oldItem: HiddenNote, newItem: HiddenNote): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HiddenNote, newItem: HiddenNote): Boolean {
            return oldItem == newItem
        }
    }
}