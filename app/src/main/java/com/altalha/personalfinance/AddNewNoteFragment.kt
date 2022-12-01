package com.altalha.personalfinance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.altalha.personalfinance.databinding.FragmentAddNewNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_new_note.*
import java.util.*

class AddNewNoteFragment : Fragment() {

    private var _binding: FragmentAddNewNoteBinding? = null
    private val bind get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddNewNoteBinding.inflate(inflater, container, false)

        bind.addBtnANN.setOnClickListener {
            val descriptions = description_ANN.text.toString()
            val title = title_ANN.text.toString()
            if (title.isNotEmpty() && descriptions.isNotEmpty()) {
                addToDatabase(title, descriptions)
                clearFields()
                Toast.makeText(requireContext(), "Note added", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(requireContext(), "Enter title and description", Toast.LENGTH_SHORT).show()
            }
        }

        return bind.root
    }

    private fun clearFields() {
        bind.titleANN.text.clear()
        bind.descriptionANN.text.clear()
    }

    private fun addToDatabase(title : String, description : String) {

        val currentUser = DashboardActivity.currentUser.uid as String
        val date = Today().date()
        val id = UUID.randomUUID().toString()

        val note = Note(id, currentUser, title, description, date)

        DashboardActivity.databaseRef.child("notes").child(note.accountUID.toString()).child(note.noteID.toString()).setValue(note)

    }
}