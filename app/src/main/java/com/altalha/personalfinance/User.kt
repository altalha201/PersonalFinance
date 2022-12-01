package com.altalha.personalfinance

import java.io.Serializable

class User : Serializable {
    var uid: String? = null
    var name: String? = null
    var gender: String? = null
    var email: String? = null
    var photo: String? = null
    var password: String? = null
    var balance: Double? = null

    constructor()

    constructor(uid : String?, name : String?, gender: String?, email : String?, photo : String?, password : String?, balance : Double) {
        this.uid = uid
        this.name = name
        this.gender = gender
        this.email = email
        this.photo = photo
        this.password = password
        this.balance = balance
    }
}