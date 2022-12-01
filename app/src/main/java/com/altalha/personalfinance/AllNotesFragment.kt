package com.altalha.personalfinance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.altalha.personalfinance.databinding.FragmentAllNotesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AllNotesFragment : Fragment() {

    private var _binding : FragmentAllNotesBinding? = null
    private val bind get() = _binding

    private lateinit var noteList : ArrayList<Note>
    private lateinit var adapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllNotesBinding.inflate(inflater, container, false)

        noteList = ArrayList()
        adapter = NotesAdapter(requireContext(), noteList)

        bind!!.noteFgRvNF.layoutManager = LinearLayoutManager(requireContext())
        bind!!.noteFgRvNF.adapter = adapter


        DashboardActivity.databaseRef.child("notes").child(DashboardActivity.currentUser.uid.toString())
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                noteList.clear()

                for (postSnapshot in snapshot.children) {
                    val note = postSnapshot.getValue(Note::class.java)
                    noteList.add(note!!)
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Some Thing wrong to load notes", Toast.LENGTH_SHORT).show()
            }

        })

        return bind!!.root
    }
}