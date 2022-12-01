package com.altalha.personalfinance

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ContactAdapter(val context: Context, private val activityClassType: String, private val list: ArrayList<Contact>) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.contact_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = list[position]

        Picasso.get().load(current.contactPhotoURL).into(holder.profileImage)
        holder.profileName.text = current.contactName

        holder.root.setOnClickListener {
            when (activityClassType) {
                "send" -> gotoTransactionActivity(current.userUID.toString())
                "myContacts" -> showDetails(current)
                else -> {
                    addUser(current)
//                    Toast.makeText(context, current.name, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = list.size

    private fun gotoTransactionActivity(receiverUID : String) {
        val intent = Intent(context, TransactionActivity::class.java)
        intent.putExtra("transactionType", "Send")
        intent.putExtra("receiver", receiverUID)
        ContextCompat.startActivity(context, intent, null)
    }

    @SuppressLint("SetTextI18n")
    private fun showDetails(user: Contact) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.delete_dialog_layout)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.animation

        val profileIV = dialog.findViewById<ImageView>(R.id.profile_image_DDL)
        val profileName = dialog.findViewById<TextView>(R.id.name_TV_DDL)
        val positionBtn = dialog.findViewById<Button>(R.id.positive_btn_DDL)
        val negativeBtn = dialog.findViewById<Button>(R.id.negative_btn_DDL)

        Picasso.get().load(user.contactPhotoURL).into(profileIV)
        profileName.text = "Do you want to delete {${user.contactName}}?"

        positionBtn.setOnClickListener{
            dialog.dismiss()
        }
        negativeBtn.setOnClickListener {
            deleteUser(user)
            dialog.dismiss()
            Toast.makeText(context, "Contact Deleted", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun addUser(user: Contact) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.add_dialog_layout)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.animation

        val profileIV = dialog.findViewById<ImageView>(R.id.profile_image_ADL)
        val profileName = dialog.findViewById<TextView>(R.id.name_TV_ADL)
        val profileEmail = dialog.findViewById<TextView>(R.id.email_TV_ADL)
        val positionBtn = dialog.findViewById<Button>(R.id.positive_btn_ADL)
        val negativeBtn = dialog.findViewById<Button>(R.id.negative_btn_ADL)

        Picasso.get().load(user.contactPhotoURL).into(profileIV)
        profileName.text = user.contactName
        profileEmail.text = user.contactEmail

        negativeBtn.setOnClickListener {
            dialog.dismiss()
        }

        positionBtn.setOnClickListener {
            addUserToContact(user)
            dialog.dismiss()
            Toast.makeText(context, "Contact Added", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    private fun deleteUser(user: Contact) {
        DashboardActivity.databaseRef.child("contacts").child(DashboardActivity.currentUser.uid.toString()).child(user.userUID.toString()).removeValue()
        DashboardActivity.contactList.remove(user)
        list.remove(user)
        notifyDataSetChanged()
    }

    private fun addUserToContact(user: Contact) {
        DashboardActivity.databaseRef.child("contacts").child(DashboardActivity.currentUser.uid.toString()).child(user.userUID.toString()).setValue(user)
        DashboardActivity.contactList.add(user)
        list.remove(user)
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val profileImage : ImageView = itemView.findViewById(R.id.profile_image_CA)
        val profileName : TextView = itemView.findViewById(R.id.profile_name_CA)
        val root : LinearLayout = itemView.findViewById(R.id.root_contact_layout)
    }
}