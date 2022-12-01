package com.altalha.personalfinance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.altalha.personalfinance.databinding.FragmentTransectionsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_transections.*

class TransactionsFragment : Fragment() {

    private val spinnerList = arrayListOf("All", "Deposit", "Withdraw", "Receive", "Send")

    private lateinit var adapter : TransactionAdapter
    private lateinit var showList : ArrayList<Transaction>

    private var _binding : FragmentTransectionsBinding? = null
    private val bind get() = _binding

    lateinit var type : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTransectionsBinding.inflate(inflater, container, false)

        showList = ArrayList()
        adapter = TransactionAdapter(requireContext(), showList)

        val sortAdapter = ArrayAdapter(requireContext(), R.layout.support_simple_spinner_dropdown_item, spinnerList)

        bind!!.sortTF.adapter = sortAdapter
        bind!!.sortTF.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                type = spinnerList[position]
                updateShowList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        bind!!.transactionsRVTF.layoutManager = LinearLayoutManager(requireContext())
        bind!!.transactionsRVTF.adapter = adapter

        return bind!!.root
    }

    private fun updateShowList() {
        showList.clear()

        if (type == "All") {
            showList.addAll(DashboardActivity.transactionsList)
        } else {
            for (trans in DashboardActivity.transactionsList) {
                if (type == trans.transactionType) {
                    showList.add(trans)
                }
            }
        }
        adapter.notifyDataSetChanged()
    }
}