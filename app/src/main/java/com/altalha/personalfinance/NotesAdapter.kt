package com.altalha.personalfinance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(val context: Context, private val list: ArrayList<Note>) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentNote = list[position]

        holder.title.text = currentNote.noteTitle.toString()
        holder.description.text = currentNote.noteDescription.toString()

        holder.root.setOnClickListener {
            Toast.makeText(context, "Added on ${currentNote.addedDate}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.note_title_NL)!!
        val description = itemView.findViewById<TextView>(R.id.note_description_NL)!!
        val root = itemView.findViewById<LinearLayout>(R.id.root_note_layout)!!
    }
}