package com.regresoa.itaca.model.repositories

import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.mandala.lib.network.RetrofitFactory
import com.regresoa.itaca.BuildConfig
import com.regresoa.itaca.model.entities.SearchResult
import com.regresoa.itaca.model.remote.booksapi.APIBooks
import com.regresoa.itaca.model.remote.firebase.FireBaseDB
import io.reactivex.Observable
import java.io.File

/**
 * Created by just_ on 27/01/2019.
 */
class BooksRepository{

    companion object {
        lateinit var instance: BooksRepository
        lateinit var apiBooks: APIBooks
        lateinit var myLibrary: DatabaseReference
        lateinit var myFiles: StorageReference
    }

    init {
        instance = this
        apiBooks = RetrofitFactory.retrofit(BuildConfig.GOOGLE_API_URL).create(APIBooks::class.java)
        myLibrary = FireBaseDB.myLibrary
        myFiles = FireBaseDB.myFiles
    }

    fun searchISBN(isbn: String): Observable<SearchResult>{
        return apiBooks.searchISBN(isbn, 40.toString(), BuildConfig.API_KEY)
    }

    fun getMyBooks(): DatabaseReference{
        return myLibrary
    }

    fun uploadFile(name: String, file: File): Task<Uri>{
        val ref = myFiles.child(name)
        return ref.putFile(Uri.fromFile(file)).continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        })
    }

    fun deleteFile(name: String): Task<Void>{
        return myFiles.child(name).delete()
    }
}