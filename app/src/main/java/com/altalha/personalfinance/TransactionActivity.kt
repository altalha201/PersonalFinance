package com.altalha.personalfinance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_transaction.*
import java.util.*
import kotlin.collections.HashMap

class TransactionActivity : AppCompatActivity() {

    private lateinit var receiver : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        val transactionType : String? = intent.getStringExtra("transactionType")
        val transactionNumber : Int = DashboardActivity.transactionsList.size

        title_TA.text = transactionType
        transaction_Btn_TA.text = transactionType
        if (transactionType.equals("Send")) {
            getReceiver(intent.getStringExtra("receiver").toString())
        }

        transaction_Btn_TA.setOnClickListener {
            val amount = amount_TA.text.toString()
            val pin = pin_TA.text.toString()

            if (amount.isNotEmpty() && pin.isNotEmpty()) {
                val userPin = DashboardActivity.currentUser.password
                if (userPin == pin) {
                    selectTransactionMethod(transactionType!!, transactionNumber, amount.toDouble())
                }
                else {
                    Toast.makeText(this, "Wrong PIN, try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun selectTransactionMethod(type : String, num : Int, amount : Double) {
        when (type) {
            "Deposit" -> deposit(num, amount)
            "Withdraw" -> withdraw(num, amount)
            else -> sendMoney(num, amount)
        }
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun deposit(num: Int, amount: Double) {
        val newAmount = DashboardActivity.currentUser.balance!!.toDouble() + amount
        DashboardActivity.currentUser.balance = newAmount
        updateToDatabase()
        addToTransactions("Deposit", num, amount)
    }

    private fun withdraw(num: Int, amount: Double) {
        if (amount <= DashboardActivity.currentUser.balance!!.toDouble()) {
            val newAmount = DashboardActivity.currentUser.balance!!.toDouble() - amount
            DashboardActivity.currentUser.balance = newAmount
            updateToDatabase()
            addToTransactions("Withdraw", num, amount)
        }
        else {
            Toast.makeText(this, "Insufficient Balance", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendMoney(num: Int, amount: Double) {

        Log.d("SendingTransaction", "Arrived at sendMoney")

        if (amount <= DashboardActivity.currentUser.balance!!.toDouble()) {
            val senderNewAmount = DashboardActivity.currentUser.balance!! - amount
            val receiverNewAmount = receiver.balance!! + amount

            DashboardActivity.currentUser.balance = senderNewAmount
            receiver.balance = receiverNewAmount
            updateToDatabase()
            val update = HashMap<String, Double>()
            update["balance"] = receiver.balance!!.toDouble()
            DashboardActivity.databaseRef.child("user").child(receiver.uid.toString()).updateChildren(update as Map<String, Any>)

            addToBothTransaction(getReceiverNum(), num, amount)
        }
        else
            Toast.makeText(this, "Insufficient Balance", Toast.LENGTH_SHORT).show()
    }

    private fun getReceiverNum() : Int {
        Log.d("SendingTransaction", "Arrived at getReceiverNumber")
        var count = 0

        DashboardActivity.databaseRef.child("transactions").child(receiver.uid.toString()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                count = snapshot.children.count()
                Log.d("SendingTransaction", "Count collected")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        return count
    }

    private fun getReceiver(uid : String){
        Log.d("SendingTransaction", "Arrived at getReceiver")
        DashboardActivity.databaseRef.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)
                    if (user!!.uid == uid) {
                        receiver = user
                        Log.d("SendingTransaction", "Found Receiver")
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun updateToDatabase() {
        val newBalance = HashMap<String, Double>()
        newBalance["balance"] = DashboardActivity.currentUser.balance!!
        DashboardActivity.databaseRef.child("user").child(DashboardActivity.currentUser.uid.toString()).updateChildren(newBalance as Map<String, Any>)
        Log.d("SendingTransaction", "Database updated for sender")
    }

    private fun addToTransactions(type: String, num: Int, amount: Double) {
        val description =  description_TA.text.toString()
        val date = Today().date()
        val id = UUID.randomUUID().toString()

        val transaction = Transaction(id, DashboardActivity.currentUser.uid.toString(), type, description, amount, date, num)

        DashboardActivity.databaseRef.child("transactions").child(transaction.accountUID.toString()).child(transaction.transactionId.toString())
            .setValue(transaction)
    }

    private fun addToBothTransaction(receiverCount : Int, senderCount : Int, amount: Double) {
        Log.d("SendingTransaction", "Arrived at addTo Transactions")
        val description = description_TA.text.toString()
        val date = Today().date()
        val id = UUID.randomUUID().toString()

        val senderTransaction = Transaction(id, DashboardActivity.currentUser.uid.toString(), "Send", description, amount, date, senderCount)
        val receiverTransaction = Transaction(id, receiver.uid.toString(), "Receive", description, amount, date, receiverCount)

        DashboardActivity.databaseRef.child("transactions").child(senderTransaction.accountUID.toString()).child(senderTransaction.transactionId.toString())
            .setValue(senderTransaction)
        DashboardActivity.databaseRef.child("transactions").child(receiverTransaction.accountUID.toString()).child(receiverTransaction.transactionId.toString())
            .setValue(receiverTransaction)

        Log.d("SendingTransaction", "Sending Finished")
    }
}