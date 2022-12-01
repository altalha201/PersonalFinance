package com.altalha.personalfinance

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class Today {

    @SuppressLint("SimpleDateFormat")
    fun date() : String {
        val currentDate = SimpleDateFormat("dd").format(System.currentTimeMillis()).toInt()
        val currentMonth = SimpleDateFormat("MM").format(System.currentTimeMillis()).toInt()
        val currentYear = SimpleDateFormat("YYYY").format(System.currentTimeMillis()).toInt()

        val currentMonthString = setMonth(currentMonth - 1)

        return "$currentMonthString $currentDate, $currentYear"
    }

    private fun setMonth(month: Int) : String {
        val monthString : String = when (month) {
            0 -> "January"
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            else -> "December"
        }
        return monthString
    }
}