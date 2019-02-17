package com.regresoa.itaca.model.remote.booksapi

import com.regresoa.itaca.model.entities.SearchResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by just_ on 27/01/2019.
 */
interface APIBooks{

    @GET("volumes")
    fun searchISBN(@Query("q") isbn: String, @Query("maxResults") maxResults: String, @Query("key") key: String) : Observable<SearchResult>

}