package com.regresoa.itaca.model.remote.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.regresoa.itaca.BuildConfig

/**
 * Created by just_ on 10/02/2019.
 */
class FireBaseDB{

    companion object {
        private val database = FirebaseDatabase.getInstance()
        private val files = FirebaseStorage.getInstance()
        val myLibrary = database.getReference(BuildConfig.FIREBASE_DBNAME)
        val myFiles = files.getReference(BuildConfig.FIREBASE_MEDIA)
    }

}