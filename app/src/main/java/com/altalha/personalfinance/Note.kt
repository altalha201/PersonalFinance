package com.altalha.personalfinance

import java.io.Serializable

class Note : Serializable {
    var noteID : String? = null
    var accountUID : String? = null
    var noteTitle : String? = null
    var noteDescription : String? = null
    var addedDate : String? = null

    constructor() {}

    constructor(
        noteID : String,
        accountUID : String,
        noteTitle : String,
        noteDescription : String,
        addedDate : String
    ) {
        this.noteID = noteID
        this.accountUID = accountUID
        this.noteTitle = noteTitle
        this.noteDescription = noteDescription
        this.addedDate = addedDate
    }
}