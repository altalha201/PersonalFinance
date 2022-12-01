package com.altalha.personalfinance

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(val context: Context, private val list: ArrayList<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.transaction_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = list[position]

        holder.date.text = current.date.toString()

        when (current.transactionType) {
            "Deposit" -> {
                setForIncoming(holder, current.transactionType!!, current.amount!!)
            }
            "Withdraw" -> {
                setForOutgoing(holder, current.transactionType!!, current.amount!!)
            }
            "Send" -> {
                setForOutgoing(holder, current.transactionType!!, current.amount!!)
            }
            "Receive" -> {
                setForIncoming(holder, current.transactionType!!, current.amount!!)
            }
        }
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("SetTextI18n")
    private fun setForIncoming(holder: ViewHolder, title : String, amount: Double) {
        holder.photo.setImageResource(R.drawable.ic_in_coming)
        holder.title.text = title
        holder.amount.text = "+ ${String.format("%.02f", amount)}"
        holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
    }

    @SuppressLint("SetTextI18n")
    private fun setForOutgoing(holder: ViewHolder, title : String, amount: Double) {
        holder.photo.setImageResource(R.drawable.ic_out_going)
        holder.title.text = title
        holder.amount.text = "- ${String.format("%.02f", amount)}"
        holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val photo: ImageView = itemView.findViewById(R.id.image_TL)
        val title: TextView = itemView.findViewById(R.id.title_TL)
        val date: TextView = itemView.findViewById(R.id.date_TL)
        val amount: TextView = itemView.findViewById(R.id.amount_TL)
        val root: RelativeLayout = itemView.findViewById(R.id.root_transaction)
    }
}