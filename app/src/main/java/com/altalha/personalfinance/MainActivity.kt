package com.altalha.personalfinance

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var databaseRef: DatabaseReference

    lateinit var pin : String
    lateinit var email : String
    private var enteredPin: String = ""
    var previous : Int = 0


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        getCurrentUser(intent.getStringExtra("savedUID").toString())


        pin_1_Btn_MA.setOnClickListener(this)
        pin_2_Btn_MA.setOnClickListener(this)
        pin_3_Btn_MA.setOnClickListener(this)
        pin_4_Btn_MA.setOnClickListener(this)
        pin_5_Btn_MA.setOnClickListener(this)
        pin_6_Btn_MA.setOnClickListener(this)
        pin_7_Btn_MA.setOnClickListener(this)
        pin_8_Btn_MA.setOnClickListener(this)
        pin_9_Btn_MA.setOnClickListener(this)
        pin_0_Btn_MA.setOnClickListener(this)

        backspace_Btn_MA.setOnClickListener {
            enteredPin = enteredPin.dropLast(1)
            updatePINLayout(false)
        }

        first_login_page_TV_MA.setOnClickListener {
            startActivity(Intent(this, FirstLoginActivity::class.java))
            finish()
        }
        enter_btn_MA.setOnClickListener {
            if (enteredPin == pin)
                login(email, pin)
            else
                Toast.makeText(this, "Wrong PIN", Toast.LENGTH_SHORT).show()
        }
    }

    private fun String.updatePIN() {
        if (previous in 0..6) {
            enteredPin += this
            updatePINLayout(true)
        }
    }

    @SuppressLint("ResourceType")
    fun updatePINLayout(increase : Boolean) {
        if (increase) {
            when (previous) {
                0 -> pin_1_MA.visibility = View.VISIBLE
                1 -> pin_2_MA.visibility = View.VISIBLE
                2 -> pin_3_MA.visibility = View.VISIBLE
                3 -> pin_4_MA.visibility = View.VISIBLE
                4 -> pin_5_MA.visibility = View.VISIBLE
                5 -> pin_6_MA.visibility = View.VISIBLE
            }
            previous ++
        }
        else {
            when (previous) {
                1 -> pin_1_MA.visibility = View.GONE
                2 -> pin_2_MA.visibility = View.GONE
                3 -> pin_3_MA.visibility = View.GONE
                4 -> pin_4_MA.visibility = View.GONE
                5 -> pin_5_MA.visibility = View.GONE
                6 -> pin_6_MA.visibility = View.GONE
            }
            if (previous > 0)
                previous --
        }

    }

    private fun login(e : String, p: String) {
        auth.signInWithEmailAndPassword(e, p)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else {
                    Toast.makeText(this, "User dose not exist", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getCurrentUser(uid: String) {
        databaseRef.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var user : User? = null
                var userFound = false
                for (postSnapshot in snapshot.children) {
                    user = postSnapshot.getValue(User::class.java)
                    if (uid == user?.uid) {
                        userFound = true
                        break
                    }
                }
                if (userFound)
                    setLayout(user!!)
            }

            override fun onCancelled(error: DatabaseError) {
                val intent = Intent(this@MainActivity, FirstLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun setLayout(u : User) {
        Picasso.get().load(u.photo).into(user_pc_IV_MA)
        user_name_TV_MA.text = "Welcome! ${u.name}"
        first_login_page_TV_MA.text = "Not ${u.name}, Login to another account"
        pin = u.password.toString()
        email = u.email.toString()
    }

    override fun onClick(v: View?) {
        val b = v as Button
        b.text.toString().updatePIN()
    }
}