package com.regresoa.itaca.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.regresoa.itaca.model.entities.Book
import com.regresoa.itaca.model.entities.SearchResult
import com.regresoa.itaca.model.repositories.BooksRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by just_ on 27/01/2019.
 */
class BooksViewModel(private var repository: BooksRepository) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val searchResults = MutableLiveData<SearchResult>()
    val myBooks = MutableLiveData<Book>()
    val removedBook = MutableLiveData<Book>()

    fun searchISBN(isbn: String){
        repository.searchISBN(isbn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    isLoading.postValue(true)
                }
                .doOnTerminate {
                    isLoading.postValue(false)
                }
                .subscribe (
                    { result -> searchResults.postValue(result) },
                    { error.postValue(it.message) }
                )
    }

    fun getMyBooks(listener: ChildEvents){
        isLoading.postValue(true)
        repository.getMyBooks().addChildEventListener(listener)
    }

    fun searchMyBooks(value: String, listener: ChildEvents){
        isLoading.postValue(true)
        repository.getMyBooks().orderByChild("volumeInfo/industryIdentifiers/identifier").startAt(value).addChildEventListener(listener)
    }

    fun removeMyBooksListener(listener: ChildEvents){
        repository.getMyBooks().removeEventListener(listener)
    }

    fun addBookToMyLibrary(book: Book){
        repository.getMyBooks().child(book.id).setValue(book)
    }

    fun removeBookFromMyLibrary(book: Book){
        repository.getMyBooks().child(book.id).removeValue()
    }

    fun updateBookFromMyLibrary(book: Book){
        addBookToMyLibrary(book)
    }

    open abstract class ChildEvents: ChildEventListener{
        override fun onChildAdded(dataSnapshot: DataSnapshot, prevChildKey: String?) {
            val item = dataSnapshot.getValue(Book::class.java)
            item?.let {
                onChildLoaded(item)
            }
        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            val item = dataSnapshot.getValue(Book::class.java)
            item?.let {
                onChildDeleted(item)
            }
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, prevChildKey: String?) {
            val item = dataSnapshot.getValue(Book::class.java)
            item?.let {
                onChildUpdate(item)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w(TAG, "Failed to read value.", error.toException())
        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, prevChildKey: String?) {}

        abstract fun onChildLoaded(book: Book)

        abstract fun onChildDeleted(book: Book)

        abstract fun onChildUpdate(book: Book)
    }
}