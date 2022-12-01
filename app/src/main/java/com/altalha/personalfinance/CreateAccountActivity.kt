package com.altalha.personalfinance

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_account.*

class CreateAccountActivity : AppCompatActivity() {

    private val genderSPItems = arrayOf("Male", "Female", "Others")
    lateinit var gender : String
    var selectedImageURI : Uri? = null
    private var imageSelected = false

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference

        val genderAdapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, genderSPItems)

        gender_SP_SA.adapter = genderAdapter
        gender_SP_SA.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gender = genderSPItems[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        profile_pic_IV_SA.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        login_Btn_SA.setOnClickListener {
            startActivity(Intent(this, FirstLoginActivity::class.java))
            finish()
        }

        create_Btn_SA.setOnClickListener {
            if (allFilled()) {
                signup(name_ET_SA.text.toString(), email_ET_SA.text.toString(), pin_ET_SA.text.toString())
            }
            else
                Toast.makeText(this, "Enter all information to create new account", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageURI = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageURI)
            profile_pic_IV_SA.setImageBitmap(bitmap)
            imageSelected = true
        }
    }

    private fun signup(name: String, email: String, pin: String) {
        auth.createUserWithEmailAndPassword(email, pin)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    uploadImageToStorage(name, email, pin, auth.currentUser!!.uid)
                }
                else
                    Toast.makeText(this, "Some Error occurred", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToStorage(name: String, email: String, pin: String, uid: String) {
        if (selectedImageURI == null) return

        var url : String?
        val filename = "profileImage_{$uid}"

        val ref = FirebaseStorage.getInstance().getReference("/profile_image/$filename")
        ref.putFile(selectedImageURI!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    url = it.toString()
                    val user = User(uid, name, gender, email, url, pin, 0.0)
                    addUserToDatabase(user)
                }
            }
    }

    private fun addUserToDatabase(user: User) {
        databaseReference.child("user").child(user.uid.toString()).setValue(user)
        gotoDashboard()
    }

    private fun gotoDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun allFilled() : Boolean {
        if (name_ET_SA.text.toString().isEmpty())
            return false
        if (email_ET_SA.text.toString().isEmpty())
            return false
        if (gender.isEmpty())
            return false
        if (pin_ET_SA.text.toString().isEmpty())
            return false
        if (!imageSelected)
            return false

        return true
    }
}