package com.altalha.personalfinance

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_contact.*

class ContactActivity : AppCompatActivity() {

    private lateinit var listForAdapter : ArrayList<Contact>
    private lateinit var adapter: ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        val fromClass : String? = intent.getStringExtra("class")

        listForAdapter = ArrayList()
        adapter = ContactAdapter(this, fromClass!!, listForAdapter)

        setTitleOfClass(fromClass)
        listContentSelectingMethod(fromClass)

        contactList_CA.layoutManager = LinearLayoutManager(this)
        contactList_CA.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun setTitleOfClass(Class : String?) {
        if (Class != null) {
            when (Class) {
                "send" -> title_CA.text = "Select receiver"
                "myContacts" -> title_CA.text = "My Contacts"
                else -> title_CA.text = "Add new Contact"
            }
        }
        else {
            returnToDashboard()
        }
    }

    private fun listContentSelectingMethod(Class: String?) {
        if (Class != null) {
            when (Class) {
                "addContact" -> getUsers()
                else -> getContacts()
            }

        } else {
            returnToDashboard()
        }
    }

    private fun getUsers() {
        DashboardActivity.databaseRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contactURIList = arrayListOf<String>()
                for (i in DashboardActivity.contactList) {
                    contactURIList.add(i.userUID!!)
                }
                listForAdapter.clear()
                for (postSnapshot in snapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)
                    if (user!!.uid !in contactURIList && user.uid != DashboardActivity.currentUser.uid) {
                        val con = Contact(user.uid.toString(), user.name.toString(), user.gender.toString(), user.photo.toString(), user.email.toString())
                        listForAdapter.add(con)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                returnToDashboard()
            }
        })
    }

    private fun getContacts() {
        listForAdapter.clear()
        listForAdapter.addAll(DashboardActivity.contactList)
        adapter.notifyDataSetChanged()
    }

    private fun returnToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}