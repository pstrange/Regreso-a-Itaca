package com.regresoa.itaca.model.repositories

import com.google.firebase.database.DatabaseReference
import com.mandala.lib.network.RetrofitFactory
import com.regresoa.itaca.BuildConfig
import com.regresoa.itaca.model.entities.SearchResult
import com.regresoa.itaca.model.remote.booksapi.APIBooks
import com.regresoa.itaca.model.remote.firebase.FireBaseDB
import io.reactivex.Observable

/**
 * Created by just_ on 27/01/2019.
 */
class BooksRepository{

    companion object {
        lateinit var instance: BooksRepository
        lateinit var apiBooks: APIBooks
        lateinit var myLibrary: DatabaseReference
    }

    init {
        instance = this
        apiBooks = RetrofitFactory.retrofit(BuildConfig.GOOGLE_API_URL).create(APIBooks::class.java)
        myLibrary = FireBaseDB.myLibrary
    }

    fun searchISBN(isbn: String): Observable<SearchResult>{
        return apiBooks.searchISBN(isbn, 40.toString(), BuildConfig.API_KEY)
    }

    fun getMyBooks(): DatabaseReference{
        return myLibrary
    }
}