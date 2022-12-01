package com.altalha.personalfinance

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_first_login.*

class FirstLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_login)

        auth = FirebaseAuth.getInstance()

        login_Btn_FLA.setOnClickListener {
            val email = email_ET_FLA.text.toString()
            val pin = pin_ET_FLA.text.toString()

            if (email.isNotEmpty() && pin.isNotEmpty())
                login(email, pin)
            else
                Toast.makeText(this, "Enter email and pin to login", Toast.LENGTH_SHORT).show()
        }

        create_account_TV_FLA.setOnClickListener {
            startActivity((Intent(this, CreateAccountActivity::class.java)))
        }
    }

    private fun login(email: String, pin :String) {
        auth.signInWithEmailAndPassword(email, pin)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUser(auth.currentUser!!.uid)
                    gotoDashboard()
                }
                else
                    Toast.makeText(this, "Something is wrong", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveUser(uid : String) {
        val sharedPreferences = getSharedPreferences("SAVE_USER", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userUID", uid)
        editor.apply()
    }

    private fun gotoDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}