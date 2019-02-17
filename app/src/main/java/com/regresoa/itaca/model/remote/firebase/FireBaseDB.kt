package com.regresoa.itaca.model.remote.firebase

import com.google.firebase.database.FirebaseDatabase

/**
 * Created by just_ on 10/02/2019.
 */
class FireBaseDB{

    companion object {
        private val database = FirebaseDatabase.getInstance()
        val myLibrary = database.getReference("mylibrary")
    }

}