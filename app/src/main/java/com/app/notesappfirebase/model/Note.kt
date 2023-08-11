package com.app.notesappfirebase.model

import com.google.firebase.firestore.PropertyName

data class Note(
    @PropertyName("title") var title: String,
    @PropertyName("body") var body: String
) {
    constructor() : this("", "")
}

