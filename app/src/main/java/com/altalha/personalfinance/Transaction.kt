package com.altalha.personalfinance

import java.io.Serializable

class Transaction : Serializable {
    var transactionId : String? = null
    var accountUID : String? = null
    var transactionType : String? = null
    var transactionDescription : String? = null
    var amount : Double? = null
    var date : String? = null
    var num : Int? = null

    constructor()

    constructor(
        transactionId: String,
        accountUID: String,
        transactionType: String,
        transactionDescription: String,
        amount: Double,
        date: String,
        num : Int
    ) {
        this.transactionId = transactionId
        this.accountUID = accountUID
        this.transactionType = transactionType
        this.transactionDescription = transactionDescription
        this.amount = amount
        this.date = date
        this.num = num
    }
}