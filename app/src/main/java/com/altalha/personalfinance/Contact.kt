package com.altalha.personalfinance

import java.io.Serializable

class Contact : Serializable {
    var userUID : String? = null
    var contactName: String? = null
    var contactGender: String? = null
    var contactPhotoURL: String? = null
    var contactEmail: String? = null

    constructor()

    constructor(userUID : String, contactName : String, contactGender : String, contactPhotoURL : String, contactEmail : String) {
        this.userUID = userUID
        this.contactEmail = contactEmail
        this.contactName = contactName
        this.contactGender = contactGender
        this.contactPhotoURL = contactPhotoURL
    }
}