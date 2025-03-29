package com.example.safevault.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.safevault.data.model.Note
import com.example.safevault.databinding.ItemNoteBinding
import com.example.safevault.utils.formatToString
import com.example.safevault.utils.limitLength

class NoteAdapter(private val listener: NoteClickListener) : 
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    interface NoteClickListener {
        fun onNoteClick(note: Note)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) : 
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val note = getItem(position)
                    listener.onNoteClick(note)
                }
            }
        }

        fun bind(note: Note) {
            binding.tvTitle.text = note.title
            binding.tvContent.text = note.content.limitLength(100)
            binding.tvDate.text = note.updatedAt.formatToString()
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}