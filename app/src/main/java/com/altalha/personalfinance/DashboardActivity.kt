package com.altalha.personalfinance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class DashboardActivity : AppCompatActivity() {

    companion object {
        lateinit var transactionsList : ArrayList<Transaction>
        lateinit var contactList : ArrayList<Contact>
        lateinit var auth: FirebaseAuth
        lateinit var storage: FirebaseStorage
        lateinit var databaseRef: DatabaseReference
        lateinit var currentUser: User
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        currentUser = User()

        transactionsList = ArrayList()
        contactList = ArrayList()

        getCurrentUser()



        home_BL_DA.setOnClickListener{
            changeLayout(AllNotesFragment())
        }

        new_note_BL_DA.setOnClickListener {
            changeLayout(AddNewNoteFragment())
        }

        transactions_BL_DA.setOnClickListener {
            changeLayout(TransactionsFragment())
        }

        deposit_btn_AD.setOnClickListener {
            changeToTransactionLayout("Deposit")
        }

        withdraw_btn_DA.setOnClickListener {
            changeToTransactionLayout("Withdraw")
        }

        settings_btn_DA.setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }

        send_btn_DA.setOnClickListener {
            val intent = Intent(this, ContactActivity::class.java)
            intent.putExtra("class", "send")
            startActivity(intent)
        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.home_NM -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                }
                R.id.add_new_contact_NM -> {
                    goToContacts("addContact")
                }
                R.id.my_contacts_NM -> {
                    goToContacts("myContacts")
                }
                R.id.sign_out_NM -> {
                    auth.signOut()
                    clearSavedData()
                    val intent = Intent(this, FirstLoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.about_NM -> {
                    startActivity(Intent(this, About::class.java))
                }
                R.id.settings_NM -> {
                    startActivity(Intent(this, Settings::class.java))
                }
                R.id.exit_NM -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exit")
                        .setMessage("Do you want to close app?")
                        .setPositiveButton("Yes") {_, _ ->
                            exitProcess(1)
                        }
                        .setNegativeButton("No") {dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }
            }
            true
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = getSharedPreferences("SAVE_USER", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userUID", currentUser.uid.toString())
        editor.apply()
        exitProcess(1)
    }

    private fun clearSavedData() {
        val sharedPreferences = getSharedPreferences("SAVE_USER", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userUID", null)
        editor.apply()
    }

    private fun goToContacts(type: String) {
        val intent = Intent(this, ContactActivity::class.java)
        intent.putExtra("class", type)
        startActivity(intent)
    }

    private fun changeToTransactionLayout(type : String) {
        val intent = Intent(this, TransactionActivity::class.java)
        intent.putExtra("transactionType", type)
        startActivity(intent)
    }

    private fun getCurrentUser() {
        val currentUserUID = auth.currentUser?.uid as String
        databaseRef.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)
                    if (currentUserUID == user?.uid) {
                        currentUser = user
                        setDashboard()
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                auth.signOut()
                val intent = Intent(this@DashboardActivity, FirstLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

        })
    }

    private fun getTransactions() {
        databaseRef.child("transactions").child(auth.currentUser?.uid.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                transactionsList.clear()

                for (postSnapshot in snapshot.children) {
                    val trans = postSnapshot.getValue(Transaction::class.java)
                    transactionsList.add(trans!!)
                }

                transactionsList.sortBy { it.num }
                transactionsList.reverse()

                Log.d("Dashboard", "Transactions Load complete")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun getContacts() {
        databaseRef.child("contacts").child(currentUser.uid.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                contactList.clear()

                for (postSnapshot in snapshot.children) {
                    val cont = postSnapshot.getValue(Contact::class.java)
                    contactList.add(cont!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DashboardActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun setDashboard() {
        changeLayout(AllNotesFragment())
        getContacts()
        getTransactions()
        Picasso.get().load(currentUser.photo).into(profile_image_DA)
        balance_DA.text = currentUser.balance.toString() + " /="
        user_name_DA.text = "Welcome back " + currentUser.name
    }

    private fun changeLayout(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.layout_FG_DA, fragment)
        fragmentTransaction.commit()
    }
}