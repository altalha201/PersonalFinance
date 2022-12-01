package com.altalha.personalfinance

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_about.*

class About : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        version_txt.text = "Personal Finance ${BuildConfig.VERSION_NAME}"

        aboutText.text = text

    }

    private val text = "Personal Finance is a project application made with Kotlin android, for the " +
            "course 'Pervasive Computing and Mobile Application Development'." +
            "\nProject submitted to, Mohammad Jahangir Alam (Senior Lecturer Department of CSE, Daffodil International University)." +
            "\nSubmitted by, Md. Motasaddique Al Talha, ID: 201-15-3482." +
            "\nSection: PC-B, Department of CSE, Daffodil International University."
}